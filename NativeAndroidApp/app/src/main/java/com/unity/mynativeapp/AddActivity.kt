package com.example.naviar2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.unity.mynativeapp.databinding.ActivityAddfriendBinding
import android.content.SharedPreferences
import android.content.Context
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import com.unity.mynativeapp.R
import com.unity.mynativeapp.SocketHandler

class AddActivity : AppCompatActivity() {
    var PREFS_KEY = "prefs"
    var ID_KEY = "ID"
    var userID = 0
    lateinit var sharedPreferences: SharedPreferences
    var toastMessage: Toast? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddfriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        userID = sharedPreferences.getInt(ID_KEY, 0)
        val mSocket = SocketHandler.getSocket()

        val addFriend = findViewById<Button>(R.id.button4)
        addFriend.setOnClickListener {
            val inputID = findViewById<EditText>(R.id.editTextNumber).text
            mSocket.emit("addFriend", userID, inputID)
            mSocket.on("addFriend") { args ->
                if (args[0] != null) {
                    val msg = args[0] as String
                    runOnUiThread {
                        toastMessage?.cancel()
                        toastMessage = Toast.makeText(this@AddActivity, "$msg", Toast.LENGTH_SHORT)
                        toastMessage!!.show()
                    }
                }
            }
        }
    }
}