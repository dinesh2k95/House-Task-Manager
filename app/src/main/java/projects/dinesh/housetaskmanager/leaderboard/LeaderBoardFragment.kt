package projects.dinesh.housetaskmanager.leaderboard

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import projects.dinesh.housetaskmanager.R


class LeaderBoardFragment : Fragment() {

    companion object {
        fun newInstance() = LeaderBoardFragment()
    }

    private lateinit var viewModel: LeaderBoardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.leader_board_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LeaderBoardViewModel::class.java)


    }

}
