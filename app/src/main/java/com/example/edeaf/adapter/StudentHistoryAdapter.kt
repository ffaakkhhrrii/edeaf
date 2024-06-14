package com.example.edeaf.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.edeaf.databinding.ListLiveHistoryBinding
import com.example.edeaf.livetrans.LiveTranslationStudentDirections
import com.example.edeaf.model.HistoryResponse

class StudentHistoryAdapter(private val historyList: ArrayList<HistoryResponse>):RecyclerView.Adapter<StudentHistoryAdapter.ViewHolder>() {
    class ViewHolder(val binding:ListLiveHistoryBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListLiveHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = historyList[position]
        holder.apply {
            binding.apply {

                rvContainer.setOnClickListener{
                    val action = LiveTranslationStudentDirections.actionLiveTranslationStudentToLiveStudentHistory(
                        currentItem.title.toString(),
                        currentItem.historyText.toString()
                    )
                    findNavController(holder.itemView).navigate(action)
                }

                titleLiveHistory.text = currentItem.title
                dateLiveHistory.text = currentItem.startTime
            }
        }
    }
}