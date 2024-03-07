package com.unity.mynativeapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.naviar2.UiActivity

class MainActivity : AppCompatActivity() {
    var isUnityLoaded = false
    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SocketHandler.setSocket()
        SocketHandler.establishConnection()

        var guestLogin = findViewById<Button>(R.id.guestLogin)
        var userLogin = findViewById<Button>(R.id.button)
        val mSocket = SocketHandler.getSocket()
        guestLogin.setOnClickListener {
            val intent = Intent(this, UiActivity::class.java)
            startActivity(intent)
        }
        userLogin.setOnClickListener {
            var userEmail = findViewById<EditText>(R.id.editTextTextEmailAddress).text
            var userPass = findViewById<EditText>(R.id.editTextTextPassword).text
            mSocket.emit("login", userEmail, userPass)
            mSocket.on("login") { args ->
                if (args[0] != null) {
                    val islogged = args[0] as Boolean
                    val username = args[1] as String
                    runOnUiThread {
                        if (islogged) {
                            Toast.makeText(this@MainActivity, username, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@MainActivity, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

}