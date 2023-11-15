package com.example.edeaf

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.edeaf.databinding.FragmentHomeBinding
import com.example.edeaf.databinding.FragmentHomePageBinding
import com.example.edeaf.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {
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
                    TODO("Not yet implemented")
                }

            })

            btnLiveTrans.setOnClickListener {
                moveValidatePage()
            }
            cvAlphabet.setOnClickListener {
                val direct = HomeFragmentDirections.actionHomeFragmentToDictionaryPage("Alfabet")
                findNavController().navigate(direct)
            }
            cvBulan.setOnClickListener {
                val direct = HomeFragmentDirections.actionHomeFragmentToDictionaryPage("Bulan")
                findNavController().navigate(direct)
            }
            cvHari.setOnClickListener {
                val direct = HomeFragmentDirections.actionHomeFragmentToDictionaryPage("Hari")
                findNavController().navigate(direct)
            }
            cvSehari.setOnClickListener {
                val direct = HomeFragmentDirections.actionHomeFragmentToDictionaryPage("Sehari")
                findNavController().navigate(direct)
            }
        }

        return binding.root
    }

    private fun moveValidatePage() {
        firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val data = snapshot.getValue(Users::class.java)
                    if (data?.role.toString() == "Guru"){
                        findNavController().navigate(R.id.action_homeFragment_to_liveTranslationTeacher)
                    }else if(data?.role.toString() == "Murid"){
                        findNavController().navigate(R.id.action_homeFragment_to_liveTranslationStudent)
                    }else{
                        Toast.makeText(requireContext(),"Error",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(),"Error",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),error.message.toString(),Toast.LENGTH_SHORT).show()
            }

        })


    }


}