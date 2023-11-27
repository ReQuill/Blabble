package com.borg.blabble.activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.borg.blabble.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.topicBtn.setOnClickListener(){
            if(TextUtils.isEmpty(binding.aliasText.text.toString())){
                Toast.makeText(this, "You need to specify an alias", Toast.LENGTH_SHORT).show()
            }
            else{
                signInAnonymously(binding.aliasText.text.toString())
            }
        }
    }

    private fun signInAnonymously(username:String){
        auth.signOut()

        auth.signInAnonymously()
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    //sign in success
                    var user: FirebaseUser? = auth.currentUser
                    Log.d(TAG, "Signed in anonymously with UID: ${user?.uid}")

                    //associate the username with user
                    databaseReference = FirebaseDatabase.getInstance("https://blabble-5d037-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")
                    databaseReference.child(user!!.uid).child("username").setValue(username)
                        .addOnCompleteListener(this){
                            if(it.isSuccessful){
                                val i = Intent(this, TopicActivity::class.java)
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