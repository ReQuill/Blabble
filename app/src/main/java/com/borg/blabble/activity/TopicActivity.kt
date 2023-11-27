package com.borg.blabble.activity

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.borg.blabble.databinding.ActivityHomeBinding
import com.borg.blabble.databinding.ActivityTopicBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TopicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTopicBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Set up the back button behavior
//        val onBackPressedCallback = object: OnBackPressedCallback(true){
//            override fun handleOnBackPressed() {
//                // Your custom back button behavior here
//                deleteUserData()
//                // For example, you might want to navigate to a different screen or show a confirmation dialog
//                finish()
//            }
//        }
//        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

//    private fun deleteUserData() {
//        val user = auth.currentUser
//
//        if (user != null) {
//            // Delete user data from the database
//            val databaseReference = FirebaseDatabase.getInstance("https://your-firebase-project-id.firebaseio.com").getReference("users")
//            val userReference = databaseReference.child(user.uid)
//
//            userReference.removeValue()
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Log.d(ContentValues.TAG, "User data deleted from the database")
//                        // Optionally, sign the user out after deleting data
//                        auth.signOut()
//                        finish() // finish the current activity or navigate to another screen
//                    } else {
//                        Log.w(ContentValues.TAG, "Error deleting user data from the database", task.exception)
//                        Toast.makeText(this, "Failed to delete user data. Please try again.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
//    }
}