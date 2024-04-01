package com.unity.mynativeapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.gson.GsonBuilder
import com.unity.mynativeapp.databinding.ActivityAddEventBinding
import org.json.JSONArray
import java.util.Calendar
import android.content.SharedPreferences
import android.content.Context

class AddEventActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAddEventBinding
    private var pickedDateTimeString = ""
    private lateinit var buildingArray: Array<String>
    private lateinit var selectedBuilding: String
    private lateinit var roomArray: Array<String>
    private lateinit var selectedRoom: String
    private lateinit var sharedPreferences: SharedPreferences
    private var PREFS_KEY = "prefs"
    private var ID_KEY = "ID"
    private var userID = 0
    private var toastMessage: Toast? = null

    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
                val realMonth = month + 1
                var monthString = ""
                monthString = if (realMonth <= 10) {
                    "0$realMonth"
                } else {
                    realMonth.toString()
                }
                pickedDateTimeString = "$year-$monthString-$day $hour:$minute"
                val dateTimeDisplay = findViewById<TextView>(R.id.datetimeset)
                dateTimeDisplay.text = pickedDateTimeString
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        userID = sharedPreferences.getInt(ID_KEY, 0)
        val mSocket = SocketHandler.getSocket()
        mSocket.emit("getBuildings")
        mSocket.on("getBuildings") { args ->
            if (args[0] != null) {
                val buildings = args[0] as JSONArray
                runOnUiThread {
                    buildingArray = Array(buildings.length() + 1) {""}
                    for (i in 1 until buildingArray.size) {
                        buildingArray[i] = buildings.getJSONObject(i - 1).getString("building_name")
                    }
                    binding = ActivityAddEventBinding.inflate(layoutInflater)
                    setContentView(binding.root)
                    val s = findViewById<Spinner>(R.id.spinner)
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, buildingArray)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    s.adapter = adapter

                    val roomSpin = findViewById<Spinner>(R.id.roomSpin)

                    s.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                        ) {
                            selectedBuilding = buildingArray[position]
                            roomSpin.isEnabled = selectedBuilding.isNotEmpty()
                            mSocket.emit("getRooms", selectedBuilding)
                            mSocket.on("getRooms") { args ->
                                if (args[0] != null) {
                                    val rooms = args[0] as JSONArray
                                    runOnUiThread {
                                        roomArray = Array(rooms.length() + 1) {""}
                                        for (j in 1 until roomArray.size) {
                                            roomArray[j] = rooms.getJSONObject(j - 1).getString("room_no")
                                        }
                                        val s2 = findViewById<Spinner>(R.id.roomSpin)
                                        val adapter2 = ArrayAdapter(this@AddEventActivity, android.R.layout.simple_spinner_item, roomArray)
                                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        s2.adapter = adapter2

                                        s2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                            override fun onItemSelected(
                                                    parent: AdapterView<*>?,
                                                    view: View?,
                                                    position: Int,
                                                    id: Long
                                            ) {
                                                selectedRoom = roomArray[position]
                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>?) {

                                            }
                                        }
                                    }
                                }
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // Handle case where nothing is selected, you can choose to enable or disable the text field here
                            roomSpin.isEnabled = false
                        }
                    }

                    val datepick = findViewById<Button>(R.id.editButton)
                    datepick.setOnClickListener {
                        pickDateTime()
                    }
                    val createButton = findViewById<Button>(R.id.createButton)
                    createButton.setOnClickListener {
                        val eventName = findViewById<EditText>(R.id.editTextText5).text
                        val eventDesc = findViewById<EditText>(R.id.editTextText8).text
                        var blankMsg = ""

                        if (eventName.isEmpty()) {
                            blankMsg = "Please enter a name for your event."
                        } else if (selectedBuilding.isEmpty()) {
                            blankMsg = "Please select a building."
                        } else if (selectedRoom.isEmpty()) {
                            blankMsg = "Please select a room."
                        } else if (pickedDateTimeString == "") {
                            blankMsg = "Please select a date and a time"
                        } else if (eventDesc.isEmpty()) {
                            blankMsg = "Please enter a description for your event."
                        } else {
                            mSocket.emit("createEvent", eventName, selectedBuilding, selectedRoom, pickedDateTimeString, eventDesc, userID)
                            mSocket.on("createEvent") { args ->
                                if (args[0] != null) {
                                    val msg = args[0] as String
                                    runOnUiThread {
                                        toastMessage?.cancel()
                                        toastMessage = Toast.makeText(this@AddEventActivity, msg, Toast.LENGTH_LONG)
                                        toastMessage!!.show()
                                    }
                                }
                            }
                        }

                        if (blankMsg != "") {
                            toastMessage?.cancel()
                            toastMessage = Toast.makeText(this@AddEventActivity, blankMsg, Toast.LENGTH_SHORT)
                            toastMessage!!.show()
                        }
                    }
                }
            }
        }
    }
}