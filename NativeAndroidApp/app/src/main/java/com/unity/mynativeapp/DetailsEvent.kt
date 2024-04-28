package com.unity.mynativeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.unity.mynativeapp.databinding.EventDetailsBinding

class DetailsEvent: AppCompatActivity() {

    private lateinit var binding: EventDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EventDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventName = "Name: " + intent.getStringExtra("title")
        val eventTime = "Time: " + intent.getStringExtra("start") + " - " + intent.getStringExtra("end")
        val eventBuilding = "Building: " + intent.getStringExtra("building")
        val eventRoom = "Room: " + intent.getStringExtra("room")
        val eventDesc = "Description:\n" + intent.getStringExtra("desc")
        val eventOrganizer = "Organizer: " + intent.getStringExtra("organizer")
        var eventPeople = "None!"
        val peopleArray = intent.getStringArrayExtra("people")
        if (peopleArray != null && peopleArray.isNotEmpty()) {
            // If the array is not null and not empty
            eventPeople = peopleArray.joinToString()
            // Now you can use eventPeople
        }

        binding.eventTitle.text = eventName
        binding.eventTime.text = eventTime
        binding.building.text = eventBuilding
        binding.roomNo.text = eventRoom
        binding.description.text = eventDesc
        binding.organizer.text = eventOrganizer
        binding.peopleList.text = eventPeople
    }
}