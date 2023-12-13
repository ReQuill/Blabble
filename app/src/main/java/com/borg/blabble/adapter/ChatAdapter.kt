package com.borg.blabble.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.borg.blabble.R
import com.borg.blabble.model.Message
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatAdapter(private val messageList: ArrayList<Message>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>(){
    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtmessage: TextView = view.findViewById(R.id.tvMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == MESSAGE_TYPE_RIGHT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.chat_right_layout, parent, false)
            return ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.chat_left_layout, parent, false)
            return ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = messageList[position]
        holder.txtmessage.text = chat.text
    }

    override fun getItemViewType(position: Int): Int {
        if (messageList[position].senderId == Firebase.auth.currentUser?.uid) {
            return MESSAGE_TYPE_RIGHT
        } else {
            return MESSAGE_TYPE_LEFT
        }
    }
}