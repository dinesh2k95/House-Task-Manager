package projects.dinesh.housetaskmanager

import android.arch.lifecycle.ViewModel
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.task_line_item.view.*

class TaskViewModel : ViewModel() {

    private var dataBaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("tasks")
    lateinit var taskAdapter: TaskAdapter


    fun init(): TaskAdapter {
        val options = FirebaseRecyclerOptions.Builder<String>()
            .setQuery(dataBaseReference, String::class.java)
            .build()
        dataBaseReference.addValueEventListener(
            object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    println("Database value :::: $p0")
                }
            }
        )
        taskAdapter = TaskAdapter(options)
        return taskAdapter
    }
}

class TaskAdapter(options: FirebaseRecyclerOptions<String>): FirebaseRecyclerAdapter<String, TaskViewHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.task_line_item, parent, false)
            return TaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int, model: String) {
            holder.taskName.text = model
        }
}

class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val taskName: TextView = itemView.task_name
}

data class Task(val id: Int, val name: String, val completed: Boolean)