package ForgetPassFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Space
import android.widget.Toast
import com.flexicharge.bolt.Communicator_
import com.flexicharge.bolt.R

import com.google.android.gms.tasks.OnCanceledListener



class FragmentRecoverPass : Fragment() {

    private  lateinit var communicator: Communicator_
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_recover_pass, container, false)

        communicator = activity as Communicator_
        view.findViewById<Button>(R.id.buttonConfirmRecoverPassword).setOnClickListener{
            val email: String = view.findViewById<EditText>(R.id.loginActivity_editText_email).text.toString().trim{it <= ' '}
            fun String.isEmailValid(): Boolean {
                return !email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
            }
            if (!email.isEmailValid()){
                Toast.makeText(activity, "The email address is badly formatted", Toast.LENGTH_SHORT).show()
            }else{

                //here check the address



                communicator.passDataCom(view.findViewById<EditText>(R.id.loginActivity_editText_email).text.toString())

            }
        }
        return view

    }

}