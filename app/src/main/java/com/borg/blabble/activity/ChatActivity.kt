package com.borg.blabble.activity

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.borg.blabble.adapter.ChatAdapter
import com.borg.blabble.adapter.TopicAdapter
import com.borg.blabble.databinding.ActivityChatBinding
import com.borg.blabble.model.Message
import com.borg.blabble.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    private lateinit var bubbleId: String
    private lateinit var user: User

    private var messageList: ArrayList<Message> = arrayListOf()

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bubbleId = intent.getStringExtra("com.borg.blabble.activity.bubbleId")!!
        user = intent.getParcelableExtra<User>("com.borg.blabble.activity.user")!!

        //if editText is empty, set sendbtn to false
        binding.etMessage.text = null
        if(binding.etMessage.text.isEmpty()){
            binding.btnSendMessage.isEnabled = false
        }

        val linearLayoutManager = LinearLayoutManager(this)

        binding.chatRecyclerView.layoutManager = linearLayoutManager
        binding.chatRecyclerView.setHasFixedSize(true)
        binding.chatRecyclerView.adapter = ChatAdapter(messageList)

        Firebase.database.reference.child("bubbles").child(bubbleId).child("messages")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.getValue<Message>()?.let {
                        messageList.add(it)
                        binding.chatRecyclerView.adapter?.notifyItemInserted(messageList.indexOf(it))

                        //auto scroll
                        binding.chatRecyclerView.scrollToPosition(messageList.size - 1)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        Firebase.database.reference
            .child("users")
            .child(Firebase.auth.currentUser?.uid!!)
            .child("matched")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        binding.backBtn.setOnClickListener{
            Firebase.database.reference.child("bubbles").child(bubbleId).removeValue()
        }

        binding.btnSendMessage.setOnClickListener {
            val message = Message(Firebase.auth.currentUser?.uid!!, binding.etMessage.text.toString())
            Firebase.database.reference.child("bubbles").child(bubbleId).child("messages").push().setValue(message)

            binding.etMessage.text = null
        }

        binding.etMessage.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.etMessage.text.isEmpty()){
                    binding.btnSendMessage.isEnabled = false
                }
                if(binding.etMessage.text.isNotEmpty()){
                    binding.btnSendMessage.isEnabled = true
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Firebase.database.reference.child("bubbles").child(bubbleId).removeValue()
            }
        })
    }

    private fun deleteUserData() {
        val user = Firebase.auth.currentUser ?: return

        // Delete user data from the database
        val userReference = Firebase.database.reference.child("users").child(user.uid)

        userReference.removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(ContentValues.TAG, "User data deleted from the database")

                // Optionally, sign the user out after deleting data
                Firebase.auth.signOut()

            } else {
                Log.w(ContentValues.TAG, "Error deleting user data from the database", it.exception)
                Toast.makeText(this, "Failed to delete user data. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteUserData()
    }
}