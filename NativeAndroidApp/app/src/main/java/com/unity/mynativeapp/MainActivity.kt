package com.unity.mynativeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.naviar2.UiActivity

class MainActivity : AppCompatActivity() {
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
        userLogin.setOnClickListener {
            val userEmail = findViewById<EditText>(R.id.editTextTextEmailAddress).text
            val userPass = findViewById<EditText>(R.id.editTextTextPassword).text
            mSocket.emit("login", userEmail, userPass)
            mSocket.on("login") { args ->
                if (args[0] != null) {
                    val isLogged = args[0] as Boolean
                    val username = args[1] as String
                    runOnUiThread {
                        if (isLogged) {
                            val intent = Intent(this, UiActivity::class.java)
                            intent.putExtra("name", username);
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@MainActivity, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

}