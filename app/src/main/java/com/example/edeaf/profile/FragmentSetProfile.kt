package com.example.edeaf.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.edeaf.R
import com.example.edeaf.databinding.FragmentSetProfilePageBinding
import com.example.edeaf.model.Users
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class FragmentSetProfile : Fragment() {
    private var _binding: FragmentSetProfilePageBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentUser: FirebaseUser
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference

    private var uri: Uri? = null
    private var imgUri: String? = null


    private val args: FragmentSetProfileArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetProfilePageBinding.inflate(inflater, container, false)


        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!
        firebaseRef = FirebaseDatabase.getInstance().getReference("Users")
        storageRef = FirebaseStorage.getInstance().getReference("Profile")


        binding.apply {
            edtNameUser.setText(args.name)
            edtEmailUser.setText(args.email)
            Picasso.get().load(args.imageUri).into(imgUpdate)

            val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
                binding.imgUpdate.setImageURI(it)
                if (it != null) {
                    uri = it
                }
            }

            imgUri = args.imageUri
            changeImg.setOnClickListener {
                context?.let { context ->
                    MaterialAlertDialogBuilder(context)
                        .setTitle("Change profile photo")
                        .setMessage("Do you want to change your profile photo?")
                        .setPositiveButton("Change") { _, _ ->
                            pickImage.launch("image/*")
                        }
                        .setNegativeButton("Delete") { _, _ ->
                            imgUri = null
                            imgUpdate.setImageResource(R.drawable.user_profile_nulll)
                        }
                        .setNeutralButton("Cancel") { _, _ ->
                        }
                        .show()
                }
            }

            binding.btnSaveProfile.setOnClickListener {
                updateData()
                findNavController().navigate(R.id.action_setProfilePage_to_userProfilePage)
            }
            backFragment.setOnClickListener {
                findNavController().navigate(R.id.action_setProfilePage_to_userProfilePage)
            }

        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun updateData() {
        val name = binding.edtNameUser.text.toString()
        val email = binding.edtEmailUser.text.toString()

        var users: Users
        if (uri != null) {
            storageRef.child(args.uid).putFile(uri!!)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {
                            imgUri = it.toString()
                            users = Users(args.uid, name, email, args.password, args.role, imgUri)
                            firebaseRef.child(args.uid).setValue(users)
                                .addOnCompleteListener {
                                    currentUser.verifyBeforeUpdateEmail(email)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                currentUser.sendEmailVerification()
                                                    .addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            Toast.makeText(
                                                                context,
                                                                "Email successfully sent to $email",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "Failed to update email: ${task.exception?.message}",
                                                                Toast.LENGTH_SHORT
                                                            ).show()

                                                            Log.i("updateEmailError",task.exception?.message.toString())
                                                        }
                                                    }
                                            } else {
                                                Toast.makeText(context, "Failed to update email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "${it.message}}", Toast.LENGTH_SHORT)
                                        .show()
                                }
                        }
                }
        } else if (uri == null) {
            users = Users(args.uid, name, email, args.password, args.role, imgUri)
            firebaseRef.child(args.uid).setValue(users)
                .addOnCompleteListener {
                    currentUser.verifyBeforeUpdateEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                currentUser.sendEmailVerification()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "Email successfully sent to $email",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Failed to update email: ${task.exception?.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            Log.i("updateEmailError",task.exception?.message.toString())
                                        }
                                    }
                            } else {
                                Toast.makeText(context, "Failed to update email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "${it.message}}", Toast.LENGTH_SHORT).show()
                }
        }

        if (imgUri == null) {
            storageRef.child(args.uid).delete()
        }

    }

}