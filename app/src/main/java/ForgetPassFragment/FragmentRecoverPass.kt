package FragmentRecoverPass


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.flexicharge.bolt.Communicator_
import com.flexicharge.bolt.api.flexicharge.RetrofitInstance
import com.flexicharge.bolt.databinding.FragmentRecoverPassBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class FragmentRecoverPass : Fragment() {
    private lateinit var communicator: Communicator_
    private var _binding: FragmentRecoverPassBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecoverPassBinding.inflate(inflater, container, false)
        val email = binding.resetActivityEditTextEmail.text.toString()
        // Inflate the layout for this fragment
        communicator = activity as Communicator_

        binding.buttonConfirmRecoverPassword.setOnClickListener {

                sendEmailAdd()
        }
        return binding.root
    }
        private fun sendEmailAdd() {
            val email = binding.resetActivityEditTextEmail.text.toString()
            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val response = RetrofitInstance.flexiChargeApi.resetPass(email)

                    if (response.code() == 200) {
                        // navigate and pass Email address to fragmentrecoverEnamiSent
                        communicator.passDataCom(binding.resetActivityEditTextEmail.text.toString())
                    } else if (response.code() == 400) {
                        Toast.makeText(activity,  "Attempt limit exceeded, please try later.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: HttpException) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()

                }
            }
        }


    }





