package com.example.naviar2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.unity.mynativeapp.MainActivity
import com.unity.mynativeapp.R
import com.unity.mynativeapp.databinding.ActivityUiBinding


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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) { // Use your own menu item ID
            // Handle logout action here
            showLogoutConfirmationDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
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
        if (!name.equals("Guest")) {
            sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
            username = sharedPreferences.getString(NAME_KEY, null)!!
            userid = sharedPreferences.getInt(ID_KEY, 0)
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
                    replaceFragment(FriendsFragment.newInstance(userid))
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
