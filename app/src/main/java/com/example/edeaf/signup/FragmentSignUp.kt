package com.example.edeaf.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.edeaf.R
import com.example.edeaf.databinding.FragmentSignUpBinding
import com.example.edeaf.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentSignUp : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRef : DatabaseReference

    val role = arrayOf("Student", "Teacher")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, role)
        adapter.setDropDownViewResource(androidx.transition.R.layout.support_simple_spinner_dropdown_item)


        binding.apply {
            edtRoleSignUp.adapter = adapter
            CoroutineScope(Dispatchers.Main).launch {
                btnSignUp.setOnClickListener {
                    val name = edtNameSignUp.text.toString()
                    val email = edtEmailSignUp.text.toString()
                    val role = edtRoleSignUp.selectedItem.toString()
                    val password = edtPasswordSignUp.text.toString()

                    btnSignUp.isEnabled = false
                    if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val uid = firebaseAuth.currentUser?.uid.toString()
                                    val users : Users = Users(uid,name,email, password, role)
                                    firebaseRef.child(uid).setValue(users)
                                    findNavController().navigate(R.id.action_signUp_to_loginPage)
                                    btnSignUp.isEnabled = true
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Sign-up failed ${task.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    btnSignUp.isEnabled = true
                                }
                            }

                    } else{
                        Toast.makeText(requireContext(),"Data must be filled in",Toast.LENGTH_SHORT).show()
                        btnSignUp.isEnabled = true
                    }
                }
            }
        }

        return binding.root
    }

}