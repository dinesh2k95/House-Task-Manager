package projects.dinesh.housetaskmanager.admin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.applandeo.materialcalendarview.CalendarView.MANY_DAYS_PICKER
import com.applandeo.materialcalendarview.CalendarView.ONE_DAY_PICKER
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_admin.*

import projects.dinesh.housetaskmanager.R
import projects.dinesh.housetaskmanager.task.User
import projects.dinesh.housetaskmanager.utils.Utils.TASK_DATE_FORMAT
import projects.dinesh.housetaskmanager.utils.Utils.formatGivenDateForPattern
import java.util.*
import kotlin.collections.ArrayList

class AdminFragment : Fragment() {

    private var usersDbReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")

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
                        println("Selected User" + userList[position])
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
                selectDate.text = formatGivenDateForPattern(calendar.timeInMillis, TASK_DATE_FORMAT)
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

    }

    companion object {
        @JvmStatic
        fun newInstance() = AdminFragment()
    }
}
