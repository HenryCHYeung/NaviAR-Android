package com.example.naviar2

import android.os.Bundle
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import com.unity.mynativeapp.MainActivity
import com.unity.mynativeapp.R
import com.unity.mynativeapp.databinding.ActivityUiBinding
import android.content.SharedPreferences
import android.content.Context
import com.unity.mynativeapp.SocketHandler
import android.widget.Toast
import android.util.Log


class UiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUiBinding
    var PREFS_KEY = "prefs"
    var EMAIL_KEY = "email"
    var ID_KEY = "ID"
    var NAME_KEY = "name"
    var username = ""
    var userid = 0
    lateinit var sharedPreferences: SharedPreferences
    override fun onBackPressed() {
        showLogoutConfirmationDialog()
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { dialog, which -> logout() }
        builder.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
        builder.show()
    }

    private fun logout() {
        // Clear any user data or session here
        // For example, clear SharedPreferences, reset variables, etc.
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        // Navigate back to the login screen
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(MapFragment())
        val bundle = intent.extras
        var name = bundle!!.getString("name")
        val mSocket = SocketHandler.getSocket()
        if (!name.equals("Guest")) {
            sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
            username = sharedPreferences.getString(NAME_KEY, null)!!
            userid = sharedPreferences.getInt(ID_KEY, 0)
            mSocket.emit("getRequested", userid)
            mSocket.on("getRequested") { args ->
                if (args[0] != null) {
                    val friendslist = args[0]
                    runOnUiThread {
                        Log.d("HELLO2", "$friendslist")
                    }
                }
            }
        }
        supportActionBar?.title = "Welcome, $name!"

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.map -> {
                    replaceFragment(MapFragment())
                }
                R.id.campus -> {
                    replaceFragment(CampusFragment())
                }
                R.id.events -> {
                    replaceFragment(EventFragment())
                }
                R.id.friends -> {
                    replaceFragment(FriendsFragment())
                }
            }
            true // Return true explicitly
        }

    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
