package FragmentRecoverPass

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.activities.LoginActivity
import com.flexicharge.bolt.api.flexicharge.ResetRequestBody
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.databinding.FragmentRevoverEmailSentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class FragmentRevoverEmailSent : Fragment() {

    var emailAddress: String? = ""
    private var _binding: FragmentRevoverEmailSentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRevoverEmailSentBinding.inflate(inflater, container, false)
        // Displey Email
        emailAddress = arguments?.getString("EmailAddress")
        binding.textViewEmailRecover.text = emailAddress

        binding.buttinConfirm.setOnClickListener {
            val email = binding.emailConfirmEditText.text.toString()
            val newPassword = binding.newPassword.text.toString()
            val confirmCode = binding.confrimCode.text.toString()

            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val body = ResetRequestBody(email, newPassword, confirmCode)
                    val response = RetrofitInstance.flexiChargeApi.confReset(body)
                    if (response.code() == 200) {
                        navigateToLogIn()
                    } else if (response.code() == 400) {
                        Toast.makeText(activity,  " Please try later.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: HttpException) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.textViewSendAgain.setOnClickListener {
            val sendAgain = binding.textViewEmailRecover.text.toString()
            lifecycleScope.launch(Dispatchers.Main){
                try {
                    val response = RetrofitInstance.flexiChargeApi.resetPass(sendAgain)
                    if (response.code() == 200){
                        Toast.makeText(
                            activity,
                            "A message with a verification code has been sent",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else if(response.code() == 400){
                        Toast.makeText(activity,  " Attempt limit exceeded, please try later.", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: HttpException){
                    Toast.makeText(activity,  e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }
    private fun navigateToLogIn() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
    }

}






