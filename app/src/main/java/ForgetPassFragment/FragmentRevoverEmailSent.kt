package ForgetPassFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.flexicharge.bolt.R
import com.flexicharge.bolt.activities.ForgotPasswordActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 */
class FragmentRevoverEmailSent : Fragment() {

    var emailAddress:String? = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_revover_email_sent, container, false)
        emailAddress = arguments?.getString("EmailAddress")

         view.findViewById<TextView>(R.id.textView_emailRecover).text = emailAddress
        view.findViewById<TextView>(R.id.textView_Send_again).setOnClickListener{
            val fragmentRecoverPass = FragmentRecoverPass()
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragmentRecoverPass).commit()

        }

        return view

    }

}