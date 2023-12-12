package com.borg.blabble.activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.borg.blabble.R
import com.borg.blabble.databinding.ActivityHomeBinding
import com.borg.blabble.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logo.translationY = 1000f
        binding.textView.translationY = 1000f
        binding.aliasText.translationY = 1000f
        binding.topicBtn.translationY = 1000f

        binding.logo.animate().setDuration(1000).translationY(-120f)
        binding.textView.animate().setDuration(1500).translationY(-100f)
        binding.aliasText.animate().setDuration(2000).translationY(-100f)
        binding.topicBtn.animate().setDuration(2500).translationY(-50f)

        binding.topicBtn.setOnClickListener {
            if (TextUtils.isEmpty(binding.aliasText.text.toString())){
                Toast.makeText(this, "You need to specify an alias", Toast.LENGTH_SHORT).show()
            }
            else {
                signInAnonymously(binding.aliasText.text.toString())
            }
        }
    }

    private fun signInAnonymously(username: String) {
        Firebase.auth.signOut()

        Firebase.auth.signInAnonymously().addOnCompleteListener(this) {
            val userId = Firebase.auth.currentUser!!.uid
            val user = User(username)

            if (it.isSuccessful) {
                // Sign in success
                Log.d(TAG, "Signed in anonymously with UID: $userId")

                // Associate the username with user
                Firebase.database.reference.child("users").child(userId).setValue(user).addOnCompleteListener(this) { it ->
                    if(it.isSuccessful){
                        val i = Intent(this, TopicActivity::class.java)
                        i.putExtra("com.borg.blabble.activity.user", user)
                        startActivity(i)
                    }
                }
            }
            else {
                Log.e(TAG, "Anonymous sign-in failed", it.exception)
                Toast.makeText(this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}