package com.unity.mynativeapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.unity.mynativeapp.databinding.EventDetailsBinding

class DetailsEvent: AppCompatActivity() {

    private lateinit var binding: EventDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EventDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mSocket = SocketHandler.getSocket()

        val eventName = intent.getStringExtra("title")
        val eventNameDisplay = "Name: $eventName"
        val eventTime = "Time: " + intent.getStringExtra("start") + " - " + intent.getStringExtra("end")
        val eventBuilding = "Building: " + intent.getStringExtra("building")
        val eventRoom = "Room: " + intent.getStringExtra("room")
        val eventDesc = "Description:\n" + intent.getStringExtra("desc")
        val eventOrganizer = "Organizer: " + intent.getStringExtra("organizer")
        val organizerID = intent.getIntExtra("organizerID", 0)
        val studentID = intent.getIntExtra("studentID", 0)
        var eventPeople = "No one yet!"
        val peopleArray = intent.getStringArrayExtra("people")
        if (!peopleArray.isNullOrEmpty()) {
            // If the array is not null and not empty
            eventPeople = peopleArray.joinToString()
            // Now you can use eventPeople
        }

        binding.eventTitle.text = eventNameDisplay
        binding.eventTime.text = eventTime
        binding.building.text = eventBuilding
        binding.roomNo.text = eventRoom
        binding.description.text = eventDesc
        binding.organizer.text = eventOrganizer
        binding.peopleList.text = eventPeople

        val button = binding.joinEvent
        button.setOnClickListener {
            if (studentID == organizerID) {
                Toast.makeText(this@DetailsEvent, "Can't register for your own event.", Toast.LENGTH_SHORT).show()
            } else {
                mSocket.emit("joinEvent", studentID, eventName)
                mSocket.once("joinEvent") { args ->
                    runOnUiThread {
                        if (args[0] != null) {
                            val msg = args[0] as String
                            Toast.makeText(this@DetailsEvent, msg, Toast.LENGTH_SHORT).show()
                            if (msg == "Registration successful.") {
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }
}