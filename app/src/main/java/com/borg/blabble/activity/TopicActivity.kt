package com.borg.blabble.activity

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.borg.blabble.R
import com.borg.blabble.adapter.TopicAdapter
import com.borg.blabble.databinding.ActivityHomeBinding
import com.borg.blabble.databinding.ActivityTopicBinding
import com.borg.blabble.model.TopicData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TopicActivity : AppCompatActivity(), TopicAdapter.OnSwitchCheckedChangeListener {
    private lateinit var binding: ActivityTopicBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<TopicData>
    lateinit var imageList: Array<Int>
    lateinit var titleList: Array<String>

    private fun getData(){
        for (i in imageList.indices){
            val topicData = TopicData(imageList[i], titleList[i], isChecked = false)
            dataList.add(topicData)
        }
        recyclerView.adapter = TopicAdapter(dataList, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //topic recycler view
        imageList = arrayOf(
            R.drawable.ic_books,
            R.drawable.ic_movies,
            R.drawable.ic_games,
            R.drawable.ic_medical,
            R.drawable.ic_shopping,
            R.drawable.ic_drawing,
            R.drawable.ic_photography,
            R.drawable.ic_cars
        )

        titleList = arrayOf(
            "Books",
            "Movies",
            "Games",
            "Medical",
            "Shopping",
            "Drawing",
            "Photography",
            "Cars"
        )

        recyclerView = binding.topicRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        dataList = arrayListOf<TopicData>()
        getData()

        auth = FirebaseAuth.getInstance()

//         Set up the back button behavior
        val onBackPressedCallback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                deleteUserData()
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)

        binding.starChatBtn.setOnClickListener{
            val i = Intent(this, ChatActivity::class.java)
            startActivity(i)
        }
    }

    override fun onSwitchCheckedChange(position: Int, isChecked: Boolean) {
        val clickedItem = dataList[position]
        if (isChecked) {
            // Do something when the switch is activated (mungkin nanti disini logic user pairingnya
            Toast.makeText(this, "Switch Activated for: ${clickedItem.topicTitle}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteUserData() {
        val user = auth.currentUser

        if (user != null) {
            // Delete user data from the database
            val databaseReference = FirebaseDatabase.getInstance("https://blabble-5d037-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")
            val userReference = databaseReference.child(user.uid)

            userReference.removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "User data deleted from the database")
                        // Optionally, sign the user out after deleting data
                        auth.signOut()
                        finish() // finish the current activity or navigate to another screen
                    } else {
                        Log.w(ContentValues.TAG, "Error deleting user data from the database", task.exception)
                        Toast.makeText(this, "Failed to delete user data. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}