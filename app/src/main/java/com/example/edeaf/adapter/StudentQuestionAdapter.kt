package com.example.edeaf.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.edeaf.LiveTranslationStudentDirections
import com.example.edeaf.databinding.ListItemAskBinding
import com.example.edeaf.databinding.ListLiveHistoryBinding
import com.example.edeaf.model.HistoryResponse
import com.example.edeaf.model.Participant
import com.example.edeaf.model.Participants

class StudentQuestionAdapter(private val historyList: List<Participant>): RecyclerView.Adapter<StudentQuestionAdapter.ViewHolder>() {
    class ViewHolder(val binding: ListItemAskBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemAskBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = historyList[position]
        holder.apply {
            binding.apply {
//                val participantName = currentItem.participantId?.values?.firstOrNull()?.name
//                nameStudent.text = participantName
//
//                val questions = currentItem.participantId?.values?.firstOrNull()?.questions?.values?.firstOrNull()?.questionText
//                questionStudent.text = questions
                nameStudent.text = currentItem.name

                val questions = currentItem.questions?.values?.toList() ?: emptyList()
                val questionTextList = questions.mapNotNull { it.questionText }

                val questionText = if (questionTextList.isNotEmpty()) {
                    questionTextList.joinToString(", ")
                } else {
                    "No questions"
                }

                questionStudent.text = questionText
                }
            }
        }
    }
