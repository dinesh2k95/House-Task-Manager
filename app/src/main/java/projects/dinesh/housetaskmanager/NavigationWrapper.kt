package projects.dinesh.housetaskmanager

import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.gofrugal.sellquick.atslibrary.CustomToast
import kotlinx.android.synthetic.main.main_content.*
import projects.dinesh.housetaskmanager.LoginFragment.Companion.AccessLevel.*
import projects.dinesh.housetaskmanager.admin.AdminFragment
import projects.dinesh.housetaskmanager.task.TaskActivity
import projects.dinesh.housetaskmanager.task.TaskFragment
import projects.dinesh.housetaskmanager.utils.AppPreferences

class NavigationWrapper(val taskActivity: TaskActivity) : NavigationView.OnNavigationItemSelectedListener {

    private var previousMenuItem = -1
    private var toast: CustomToast
    private var drawer: DrawerLayout? = null
    private val fragmentManager = taskActivity.supportFragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction
    private var navigationView: NavigationView = taskActivity.navigationView

    init {
        drawer = taskActivity.drawerLayout
        toast = CustomToast(taskActivity)
    }

    fun initialiseNavigationDrawer(toolbar: Toolbar) {
        drawer?.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val accessLevel = LoginFragment.usersDataSet.first { it.first == AppPreferences.getString(taskActivity, "userName", "") }.third
                if (accessLevel != ADMIN) {
                    taskActivity.navigationView?.menu?.findItem(R.id.admin_panel)?.isVisible = false
                }
            }

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })
        taskActivity.setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(taskActivity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer?.setDrawerListener(toggle)
        toggle.syncState()
        initialiseNavigationView()
    }

    private fun initialiseNavigationView() {
        navigationView.setCheckedItem(R.id.task_entry)
        fragmentManager.beginTransaction()
            .replace(R.id.content_frame, TaskFragment.newInstance())
            .commitNow()
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(drawerItem: MenuItem): Boolean {
        if (previousMenuItem == drawerItem.itemId)
            return true
        fragmentTransaction = fragmentManager.beginTransaction()
        when (drawerItem.itemId) {
            R.id.task_entry -> {
                fragmentManager.popBackStackImmediate(null, POP_BACK_STACK_INCLUSIVE)
                taskActivity.supportActionBar!!.title = "House Task Manager"
                fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, TaskFragment.newInstance())
                    .commitNow()
            }
            R.id.admin_panel -> {
                fragmentManager.popBackStackImmediate(null, POP_BACK_STACK_INCLUSIVE)
                taskActivity.supportActionBar!!.title = "Admin Panel"
                fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, AdminFragment.newInstance())
                    .commitNow()
            }
        }
        previousMenuItem = drawerItem.itemId
        drawerItem.isChecked = true
        closeNavigationDrawer()
        return true
    }

    fun isNavigationDrawerOpen(): Boolean {
        if (drawer != null && drawer!!.isDrawerOpen(GravityCompat.START))
            return true
        return false
    }

    fun closeNavigationDrawer() {
        if (drawer != null && drawer!!.isDrawerOpen(GravityCompat.START))
            drawer?.closeDrawer(GravityCompat.START)
    }

    private fun findViewById(id: Int): View {
        return taskActivity.findViewById(id)
    }
}