package com.example.edeaf.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.edeaf.R
import com.example.edeaf.databinding.FragmentLoginPageBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginPage : Fragment() {

    private var _binding: FragmentLoginPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPageBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        checkRememberMe()
        binding.apply {
            signUp.setOnClickListener {
                findNavController().navigate(R.id.action_loginPage_to_signUp)
            }

            btnLogin.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    login()
                }
            }
        }
        return binding.root
    }

    private suspend fun login() {
        withContext(Dispatchers.Main) {
            binding.btnLogin.isEnabled = false // Disable the button to prevent double-click
        }
        binding.apply {
            val email = edtEmailLogin.text.toString()
            val password = edtEmailPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Save credentials if "Remember Me" option is checked
                            if (checkBoxLogin.isChecked) {
                                saveCredentials(email, password)
                            }

                            val action = LoginPageDirections.actionLoginPageToHomePage(
                                email, password
                            )
                            findNavController().navigate(action)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Sign-in failed ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        binding.btnLogin.isEnabled = true // Re-enable the button after login attempt
                    }
            } else {
                withContext(Dispatchers.Main) {
                    if (email.isEmpty()) {
                        edtEmailLogin.error = "Write Email"
                    } else if (password.isEmpty()) {
                        edtEmailPassword.error = "Write Password"
                    } else {
                        Toast.makeText(requireContext(), "Data must be filled in", Toast.LENGTH_SHORT).show()
                    }
                    binding.btnLogin.isEnabled = true // Re-enable the button if input is invalid
                }
            }
        }
    }

    private fun saveCredentials(email: String, password: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", email)
        editor.putString("password", password)
        editor.apply()
    }

    private fun checkRememberMe() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("username", null)
        val savedPassword = sharedPreferences.getString("password", null)

        CoroutineScope(Dispatchers.IO).launch {
            if (savedUsername != null && savedPassword != null) {
                firebaseAuth.signInWithEmailAndPassword(savedUsername, savedPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val action = LoginPageDirections.actionLoginPageToHomePage(
                                savedUsername, savedPassword
                            )
                            findNavController().navigate(action)
                        } else {
                            // Automatic authentication failed, handle as needed
                        }
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
