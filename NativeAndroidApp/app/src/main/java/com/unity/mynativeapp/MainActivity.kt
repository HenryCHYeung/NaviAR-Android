package com.unity.mynativeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.naviar2.UiActivity
import android.content.SharedPreferences
import android.content.Context

class MainActivity : AppCompatActivity() {
    var PREFS_KEY = "prefs"
    var EMAIL_KEY = "email"
    var PWD_KEY = "pwd"
    var ID_KEY = "ID"
    var NAME_KEY = "name"
    var ORG_KEY = "is-org"
    var savedEmail = ""
    var savedPwd = ""
    var savedID = 0
    var savedName = ""
    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SocketHandler.setSocket()
        SocketHandler.establishConnection()

        val guestLogin = findViewById<Button>(R.id.guestLogin)
        val userLogin = findViewById<Button>(R.id.button)
        val mSocket = SocketHandler.getSocket()
        guestLogin.setOnClickListener {
            val intent = Intent(this, UiActivity::class.java)
            intent.putExtra("name", "Guest");
            startActivity(intent)
        }
        var sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        savedEmail = sharedPreferences.getString(EMAIL_KEY, "").toString()
        savedPwd = sharedPreferences.getString(PWD_KEY, "").toString()
        savedID = sharedPreferences.getInt(ID_KEY, 0)
        savedName = sharedPreferences.getString(NAME_KEY, "").toString()

        userLogin.setOnClickListener {
            val userEmail = findViewById<EditText>(R.id.editTextTextEmailAddress).text
            val userPass = findViewById<EditText>(R.id.editTextTextPassword).text
            mSocket.emit("login", userEmail, userPass)
            mSocket.on("login") { args ->
                if (args[0] != null) {
                    val isLogged = args[0] as Boolean
                    val username = args[1] as String
                    val stuID = args[2] as Int
                    val isOrganizer = args[3] as Int
                    runOnUiThread {
                        if (isLogged) {
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putString(EMAIL_KEY, userEmail.toString())
                            editor.putString(PWD_KEY, userPass.toString())
                            editor.putInt(ID_KEY, stuID)
                            editor.putString(NAME_KEY, username)
                            editor.putInt(ORG_KEY, isOrganizer)
                            editor.apply()

                            val intent = Intent(this, UiActivity::class.java)
                            intent.putExtra("name", username)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@MainActivity, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }
    override fun onStart() {
        super.onStart()
        // in this method we are checking if email and pwd are not empty.
        if (savedEmail != "" && savedPwd != "" && savedID != 0 && savedName != "") {
            val intent = Intent(this, UiActivity::class.java)
            intent.putExtra("name", savedName)
            intent.putExtra("stuID", savedID)
            startActivity(intent)
        }
    }

}