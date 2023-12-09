package com.borg.blabble.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.borg.blabble.R
import com.borg.blabble.model.TopicData

class TopicAdapter(private val dataList: ArrayList<TopicData>, private val switchCheckedChangeListener: OnSwitchCheckedChangeListener)
    :RecyclerView.Adapter<TopicAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.topic_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.rvImage.setImageResource(currentItem.topicImage)
        holder.rvTitle.text = currentItem.topicTitle
        holder.rvSwitch.isChecked = currentItem.isChecked

        //setup switch listener
        holder.rvSwitch.setOnCheckedChangeListener{_, isChecked ->
            currentItem.isChecked = isChecked
            switchCheckedChangeListener.onSwitchCheckedChange(position, isChecked)
        }
    }

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val rvImage: ImageView = itemView.findViewById(R.id.topicImage)
        val rvTitle: TextView = itemView.findViewById(R.id.topicTitle)
        val rvSwitch: SwitchCompat = itemView.findViewById(R.id.topicSwitch)
    }

    interface OnSwitchCheckedChangeListener{
        fun onSwitchCheckedChange(position: Int, isChecked: Boolean)
    }
}