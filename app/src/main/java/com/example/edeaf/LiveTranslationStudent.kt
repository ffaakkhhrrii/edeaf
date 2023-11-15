package com.example.edeaf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edeaf.adapter.StudentHistoryAdapter
import com.example.edeaf.databinding.FragmentLiveTranslationStudentBinding
import com.example.edeaf.model.HistoryResponse
import com.example.edeaf.model.LiveTrans
import com.example.edeaf.model.Participants
import com.example.edeaf.model.Users
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

class LiveTranslationStudent : Fragment() {

    private var _binding: FragmentLiveTranslationStudentBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var historyList: ArrayList<HistoryResponse>

    private var participantsId: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveTranslationStudentBinding.inflate(inflater, container, false)

        firebaseRef = FirebaseDatabase.getInstance().reference
        firebaseAuth = FirebaseAuth.getInstance()
        participantsId = firebaseRef.push().key!!

        //participantsId = firebaseRef.push().key!!

        binding.apply {
            btnJoinGroup.setOnClickListener {
                validateCodeRoom()
            }

        }
        historyList = arrayListOf()
        showHistory()

        binding.rvHistoryStudent.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
        }
        // Inflate the layout for this fragment

        return binding.root
    }

    private fun validateCodeRoom() {
        val codeRoom = binding.edtCodeRoom.text.toString()

        firebaseRef.child("LiveTrans").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot: DataSnapshot in snapshot.children) {
                        if (codeRoom.contains(childSnapshot.child("codeRoom").value.toString())) {
                            if (childSnapshot.child("endTime").value.toString() != null && childSnapshot.child(
                                    "endTime"
                                ).value.toString() != ""
                            ) {
                                Toast.makeText(
                                    requireContext(),
                                    "Pertemuan Telah Berakhir",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                updateLiveTrans(childSnapshot.child("liveId").value.toString())
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun showHistory() {
        firebaseRef.child("LiveTrans").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                historyList.clear()
                val uid = firebaseAuth.currentUser?.uid
                if (snapshot.exists()) {
                    for (historySnap: DataSnapshot in snapshot.children) {
                        for (idParticip: DataSnapshot in historySnap.child("participantId").children) {
                            if (historySnap.child("endTime").value.toString() != "" && uid.toString()
                                    .contains(idParticip.child("userId").value.toString())
                            ) {
                                val history = historySnap.getValue(HistoryResponse::class.java)
                                historyList.add(history!!)
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
                val rvAdapter = StudentHistoryAdapter(historyList)
                binding.rvHistoryStudent.adapter = rvAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateLiveTrans(id: String) {
        var participant: Participants
        val uid = firebaseAuth.currentUser?.uid
        val currentTime = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formattedTime = dateFormat.format(currentTime)
        var name: String

        val userRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid.toString())

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(Users::class.java)
                        name = data?.name.toString()

                        participant = Participants(participantsId, uid, name, formattedTime)
                        firebaseRef.child("LiveTrans").child(id).child("participantId")
                            .child(participantsId.toString()).setValue(participant)
                            .addOnCompleteListener {
                                val direct =
                                    LiveTranslationStudentDirections.actionLiveTranslationStudentToLiveStudentRoom(
                                        id,
                                        participantsId.toString()
                                    )
                                findNavController().navigate(direct)
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }
}

