package projects.dinesh.housetaskmanager

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_login.*
import projects.dinesh.housetaskmanager.utils.AppPreferences

class LoginFragment : Fragment() {
    private var listener: OnLoginListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        done.setOnClickListener {
            validateLogin()
        }
    }

    private fun validateLogin() {
        val name = name.text.toString()
        val password = password.text.toString()
        println("Name::: $name Password :::: $password")
        userNameMap[name]?.let { correctPassword ->
            if (password == correctPassword) {
                AppPreferences.putString(context!!, "userName", name)
                listener?.onLoginSuccess()
            } else {
                println("InCorrect password $name !!")
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLoginListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnLoginListener {
        fun onLoginSuccess()
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()

        val userNameMap: HashMap<String, String> = hashMapOf(
            Pair("Karunanidhi", "Karunanidhi"),
            Pair("Jeyamani", "Jeyamani"),
            Pair("Sharmili", "Sharmili"),
            Pair("Dinesh", "Dinesh")
        )
    }
}
