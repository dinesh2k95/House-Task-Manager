package projects.dinesh.housetaskmanager.task

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.task_fragment.*
import projects.dinesh.housetaskmanager.R
import projects.dinesh.housetaskmanager.databinding.TaskFragmentBinding

class TaskFragment : Fragment() {

    companion object {
        fun newInstance() = TaskFragment()
    }

    private lateinit var viewModel: TaskViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<TaskFragmentBinding>(inflater,
            R.layout.task_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        taskList.setHasFixedSize(false)
        viewModel.init(context!!)
        taskList.layoutManager = LinearLayoutManager(context)

        viewModel.points.observe(this, Observer {points: String? ->
            infoView.text = "Total for today is $points Points"
        })

        viewModel.status.observe(this, Observer {status: String? ->
            if (status == "No Tasks") {
                taskList.visibility = GONE
                infoView.text = "No Tasks for Today !!!"
            } else {
                taskList.visibility = VISIBLE
            }
        })
    }
}