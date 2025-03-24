package com.example.edeaf.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.edeaf.R
import com.example.edeaf.data.model.DictionaryModel
import com.squareup.picasso.Picasso

class DictionaryAdapter(private val kamusList: List<DictionaryModel>) : RecyclerView.Adapter<com.example.edeaf.ui.adapter.DictionaryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val IVgambar: ImageView = itemView.findViewById(R.id.imgDict)
        val TVhuruf: TextView = itemView.findViewById(R.id.txtDict)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_kamus, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return kamusList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentKamus = kamusList[position]
        Picasso.get().load(currentKamus.gambar).into(holder.IVgambar)
        holder.TVhuruf.text = currentKamus.huruf

    }
}
