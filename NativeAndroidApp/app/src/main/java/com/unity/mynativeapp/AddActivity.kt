package com.example.naviar2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.unity.mynativeapp.databinding.ActivityAddfriendBinding

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddfriendBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}