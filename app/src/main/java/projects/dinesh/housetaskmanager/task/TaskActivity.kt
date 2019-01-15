package projects.dinesh.housetaskmanager.task

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_content.*
import projects.dinesh.housetaskmanager.LoginFragment
import projects.dinesh.housetaskmanager.LoginFragment.Companion.AccessLevel.ADMIN
import projects.dinesh.housetaskmanager.LoginFragment.Companion.usersDataSet
import projects.dinesh.housetaskmanager.NavigationWrapper
import projects.dinesh.housetaskmanager.R
import projects.dinesh.housetaskmanager.admin.AdminFragment
import projects.dinesh.housetaskmanager.utils.AppPreferences

class TaskActivity : AppCompatActivity(), LoginFragment.OnLoginListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_content)
        if (savedInstanceState == null && AppPreferences.getString(this, "userName", "") == "") {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, LoginFragment.newInstance())
                .commitNow()
        } else {
            NavigationWrapper(this).initialiseNavigationDrawer(top_toolbar)
        }
    }

    override fun onLoginSuccess() {
        val accessLevel = usersDataSet.first { it.first == AppPreferences.getString(this, "userName", "") }.third
        if (accessLevel == ADMIN) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, AdminFragment.newInstance())
                .commitNow()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, TaskFragment.newInstance())
                .commitNow()
        }
    }
}
