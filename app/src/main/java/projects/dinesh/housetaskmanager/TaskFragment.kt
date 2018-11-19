package projects.dinesh.housetaskmanager

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
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
        val binding = DataBindingUtil.inflate<TaskFragmentBinding>(inflater, R.layout.task_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        taskList.setHasFixedSize(false)
        viewModel.init()
        taskList.layoutManager = LinearLayoutManager(context)
//        (taskList.adapter as TaskViewModel.TaskAdapter).startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
//        (taskList.adapter as TaskViewModel.TaskAdapter).stopListening()
    }
}