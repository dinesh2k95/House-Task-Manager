package projects.dinesh.housetaskmanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class TaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TaskFragment.newInstance())
                .commitNow()
        }
    }

}
