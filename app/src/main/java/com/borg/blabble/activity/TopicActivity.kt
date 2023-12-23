package com.borg.blabble.activity

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.borg.blabble.R
import com.borg.blabble.adapter.TopicAdapter
import com.borg.blabble.databinding.ActivityTopicBinding
import com.borg.blabble.model.Topic
import com.borg.blabble.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONObject

class TopicActivity : AppCompatActivity(), TopicAdapter.OnSwitchCheckedChangeListener {
    private lateinit var binding: ActivityTopicBinding
    private lateinit var user: User

    private var topicList: ArrayList<Topic> = arrayListOf()
    private var selectedTopics: HashSet<Int> = hashSetOf()

    private lateinit var loadingDialog: Dialog

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getParcelableExtra<User>("com.borg.blabble.activity.user")!!

        // Set the user alias
        binding.chooseTopic.text = getString(R.string.nowchoose, user!!.name)

        binding.topicRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.topicRecyclerView.setHasFixedSize(true)
        binding.topicRecyclerView.adapter = TopicAdapter(topicList, this)

        selectedTopics.clear()

        Firebase.database.reference.child("topics").addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue<Topic>()?.let {
                    topicList.add(it)
                    binding.topicRecyclerView.adapter?.notifyItemInserted(topicList.indexOf(it))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", error.message);
            }
        })

        Firebase.database.reference
            .child("users")
            .child(Firebase.auth.currentUser?.uid!!)
            .child("matched")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Children must be whole (userId and bubbleId) which amounts to 2 properties
                    if (snapshot.childrenCount.toInt() != 2)
                        return

                    if (snapshot.exists()) {
                        Log.d("TAG", "Pairing successful")

                        loadingDialog.hide()

                        Intent(this@TopicActivity, ChatActivity::class.java).let {
                            it.putExtra("com.borg.blabble.activity.bubbleId", snapshot.child("bubbleId").value.toString());
                            it.putExtra("com.borg.blabble.activity.user", user)
                            startActivity(it)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        // Set up the back button behavior
        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                deleteUserData()
                finish()
            }
        })

        binding.startChatButton.setOnClickListener {
            //loading..
            loadingDialog = Dialog(this)
            loadingDialog.setContentView(R.layout.loading_layout)
            loadingDialog.window!!.setLayout(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            loadingDialog.show()

            startUserPairing()
        }
    }

    override fun onSwitchCheckedChange(position: Int, isChecked: Boolean) {
        if (isChecked) {
            selectedTopics.add(position + 1)
        } else {
            selectedTopics.remove(position + 1)
        }
    }

    private fun startUserPairing() {
        Log.d("TAG", "Requesting pairing")

        val currentUser = Firebase.auth.currentUser ?: return

        val data = JSONObject();
        data.put("userId", currentUser.uid)
        data.put("topics", JSONArray(selectedTopics))

        Firebase.functions("asia-southeast1")
            .getHttpsCallable("requestPairing")
            .call(data)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("TAG", task.exception?.message!!)

                    loadingDialog.hide()

                    val builder: AlertDialog.Builder = AlertDialog.Builder(this@TopicActivity)
                    builder
                        .setMessage(getString(R.string.matching_failed_dialog_content))
                        .setTitle(getString(R.string.matching_failed_dialog_title))
                    builder.create().show()
                }
            }
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

                Intent(this@TopicActivity, HomeActivity::class.java).let {
                    startActivity(it)
                    finish()
                }

            } else {
                Log.w(ContentValues.TAG, "Error deleting user data from the database", it.exception)
                Toast.makeText(this, "Failed to delete user data. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        // Dismiss the dialog to avoid WindowLeaked exception
//        if (::loadingDialog.isInitialized && loadingDialog.isShowing) {
//            loadingDialog.dismiss()
//        }
//
//        deleteUserData()
//    }
}