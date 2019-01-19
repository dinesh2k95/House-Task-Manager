@file:Suppress("UNCHECKED_CAST")

package projects.dinesh.housetaskmanager.admin

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.*
import com.applandeo.materialcalendarview.CalendarView.ONE_DAY_PICKER
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import com.gofrugal.sellquick.atslibrary.CustomToast
import org.jetbrains.anko.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_admin.*
import kotlinx.android.synthetic.main.task_line_item.view.*
import org.jetbrains.anko.sdk27.coroutines.onItemSelectedListener
import org.jetbrains.anko.support.v4.longToast
import projects.dinesh.housetaskmanager.R
import projects.dinesh.housetaskmanager.databinding.FragmentAdminBinding
import projects.dinesh.housetaskmanager.task.Task
import projects.dinesh.housetaskmanager.task.User
import projects.dinesh.housetaskmanager.task.UserTask
import projects.dinesh.housetaskmanager.utils.Utils.TASK_DATE_FORMAT
import projects.dinesh.housetaskmanager.utils.Utils.formatGivenDateForPattern
import java.util.*
import kotlin.collections.ArrayList

class AdminFragment : Fragment() {

    private var usersDbReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private var taskDbReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("tasks")
    private val customToast: CustomToast = CustomToast(context)
    private lateinit var selectedDate: String
    private lateinit var selectedUser: User
    private var userPosition: Int = 0
    private lateinit var binding: FragmentAdminBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin, container, false)
        binding.taskAdapter = AdminTaskAdapter(arrayListOf())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUserList()
        initializeDatePicker {
            selectedDate = formatGivenDateForPattern(it.first().timeInMillis, TASK_DATE_FORMAT)
            initializeTasks()
        }
    }

    private fun initializeUserList() {
        usersDbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val typeIndicator = object : GenericTypeIndicator<java.util.ArrayList<User>>() {}
                val userList: java.util.ArrayList<User> = p0.getValue(typeIndicator)!!
                if (::selectedUser.isInitialized && ::selectedDate.isInitialized) {
                    selectedUser = userList[userPosition]
                    refreshTasks()
                    return
                }
                userSpinner.adapter = ArrayAdapter(this@AdminFragment.context, R.layout.spinner_line_item, userList.map { it.name })
                userSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        userPosition = position
                        selectedUser = userList[userPosition]
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun initializeDatePicker(onSuccess: (List<Calendar>) -> Unit) {
        selectDate.visibility = VISIBLE
        selectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerBuilder = DatePickerBuilder(activity, OnSelectDateListener { calendars ->
                onSuccess(calendars)
            })

            datePickerBuilder.pickerType(ONE_DAY_PICKER)
                .date(calendar)
                .headerColor(R.color.colorPrimary)
                .selectionColor(R.color.colorPrimary)
                .dialogButtonsColor(R.color.colorPrimaryDark)
                .todayLabelColor(R.color.colorPrimaryDark)
                .build()
                .show()
        }
    }

    private fun initializeTasks() {
        adminTaskList.layoutManager = LinearLayoutManager(context)
        adminTaskList.visibility = VISIBLE
        refreshTasks()
        addTask.setOnClickListener(addTaskListener())
        cloneTasks.setOnClickListener {
            initializeDatePicker {calendars ->
                val cloneTasksDate = formatGivenDateForPattern(calendars.first().timeInMillis, TASK_DATE_FORMAT)
                val userTasks = selectedUser.taskIds.filterNotNull().filter { it.date == cloneTasksDate }
                    .map { userTask ->
                        val newUserTask = userTask.copy()
                        newUserTask.date = cloneTasksDate
                        newUserTask } as ArrayList
                selectedUser.taskIds.addAll(userTasks)

                usersDbReference.child("$userPosition").child("taskIds").setValue(selectedUser.taskIds)
                    .addOnSuccessListener {
                        customToast.infoToast("Copy Successful !")
                        refreshTasks()
                    }
                    .addOnFailureListener {
                        customToast.alertToast("Copy UnSuccessful !")
                    }
            }
        }
    }

    private fun addTaskListener(): (View) -> Unit {
        return { view ->
            taskDbReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val typeIndicator = object : GenericTypeIndicator<java.util.ArrayList<Task?>>() {}
                    var taskList: ArrayList<Task?> = p0.getValue(typeIndicator)!!

                    val taskNameList: ArrayList<String> = taskList.filterNotNull().map { it.name } as ArrayList
                    taskNameList.add("New Task")
                    lateinit var editText: EditText
                    lateinit var spinner: Spinner

                    AlertDialog.Builder(activity!!)
                        .setTitle("Add new Task")
                        .setView(with(view.context) {
                            linearLayout {
                            orientation = LinearLayout.VERTICAL
                            editText = editText {
                                visibility = GONE
                                hint = "Enter New task Name"
                            }.lparams {
                                width = MATCH_PARENT
                                margin = 10}
                            spinner = spinner {
                                adapter = ArrayAdapter(this@AdminFragment.context, R.layout.spinner_line_item, taskNameList)
                                onItemSelectedListener {
                                    onItemSelected { adapterView, view, i, l ->
                                        if (taskNameList[i] == "New Task") {
                                            editText.visibility = VISIBLE
                                        }
                                    }
                                }
                            }.lparams {width = MATCH_PARENT
                                margin = 10}
                        }})
                        .setPositiveButton("Done") { i, j ->
                            lateinit var task: UserTask
                            task = if (editText.visibility == VISIBLE) {
                                val newTask = Task("${editText.text}", false)
                                taskList.add(newTask)
                                taskList = taskList.filterNotNull() as ArrayList<Task?>
                                taskDbReference.setValue(taskList)
                                UserTask(taskList.size - 1, selectedDate, false)
                            } else {
                                UserTask(spinner.selectedItemPosition, selectedDate, false)
                            }
                            selectedUser.taskIds.add(task)
                            usersDbReference.child("$userPosition").child("taskIds").setValue(selectedUser.taskIds)
                                .addOnSuccessListener { CustomToast(context).infoToast("New Task Added successfully !") }
                                .addOnFailureListener { CustomToast(context).errorToast("Error while adding new Task") }
                        }.show()
                }
                override fun onCancelled(p0: DatabaseError) {}
            })
        }
    }

    private fun refreshTasks(date: String = selectedDate) {
        val userTasks = selectedUser.taskIds.filterNotNull().filter { it.date == date } as java.util.ArrayList<UserTask>
        println("Refreshing userTasks :::: $userTasks")
        (binding.taskAdapter as AdminTaskAdapter).userTasks = userTasks
        (binding.taskAdapter as AdminTaskAdapter).notifyDataSetChanged()
    }

    inner class AdminTaskAdapter(var userTasks: java.util.ArrayList<UserTask>) :
        RecyclerView.Adapter<AdminTaskViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminTaskViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.task_line_item, parent, false)
            return AdminTaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: AdminTaskViewHolder, position: Int) {
            val userTask = userTasks[position]
            taskDbReference.child(userTask.taskId.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val task = dataSnapshot.getValue(Task::class.java)
                        val taskView = holder.taskView
                        if (userTask.completed) {
                            taskView.isSelected = true
                        }
                        taskView.text = task?.name
                        taskView.setOnLongClickListener {
                            Toast.makeText(this@AdminFragment.context, "Deleted Item", Toast.LENGTH_LONG).show()
                            selectedUser.taskIds.indexOf(userTask)
                            usersDbReference.child("$userPosition").child("taskIds/${selectedUser.taskIds.indexOf(userTask)}").setValue(null)
                            true
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {}
                })
        }

        override fun getItemCount(): Int {
            return userTasks.size
        }
    }

    class AdminTaskViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val taskView: TextView = view.checked_task_item
    }

    companion object {
        @JvmStatic
        fun newInstance() = AdminFragment()
    }
}
