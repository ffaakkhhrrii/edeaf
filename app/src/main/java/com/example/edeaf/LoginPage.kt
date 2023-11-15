package com.example.edeaf

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.edeaf.databinding.FragmentLoginPageBinding
import com.google.firebase.auth.FirebaseAuth

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
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Simpan kredensial jika opsi "Ingat Saya" dicentang
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
                    }
            } else if (email.isEmpty()) {
                edtEmailLogin.error = "Write Email"
            } else if (password.isEmpty()) {
                edtEmailPassword.error = "Write Password"
            } else {
                Toast.makeText(requireContext(), "Data harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Saat pengguna berhasil login, simpan kredensial menggunakan SharedPreferences
    private fun saveCredentials(email: String, password: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", email)
        editor.putString("password", password)
        editor.apply()
    }

    // Saat aplikasi dimulai (misalnya dalam onCreate() di MainActivity), periksa apakah kredensial ada
    private fun checkRememberMe() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("username", null)
        val savedPassword = sharedPreferences.getString("password", null)

        if (savedUsername != null && savedPassword != null) {
            // Lakukan otentikasi otomatis menggunakan savedUsername dan savedPassword
            firebaseAuth.signInWithEmailAndPassword(savedUsername, savedPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Redirect ke halaman beranda atau halaman setelah login
                        val action = LoginPageDirections.actionLoginPageToHomePage(
                            savedUsername, savedPassword
                        )
                        findNavController().navigate(action)
                    } else {
                        // Gagal otentikasi otomatis, mungkin kredensial telah berubah
                        // Lakukan tindakan sesuai kebutuhan aplikasi Anda
                    }
                }
        }
    }

// Pemanggilan checkRememberMe() dapat ditempatkan dalam onCreate() di MainActivity



}