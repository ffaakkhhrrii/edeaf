package com.example.edeaf.ui.livetrans

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edeaf.ui.adapter.StudentHistoryAdapter
import com.example.edeaf.databinding.FragmentLiveTranslationStudentBinding
import com.example.edeaf.data.model.HistoryResponse
import com.example.edeaf.data.model.Participants
import com.example.edeaf.data.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

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
                CoroutineScope(Dispatchers.Main).launch {
                    validateCodeRoom()
                }
            }

        }
        historyList = arrayListOf()
        showHistory()

        binding.rvHistoryStudent.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
        }

        return binding.root
    }

    private suspend fun validateCodeRoom() {
        withContext(Dispatchers.Main) {
            binding.btnJoinGroup.isEnabled = false
        }
        val codeRoom = binding.edtCodeRoom.text.toString()

        firebaseRef.child("LiveTrans").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    showToast("No data available")
                    binding.btnJoinGroup.isEnabled = true
                    return
                }

                var codeValid = false
                for (childSnapshot in snapshot.children) {
                    val childCodeRoom =
                        childSnapshot.child("codeRoom").value?.toString() ?: continue
                    if (codeRoom == childCodeRoom) {
                        codeValid = true
                        val endTime = childSnapshot.child("endTime").value?.toString()
                        if (endTime.isNullOrEmpty()) {
                            val liveId = childSnapshot.child("liveId").value?.toString() ?: continue
                            updateLiveTrans(liveId)
                        } else {
                            showToast("The Meeting Has Ended")
                        }
                        break
                    }
                }

                if (!codeValid) {
                    showToast("Code is invalid")
                }
                binding.btnJoinGroup.isEnabled = true
            }

            override fun onCancelled(error: DatabaseError) {
                showToast(error.message)
                binding.btnJoinGroup.isEnabled = true
            }
        })

    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(context, "Error to load history", Toast.LENGTH_SHORT).show()
                }
                val rvAdapter = StudentHistoryAdapter(historyList)
                binding.rvHistoryStudent.adapter = rvAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()

            }

        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateLiveTrans(id: String) {
        var participant: Participants
        val uid = firebaseAuth.currentUser?.uid
        val currentTime = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formattedTime = dateFormat.format(currentTime)
        var name: String

        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(uid.toString())

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
                            binding.btnJoinGroup.isEnabled = true
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT)
                                .show()
                            binding.btnJoinGroup.isEnabled = true
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                binding.btnJoinGroup.isEnabled = true
            }

        })

    }
}

