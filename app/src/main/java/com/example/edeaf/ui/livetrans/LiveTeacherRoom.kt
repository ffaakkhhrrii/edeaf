package com.example.edeaf.ui.livetrans

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
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edeaf.R
import com.example.edeaf.ui.adapter.StudentQuestionAdapter
import com.example.edeaf.databinding.FragmentLiveTeacherRoomBinding
import com.example.edeaf.data.model.LiveTrans
import com.example.edeaf.data.model.Participant
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

    private lateinit var questionList: ArrayList<Participant>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveTeacherRoomBinding.inflate(inflater,container,false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseRef = FirebaseDatabase.getInstance().getReference("LiveTrans")


        questionList = arrayListOf()
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

        return binding.root
    }

    private fun showQuestion() {
        firebaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                questionList.clear()
                if (snapshot.exists()){
                    for(historySnap: DataSnapshot in snapshot.children){
                        for (idParticip: DataSnapshot in historySnap.child("participantId").children){
                            if (args.liveId.contains(historySnap.child("liveId").value.toString())){
                                val history = idParticip.getValue(Participant::class.java)
                                questionList.add(history!!)
                            }
                        }
                    }
                }
                val rvAdapter = StudentQuestionAdapter(questionList)
                binding.rvAsk.adapter = rvAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.message.toString(),Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun endRoom() {
        context?.let { context ->
            val dialogView: View = layoutInflater.inflate(R.layout.end_room, null)

            val buttonEnd: Button = dialogView.findViewById(R.id.yesEnd)
            val closeBtn: Button = dialogView.findViewById(R.id.cancelEnd)

            val dialog = MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .create()

            buttonEnd.setOnClickListener {
                var liveTrans : LiveTrans
                val currentTime = Date()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val formattedTime = dateFormat.format(currentTime)

                firebaseRef.child(args.liveId).child("endTime").setValue(formattedTime)
                    .addOnCompleteListener {
                        findNavController().navigate(R.id.action_liveTeacherRoom_to_homeFragment)
                        dialog.dismiss()
                    }
                    .addOnFailureListener{
                        Toast.makeText(context,it.message.toString(),Toast.LENGTH_SHORT).show()
                    }
            }

            closeBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK){
            var liveTrans: LiveTrans
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedTextFromSpeech = result?.get(0).toString()
            textSpeech.append(recognizedTextFromSpeech).append(" ")

            firebaseRef.child(args.liveId).child("historyText").setValue(textSpeech.toString())
                .addOnCompleteListener {
                    Toast.makeText(context,"Text Stored", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(context,it.message.toString(),Toast.LENGTH_SHORT).show()
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