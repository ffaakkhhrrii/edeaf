package com.example.edeaf.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.edeaf.R
import com.example.edeaf.data.mechanism.handleCollect
import com.example.edeaf.databinding.FragmentLoginPageBinding
import com.example.edeaf.ui.util.PresentationHelper.showMaterialDialog
import com.example.edeaf.ui.util.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginPageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPageBinding.inflate(inflater, container, false)

        checkRememberMe()
        binding.apply {
            signUp.setOnClickListener {
                findNavController().navigate(R.id.action_loginPage_to_signUp)
            }

            btnLogin.setOnClickListener {
                login()
            }
        }
        return binding.root
    }

    private fun login() {
        binding.apply {
            val email = edtEmailLogin.text.toString()
            val password = edtEmailPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.login(email, password).handleCollect(
                        onSuccess = { result->
                            requireContext().showMaterialDialog(
                                show = true,
                                message = "Login Success"
                            )

                            if (checkBoxLogin.isChecked){
                                saveCredentials(result.data!!,password)
                            }

                            val action = LoginFragmentDirections.actionLoginPageToHomePage(
                                email, password
                            )
                            findNavController().navigate(action)
                        },
                        onLoading = {
                            requireContext().showMaterialDialog(
                                show = true,
                                message = "Loading..."
                            )
                        },
                        onError = { result->
                            requireContext().showMaterialDialog(
                                show = true,
                                message = result.message!!
                            )
                        }
                    )
                }
            } else {
                if (email.isEmpty()) {
                    edtEmailLogin.error = "Write Email"
                } else if (password.isEmpty()) {
                    edtEmailPassword.error = "Write Password"
                } else {
                    Toast.makeText(requireContext(), "Data must be filled in", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveCredentials(uid: String, password: String) {
        SharedPreference.saveString(requireActivity(),"username",uid)
        SharedPreference.saveString(requireActivity(),"password",password)
    }

    private fun checkRememberMe() {
        val savedUsername = SharedPreference.getString(requireActivity(),"username")
        val savedPassword = SharedPreference.getString(requireActivity(),"password")

        viewLifecycleOwner.lifecycleScope.launch {
            if (savedUsername.isNotEmpty() && savedPassword.isNotEmpty()) {
                val action = LoginFragmentDirections.actionLoginPageToHomePage(
                    savedUsername, savedPassword
                )
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
