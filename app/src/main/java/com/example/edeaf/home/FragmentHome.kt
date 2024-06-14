package com.example.edeaf.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.edeaf.R
import com.example.edeaf.databinding.FragmentHomeBinding
import com.example.edeaf.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentHome : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRef: DatabaseReference

    private var imgUri: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid

        firebaseRef = FirebaseDatabase.getInstance().getReference("Users").child(uid.toString())
        binding.apply {
            firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(Users::class.java)
                        val uri =  data?.imageProfile.toString()

                        imgUri = uri

                        if (imgUri != null){
                            Picasso.get().load(imgUri).into(imgUser)
                            tvNameUser.text = data?.name.toString()
                        }else{
                            imgUser.setImageResource(R.drawable.user_profile_nulll)
                            tvNameUser.text = data?.name.toString()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context,error.message.toString(),Toast.LENGTH_SHORT).show()

                }

            })

            btnLiveTrans.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    moveValidatePage()
                }
            }
            cvAlphabet.setOnClickListener {
                val direct = FragmentHomeDirections.actionHomeFragmentToDictionaryPage("Alphabet")
                findNavController().navigate(direct)
            }
            cvBulan.setOnClickListener {
                val direct = FragmentHomeDirections.actionHomeFragmentToDictionaryPage("Month")
                findNavController().navigate(direct)
            }
            cvHari.setOnClickListener {
                val direct = FragmentHomeDirections.actionHomeFragmentToDictionaryPage("Day")
                findNavController().navigate(direct)
            }
            cvSehari.setOnClickListener {
                val direct = FragmentHomeDirections.actionHomeFragmentToDictionaryPage("Daily")
                findNavController().navigate(direct)
            }
        }

        return binding.root
    }

    private suspend fun moveValidatePage() {
        withContext(Dispatchers.Main) {
            binding.btnLiveTrans.isEnabled = false // Disable the button to prevent double-click
        }
        firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(Users::class.java)
                    if (data?.role.toString() == "Teacher"){
                        findNavController().navigate(R.id.action_homeFragment_to_liveTranslationTeacher)
                        binding.btnLiveTrans.isEnabled = true
                    }else if(data?.role.toString() == "Student"){
                        findNavController().navigate(R.id.action_homeFragment_to_liveTranslationStudent)
                        binding.btnLiveTrans.isEnabled = true
                    }else{
                        Toast.makeText(requireContext(),"Error",Toast.LENGTH_SHORT).show()
                        binding.btnLiveTrans.isEnabled = true
                    }
                }else{
                    Toast.makeText(requireContext(),"Error",Toast.LENGTH_SHORT).show()
                    binding.btnLiveTrans.isEnabled = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),error.message.toString(),Toast.LENGTH_SHORT).show()
            }

        })


    }


}