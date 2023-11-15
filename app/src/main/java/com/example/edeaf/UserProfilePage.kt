package com.example.edeaf

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.edeaf.databinding.FragmentUserProfilePageBinding
import com.example.edeaf.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class UserProfilePage : Fragment() {

    private var _binding: FragmentUserProfilePageBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRef: DatabaseReference

    private var imgUri:String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfilePageBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        firebaseRef = FirebaseDatabase.getInstance().getReference("Users").child(uid.toString())
        binding.apply {
            firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(Users::class.java)
                        userName.text = data?.name
                        userEmail.text = data?.email
                        Picasso.get().load(data?.imageProfile).into(userPhotoProfile)

                        btnSetProfile.setOnClickListener {
                            val action = UserProfilePageDirections.actionUserProfilePageToSetProfilePage(userName.text.toString(),
                                userEmail.text.toString(),uid.toString(),data?.imageProfile.toString(),data?.password.toString(),data?.role.toString())
                            findNavController().navigate(action)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
                }
            })

            logoutUser.setOnClickListener {
                firebaseAuth.signOut()
                clearCredentials()
                val intent = Intent(requireContext(),LoginActivity::class.java)
                startActivity(intent)
            }

            btnKeamanan.setOnClickListener {
                findNavController().navigate(R.id.action_userProfilePage_to_changePasswordPage)
            }
            btnReport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","edeaf.gmove@gmail.com",null))
                startActivity(Intent.createChooser(emailIntent,"Berikan feedback"))
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }
    private fun clearCredentials() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("username")
        editor.remove("password")
        editor.apply()
    }
}