package com.example.naviar2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.unity.mynativeapp.databinding.ActivityAddfriendBinding
import android.content.SharedPreferences
import android.content.Context
import android.widget.Toast
import android.widget.Button
import com.unity.mynativeapp.R

class AddActivity : AppCompatActivity() {
    var PREFS_KEY = "prefs"
    var EMAIL_KEY = "email"
    var ID_KEY = "ID"
    var NAME_KEY = "name"
    var userID = 0
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddfriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        userID = sharedPreferences.getInt(ID_KEY, 0)

        val addFriend = findViewById<Button>(R.id.button4)
        addFriend.setOnClickListener {
            Toast.makeText(this@AddActivity, "ID: $userID", Toast.LENGTH_SHORT).show()
        }
    }
}