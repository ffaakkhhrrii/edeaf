package com.example.edeaf

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.edeaf.databinding.FragmentLiveTranslationTeacherBinding
import com.example.edeaf.model.LiveTrans
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Date

class LiveTranslationTeacher : Fragment() {
    private var _binding: FragmentLiveTranslationTeacherBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveTranslationTeacherBinding.inflate(inflater,container,false)

        firebaseRef = FirebaseDatabase.getInstance().getReference("LiveTrans")
        firebaseAuth = FirebaseAuth.getInstance()

        binding.apply {
            btnCreateRoom.setOnClickListener{
                createRoom()
            }

            backFragment.setOnClickListener {
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun createRoom() {
        val edtTitleRoom = binding.edtTitleRoom.text.toString()

        val uid = firebaseAuth.currentUser?.uid
        val liveId = firebaseRef.push().key!!

        val currentTime = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formattedTime = dateFormat.format(currentTime)

        val codeRoom = (100000..999999).random()

        var liveTrans: LiveTrans

        if (edtTitleRoom.isEmpty()){
            binding.edtTitleRoom.error = "Berikan Nama Pertemuan"
        }else{
            liveTrans = LiveTrans(liveId,uid,edtTitleRoom,formattedTime,"", codeRoom = codeRoom, historyText = "", participantId = "")
            firebaseRef.child(liveId).setValue(liveTrans)
                .addOnCompleteListener {
                    val direct = LiveTranslationTeacherDirections.actionLiveTranslationTeacherToLiveTeacherRoom(liveId)
                    findNavController().navigate(direct)
                }
                .addOnFailureListener{
                    Toast.makeText(context, "${it.message}}", Toast.LENGTH_SHORT).show()
                }
        }


    }

}