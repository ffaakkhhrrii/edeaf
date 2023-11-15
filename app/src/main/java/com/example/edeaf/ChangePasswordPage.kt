package com.example.edeaf

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.edeaf.databinding.FragmentChangePasswordPageBinding
import com.example.edeaf.databinding.FragmentUserProfilePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChangePasswordPage : Fragment() {

    private var _binding: FragmentChangePasswordPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRef: DatabaseReference

    private var uid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordPageBinding.inflate(inflater,container,false)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        uid = currentUser?.uid

        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")

        binding.apply {
            btnSavePassword.setOnClickListener {
                val newPassword = edtNewPassword.text.toString()

                currentUser?.updatePassword(newPassword)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseRef.child(uid.toString()).child("password").setValue(newPassword)
                                .addOnCompleteListener {
                                    Toast.makeText(requireContext(),"Password berhasil diubah",Toast.LENGTH_SHORT).show()
                                    val fragmentManager = requireActivity().supportFragmentManager
                                    fragmentManager.popBackStack()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // Ada kesalahan saat memperbarui kata sandi
                            Toast.makeText(context, "Gagal mengubah password", Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

}