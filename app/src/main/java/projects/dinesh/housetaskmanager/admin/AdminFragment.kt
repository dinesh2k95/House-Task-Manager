package projects.dinesh.housetaskmanager.admin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.applandeo.materialcalendarview.CalendarView.MANY_DAYS_PICKER
import com.applandeo.materialcalendarview.CalendarView.ONE_DAY_PICKER
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.admin_task_line_item.view.*
import kotlinx.android.synthetic.main.fragment_admin.*
import kotlinx.android.synthetic.main.task_line_item.view.*

import projects.dinesh.housetaskmanager.R
import projects.dinesh.housetaskmanager.task.Task
import projects.dinesh.housetaskmanager.task.TaskViewHolder
import projects.dinesh.housetaskmanager.task.User
import projects.dinesh.housetaskmanager.task.UserTask
import projects.dinesh.housetaskmanager.utils.Utils.TASK_DATE_FORMAT
import projects.dinesh.housetaskmanager.utils.Utils.formatGivenDateForPattern
import java.util.*
import kotlin.collections.ArrayList

class AdminFragment : Fragment() {

    private var usersDbReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private var taskDbReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("tasks")
    public lateinit var taskAdapter: AdminTaskAdapter
    private lateinit var selectedDate: String
    private lateinit var selectedUser: User
    private var userPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUserList()
        initializeDatePicker()
    }

    private fun initializeUserList() {
        usersDbReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val typeIndicator = object : GenericTypeIndicator<java.util.ArrayList<User>>() {}
                val userList: java.util.ArrayList<User> = p0.getValue(typeIndicator)!!
                userSpinner.adapter = ArrayAdapter(this@AdminFragment.context, android.R.layout.simple_spinner_item, userList.map { it.name })
                userSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        userPosition = position
                        selectedUser = userList[userPosition]
                        println("Selected User : $selectedUser")
                        initializeDatePicker()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })
    }

    private fun initializeDatePicker() {
        selectDate.visibility = VISIBLE
        selectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerBuilder = DatePickerBuilder(activity, OnSelectDateListener { calendars ->
                selectedDate = formatGivenDateForPattern(calendar.timeInMillis, TASK_DATE_FORMAT)
                initializeTasks()
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
        val userTasks = selectedUser.taskIds.filter { it.date == selectedDate } as java.util.ArrayList<UserTask>
        println("userTasks :::: $userTasks")
        taskAdapter = AdminTaskAdapter(userTasks)
        taskAdapter.userTasks = userTasks
        taskAdapter.notifyDataSetChanged()
    }

    inner class AdminTaskAdapter(var userTasks: java.util.ArrayList<UserTask>) : RecyclerView.Adapter<AdminTaskViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminTaskViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.task_line_item, parent, false)
            return AdminTaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: AdminTaskViewHolder, position: Int) {
            val userTask = userTasks[position]
            taskDbReference.child(userTask.taskId.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val task = dataSnapshot.getValue(Task::class.java)!!
                        val taskView = holder.taskView
                        holder.taskView.isSelected = userTask.completed

                        taskView.text = task.name
                        taskView.setOnLongClickListener {
                            Toast.makeText(this@AdminFragment.context, "Delete this Item ??", Toast.LENGTH_LONG).show()
//                            usersDbReference.child("$userPosition").child("taskIds/${holder.adapterPosition}").setValue(null)
                            true
                        }
                        holder.taskView.setOnClickListener {

//                                .addOnSuccessListener { println("User update Successful !") }
//                                .addOnFailureListener { exception -> println("Toggle UnSuccessful ::: $exception") }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
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
