package com.unity.mynativeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.naviar2.EventFragment

class eventAdapter(private val context: EventFragment, private val receivedArray: List<Any>, private val userStart: Int,
                   private val notJoinedStart: Int, private val userid: Int):
                    ArrayAdapter<Any>(context.requireContext(), R.layout.events_list_layout, receivedArray) {
    private lateinit var view: View
    private val HEADER = 0
    private val USERCREATED = 1
    private val USERJOINED = 2
    private val NOTJOINED = 3
    private val mSocket = SocketHandler.getSocket()

    override fun getItemViewType(position: Int): Int {
        return if (receivedArray[position] is String) {
            HEADER
        } else if (userStart > 1 && position < userStart) {
            USERCREATED
        } else if (position >= notJoinedStart){
            NOTJOINED
        } else {
            USERJOINED
        }

    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context.requireContext())
        view = when (getItemViewType(position)) {
            HEADER -> inflater.inflate(R.layout.friends_list_header, null)
            else -> {
                inflater.inflate(R.layout.events_list_layout, null)
            }
        }
        if (getItemViewType(position) == HEADER) {
            val displayText = view.findViewById<TextView>(R.id.friendHeading)
            displayText.text = receivedArray[position].toString()
        } else {
            val eventName = view.findViewById<TextView>(R.id.eventName)
            val eventTime = view.findViewById<TextView>(R.id.eventTime)
            val organizer = view.findViewById<TextView>(R.id.organizer)
            val eventObject: event = receivedArray[position] as event
            eventName.text = eventObject.event_name
            val timeSpan = eventObject.start_time + " - " + eventObject.end_time
            eventTime.text = timeSpan
            val name = "Organizer: " + eventObject.first_name + " " + eventObject.last_name
            organizer.text = name
            val button = view.findViewById<Button>(R.id.eventButton)
            val builder = AlertDialog.Builder(context.requireContext())

            if (getItemViewType(position) == USERCREATED) {
                button.text = "DELETE"
                button.setOnClickListener {
                    builder.setTitle("Delete Event")
                    builder.setMessage("Are you sure you want to delete this event?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        mSocket.emit("deleteEvent", userid, eventObject.event_name)
                        mSocket.once("deleteEvent") { args ->
                            // reload fragment (may need RunOnUiThread)
                        }
                    }
                    builder.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                    builder.show()
                }
            }else if (getItemViewType(position) == USERJOINED) {
                button.text = "CANCEL"
                button.setOnClickListener {
                    builder.setTitle("Withdraw from event")
                    builder.setMessage("Are you sure you want to withdraw from this event?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        mSocket.emit("withdrawEvent", userid, eventObject.event_name)
                        mSocket.once("withdrawEvent") { args ->
                            // reload fragment (may need RunOnUiThread)
                        }
                    }
                    builder.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                    builder.show()
                }
            } else if (getItemViewType(position) == NOTJOINED) {
                button.text = "JOIN"
                button.setOnClickListener {
                    mSocket.emit("joinEvent", userid, eventObject.event_name)
                    mSocket.once("joinEvent") { args ->
//                        if (args[0] != null) {
//                            val msg = args[0] as String
//                            Toast.makeText(context.requireContext(), msg, Toast.LENGTH_SHORT).show()
//                        }
                    }
                }
            }
        }

        return view
    }
}