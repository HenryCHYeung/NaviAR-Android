package com.example.naviar2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.unity.mynativeapp.R
import com.unity.mynativeapp.databinding.ActivityUiBinding
import com.google.android.material.navigation.NavigationView
import android.view.MenuItem
import android.widget.Button
import com.unity.mynativeapp.MainUnityActivity


class UiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUiBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(MapFragment())

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
