package projects.dinesh.housetaskmanager

import android.arch.lifecycle.ViewModel
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.task_line_item.view.*

class TaskViewModel : ViewModel() {

    private var usersDbReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private var taskDbReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("tasks")
    lateinit var taskAdapter: TaskAdapter

    fun init() {
        taskAdapter = TaskAdapter(arrayListOf())
        usersDbReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val typeIndicator = object : GenericTypeIndicator<ArrayList<User>>() {}
                val arrayList: ArrayList<User> = p0.getValue(typeIndicator)!!
                val user = arrayList.filter { it.name == "Dinesh" }[0]
                taskAdapter.userTasks = user.taskIds
                taskAdapter.notifyDataSetChanged()
            }
        })
    }

    inner class TaskAdapter(var userTasks: ArrayList<UserTask>) :
        RecyclerView.Adapter<TaskViewHolder>()

    {
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
                        val taskName = holder.taskName
                        if (task.completed) {
                            taskName.isChecked = true
                        }
                        taskName.text = task.name
                        holder.taskName.setOnClickListener {
                            taskName.toggle()
                            userTask.completed = taskName.isChecked
                            taskDbReference.child("${holder.adapterPosition}").setValue(userTask)
                                .addOnSuccessListener { println("Toggle Successful !") }
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

}

class TaskViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val taskName: CheckedTextView = view.checked_task_item
}

data class  User(var name: String = "", var taskIds: ArrayList<UserTask> = arrayListOf())
data class UserTask(var taskId: Int = 0, var completed: Boolean = false)
data class Task(var name: String = "", var completed: Boolean = false)