package projects.dinesh.housetaskmanager.task

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import projects.dinesh.housetaskmanager.LoginFragment
import projects.dinesh.housetaskmanager.R
import projects.dinesh.housetaskmanager.admin.AdminFragment
import projects.dinesh.housetaskmanager.utils.AppPreferences

class TaskActivity : AppCompatActivity(), LoginFragment.OnLoginListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_activity)
        if (savedInstanceState == null && AppPreferences.getString(this, "userName", "") == "") {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    LoginFragment.newInstance()
                )
                .commitNow()
        } else {
            onLoginSuccess()
        }
    }

    override fun onLoginSuccess() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, TaskFragment.newInstance())
            .commitNow()
    }

    fun openAdminFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AdminFragment.newInstance())
            .commitNow()
    }

}
