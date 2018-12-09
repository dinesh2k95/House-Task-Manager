package projects.dinesh.housetaskmanager.task

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.task_line_item.view.*
import projects.dinesh.housetaskmanager.R
import projects.dinesh.housetaskmanager.utils.AppPreferences
import projects.dinesh.housetaskmanager.utils.Utils.TASK_DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*

class TaskViewModel : ViewModel() {

    private var usersDbReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private var taskDbReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("tasks")
    lateinit var taskAdapter: TaskAdapter
    lateinit var welcomeMessage: String
    private lateinit var toast: Toast
    private var userPosition = 0
    var points: MutableLiveData<String> = MutableLiveData()
    var status: MutableLiveData<String> = MutableLiveData()

    fun init(context: Context) {
        toast = Toast(context)
        taskAdapter = TaskAdapter(arrayListOf())
        val userName = AppPreferences.getString(context, "userName", "default")
        welcomeMessage = "Welcome $userName !"
        usersDbReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val typeIndicator = object : GenericTypeIndicator<ArrayList<User>>() {}
                val arrayList: ArrayList<User> = p0.getValue(typeIndicator)!!
                val simpleDateFormat = SimpleDateFormat(TASK_DATE_FORMAT)
                val todayDate: String = simpleDateFormat.format(Date())

                val user = arrayList.filterIndexed { index, user ->
                    userPosition = index
                    user.name == userName
                }[0]
                user.taskIds = user.taskIds.filter { it.date == todayDate } as ArrayList<UserTask>
                if (user.taskIds.isEmpty()) {
                    status.value = "No Tasks"
                } else {
                    status.value = "Tasks loaded"
                    val userTasks = user.taskIds
                    taskAdapter.userTasks = userTasks
                    taskAdapter.notifyDataSetChanged()
                    calculatePoints(userTasks)
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    inner class TaskAdapter(var userTasks: ArrayList<UserTask>) : RecyclerView.Adapter<TaskViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.task_line_item, parent, false)
            return TaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val userTask = userTasks[position]
            taskDbReference.child(userTask.taskId.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val task = dataSnapshot.getValue(Task::class.java)!!
                        val taskView = holder.taskView
                        if (userTask.completed) {
                            taskView.isChecked = true
                        }
                        taskView.text = task.name
                        holder.taskView.setOnClickListener {
                            taskView.toggle()
                            userTask.completed = taskView.isChecked
                            calculatePoints(userTasks)
                            usersDbReference.child("$userPosition").child("taskIds/${holder.adapterPosition}").setValue(userTask)
                                .addOnSuccessListener { println("User update Successful !") }
                                .addOnFailureListener { exception -> println("Toggle UnSuccessful ::: $exception") }
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

    private fun calculatePoints(userTasks: ArrayList<UserTask>) {
        var totalPoints = 0
        val singleTaskPoints = 10 / userTasks.size
        userTasks.filter { it.completed }.forEach {
            totalPoints += singleTaskPoints
        }
        points.value = totalPoints.toString()
    }
}

class TaskViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val taskView: CheckedTextView = view.checked_task_item
}

data class  User(var name: String = "", var taskIds: ArrayList<UserTask> = arrayListOf())
data class UserTask(var taskId: Int = 0, var date:String = "", var completed: Boolean = false)
data class Task(var name: String = "", var completed: Boolean = false)