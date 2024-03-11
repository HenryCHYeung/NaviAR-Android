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


class UiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUiBinding

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
        val name = bundle!!.getString("name")
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
