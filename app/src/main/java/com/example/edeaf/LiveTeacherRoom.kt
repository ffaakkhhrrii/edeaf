package com.example.edeaf

import android.app.Activity
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edeaf.adapter.StudentQuestionAdapter
import com.example.edeaf.databinding.FragmentLiveTeacherRoomBinding
import com.example.edeaf.model.HistoryResponse
import com.example.edeaf.model.LiveTrans
import com.example.edeaf.model.Users
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Date

class LiveTeacherRoom : Fragment() {
    private val RQ_SPEECH_REC = 102
    private val textSpeech = StringBuilder()

    private var _binding: FragmentLiveTeacherRoomBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseRef: DatabaseReference

    private val args : LiveTeacherRoomArgs by navArgs()

    private lateinit var historyList: ArrayList<HistoryResponse>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveTeacherRoomBinding.inflate(inflater,container,false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseRef = FirebaseDatabase.getInstance().getReference("LiveTrans")

        historyList = arrayListOf()
        showQuestion()
        binding.rvAsk.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
        }
        binding.apply {
            firebaseRef.child(args.liveId).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val data = snapshot.getValue(LiveTrans::class.java)
                        txtCodeRoom.text = data?.codeRoom.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
                }

            })

            btnVoice.setOnClickListener{
                inputHistoryText()
            }

            backFragment.setOnClickListener {
                endRoom()
            }

            btnShare.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, txtCodeRoom.text.toString())
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(sendIntent, "Share Code Room"))
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun showQuestion() {
        firebaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                historyList.clear()
                if (snapshot.exists()){
                    for(historySnap: DataSnapshot in snapshot.children){
                        for (idParticip: DataSnapshot in historySnap.child("participantId").children){
                                for (question: DataSnapshot in idParticip.child("questions").children){
                                    if (args.liveId.contains(historySnap.child("liveId").value.toString())){
                                        val history = historySnap.getValue(HistoryResponse::class.java)
                                        historyList.add(history!!)
                                    }
                                }
                        }
                    }
                }
                val rvAdapter = StudentQuestionAdapter(historyList)
                binding.rvAsk.adapter = rvAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun endRoom() {
        context?.let {
                context ->
            MaterialAlertDialogBuilder(context)
                .setTitle("Akhiri Ruangan")
                .setMessage("Anda yakin ingin mengakhiri ruangan?")
                .setPositiveButton("Akhiri"){
                        _,_->
                    var liveTrans : LiveTrans
                    val currentTime = Date()
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val formattedTime = dateFormat.format(currentTime)

                    firebaseRef.child(args.liveId).child("endTime").setValue(formattedTime)
                        .addOnCompleteListener {
                            findNavController().navigate(R.id.action_liveTeacherRoom_to_homeFragment)
                        }
                        .addOnFailureListener{

                        }

                }
                .setNegativeButton("Batal"){
                        _,_->
                }
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK){
            var liveTrans: LiveTrans
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedTextFromSpeech = result?.get(0).toString()
            // Menambahkan hasil pengenalan suara ke teks sebelumnya
            textSpeech.append(recognizedTextFromSpeech).append(" ")

            firebaseRef.child(args.liveId).child("historyText").setValue(textSpeech.toString())
                .addOnCompleteListener {
                    Toast.makeText(context,"Text Stored", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{

                }
        }
    }

    private fun inputHistoryText() {
        if(!SpeechRecognizer.isRecognitionAvailable(requireContext())){
            Toast.makeText(requireContext(),"Speech isn't available",Toast.LENGTH_SHORT).show()
        }else{
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"id")
            startActivityForResult(i,RQ_SPEECH_REC)
        }
    }

}