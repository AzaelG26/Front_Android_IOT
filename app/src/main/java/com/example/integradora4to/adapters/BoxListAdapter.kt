package com.example.integradora4to.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.integradora4to.models.request.response.Box
import com.example.integradora4to.R

class BoxListAdapter(private var boxes: List<Box>): RecyclerView.Adapter<BoxListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nicknameTextView: TextView = itemView.findViewById(R.id.boxNicknameTextView)
        val imageView: ImageView = itemView.findViewById(R.id.boxImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.box_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val box = boxes[position]
        holder.nicknameTextView.text = box.nickname
    }

    override fun getItemCount(): Int {
        return boxes.size
    }

    fun updateData(newBoxes: List<Box>) {
        boxes = newBoxes
        notifyDataSetChanged()
    }
}