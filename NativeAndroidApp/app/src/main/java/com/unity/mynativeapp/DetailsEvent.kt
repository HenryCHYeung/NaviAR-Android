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
        val eventStart = "Start Time: " + intent.getStringExtra("start")
        val eventEnd = "End Time: " + intent.getStringExtra("end")
        val eventBuilding = "Building: " + intent.getStringExtra("building")
        val eventRoom = "Room: " + intent.getStringExtra("room")
        val eventDesc = "Description:\n" + intent.getStringExtra("desc")
        val eventOrganizer = "Organizer: " + intent.getStringExtra("organizer")
        val eventPeople = intent.getStringArrayExtra("people")!!.joinToString()

        binding.eventTitle.text = eventName
        binding.eventStart.text = eventStart
        binding.eventEnd.text = eventEnd
        binding.building.text = eventBuilding
        binding.roomNo.text = eventRoom
        binding.description.text = eventDesc
        binding.organizer.text = eventOrganizer
        binding.peopleList.text = eventPeople
    }
}