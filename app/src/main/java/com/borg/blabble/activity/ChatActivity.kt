package com.borg.blabble.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.borg.blabble.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    var firebaseuser: FirebaseUser? = null
    var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        var intent = getIntent()
//        var userId = intent.getStringExtra("userId")
//
//        firebaseuser = FirebaseAuth.getInstance().currentUser
//        reference = FirebaseDatabase.getInstance().getReference("users").child(userId!!)
//
//        binding.btnSendMessage.setOnClickListener{
//            var message: String = binding.etMessage.text.toString()
//
//            if(message.isEmpty()){
//                Toast.makeText(applicationContext, "message is empty", Toast.LENGTH_SHORT).show()
//            }
//            else{
//                sendMessage(firebaseuser!!.uid, userId!!, message)
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

//    private fun sendMessage(senderId: String, receiverId: String, message: String){
//        var reference: DatabaseReference? = FirebaseDatabase.getInstance().getReference()
//
//        var hashMap: HashMap<String, String> = HashMap()
//        hashMap.put("senderId", senderId)
//        hashMap.put("receiverId", receiverId)
//        hashMap.put("message", message)
//
//        reference!!.child("Chat").push().setValue(hashMap)
//    }
}