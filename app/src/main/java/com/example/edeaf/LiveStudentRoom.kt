package com.example.edeaf

import android.app.AlertDialog
import android.graphics.Typeface
import android.graphics.fonts.Font
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.marginTop
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.edeaf.databinding.FragmentLiveStudentRoomBinding
import com.example.edeaf.databinding.FragmentLiveTranslationStudentBinding
import com.example.edeaf.model.LiveTrans
import com.example.edeaf.model.Users
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LiveStudentRoom : Fragment() {

    private var _binding: FragmentLiveStudentRoomBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    private val args: LiveStudentRoomArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveStudentRoomBinding.inflate(inflater,container,false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseRef = FirebaseDatabase.getInstance().getReference("LiveTrans")

        binding.apply {
            firebaseRef.child(args.liveId).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        teacherSpeechText.text = snapshot.child("historyText").value.toString()
                        titleRoom.text = snapshot.child("title").value.toString()
                        if (snapshot.child("endTime").value.toString() != null && snapshot.child("endTime").value.toString() != "" ){
                            val fragmentManager = requireActivity().supportFragmentManager
                            fragmentManager.popBackStack()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT).show()
                }

            })

            btnAsk.setOnClickListener {
                context?.let {
                        context ->
                    val dialogView: View = layoutInflater.inflate(R.layout.student_input, null)

                    // Mengakses elemen-elemen dalam layout kustom
                    val editTextInput = dialogView.findViewById<EditText>(R.id.edtAskStudent)
                    val buttonSubmit: Button = dialogView.findViewById(R.id.btnSubmitQuestion)
                    val closeBtn : ImageView = dialogView.findViewById(R.id.closeAsk)

                    val dialog = MaterialAlertDialogBuilder(context)
                        .setView(dialogView)
                        .create()

                    buttonSubmit.setOnClickListener {
                        val questionId = firebaseRef.push().key!!
                        firebaseRef.child(args.liveId).child("participantId").child(args.participantId).child("questions").child(questionId).child("questionText").setValue(editTextInput.text.toString())
                            .addOnCompleteListener {
                                dialog.dismiss()
                            }
                            .addOnFailureListener{
                                Toast.makeText(context,"Error ${it.message}}", Toast.LENGTH_SHORT).show()
                            }
                    }

                    closeBtn.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
                }
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }


}