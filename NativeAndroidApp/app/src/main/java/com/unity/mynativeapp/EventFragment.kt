package com.example.naviar2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.gson.GsonBuilder
import com.unity.mynativeapp.AddEventActivity
import com.unity.mynativeapp.DetailsEvent
import com.unity.mynativeapp.R
import com.unity.mynativeapp.SocketHandler
import com.unity.mynativeapp.databinding.FragmentEventBinding
import com.unity.mynativeapp.event
import com.unity.mynativeapp.eventAdapter
import org.json.JSONArray
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EventFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var userid: Int? = null
    private var is_organizer: Int? = null
    private lateinit var binding: FragmentEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userid = it.getInt("USERID")
            is_organizer = it.getInt("ISORGANIZER")
        }
    }

    override fun onResume() {
        super.onResume()
        if (userid != 0) {
            loadData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEventBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (userid != 0) {
            loadData()
        } else {
            binding.textView2.text = "Events are only visible to NYIT students."
        }
        if (is_organizer == 1) {
            view.findViewById<Button>(R.id.addEvent)?.setOnClickListener {
                val intent = Intent(activity, AddEventActivity::class.java)
                startActivity(intent)
            }
        } else {
            view.findViewById<Button>(R.id.addEvent).visibility = View.GONE
        }
    }

    fun loadData() {
        Log.d("CHECK", is_organizer.toString())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val current = LocalDateTime.now().format(formatter)
        val mSocket = SocketHandler.getSocket()
        mSocket.emit("getEvents", userid, is_organizer, current)
        mSocket.once("getEvents") { args ->
            if (args[0] != null) {
                val createdByUser = args[0] as JSONArray
                val userJoined = args[1] as JSONArray
                val peoplePerEvent = args[2] as JSONArray
                val friendsJoined = args[3] as JSONArray
                val otherEvents = args[4] as JSONArray

                runOnUiThread {
                    var result = listOf<Any>()
                    var userStart = 1
                    val gson = GsonBuilder().create()
                    if (is_organizer == 1) {
                        val createdByUserList = gson.fromJson(createdByUser.toString(), Array<event>::class.java).toList()
                        result = result + "Created by You" + createdByUserList
                        userStart = result.size + 1
                    }
                    val friendsJoinedList = gson.fromJson(friendsJoined.toString(), Array<event>::class.java).toList()
                    val peoplePerEventList = Array(peoplePerEvent.length()) {mutableListOf<String>()}
                    for (i in 0 until peoplePerEvent.length()) {
                        val innerArray = peoplePerEvent.getJSONArray(i)
                        for (j in 0 until innerArray.length()) {
                            val name = innerArray.getJSONObject(j).getString("first_name") + " " +
                                    innerArray.getJSONObject(j).getString("last_name")
                            peoplePerEventList[i].add(name)
                        }
                    }
                    val userJoinedList = gson.fromJson(userJoined.toString(), Array<event>::class.java).toList()
                    val otherEventsList = gson.fromJson(otherEvents.toString(), Array<event>::class.java).toList()
                    val userHeader = "You joined"
                    val friendHeader = "Your friends joined"
                    val otherHeader = "All future events"
                    result = result + userHeader + userJoinedList + friendHeader + friendsJoinedList + otherHeader + otherEventsList
                    Log.d("result", result.toString())
                    val notJoinedStart = userStart + userJoinedList.size + 1
                    val friendStart = notJoinedStart + friendsJoinedList.size + 1
                    binding.listview.adapter = eventAdapter(this, result, userStart, notJoinedStart, userid!!)
                    binding.listview.isClickable = true
                    binding.listview.setOnItemClickListener { parent, view, position, id ->
                        if (result[position] !is String) {
                            val eventObject: event = result[position] as event
                            val eventTitle = eventObject.event_name
                            val eventStart = eventObject.start_time
                            val eventEnd = eventObject.end_time
                            val eventBuilding = eventObject.building_name
                            val eventRoom = eventObject.room_no
                            val eventDesc = eventObject.event_desc
                            val eventOrganizer = eventObject.first_name + " " + eventObject.last_name
                            val organizerID = eventObject.organizer

                            val peopleInEvent: MutableList<String> = if (userStart > 1 && position < userStart) {
                                peoplePerEventList[position - 1]
                            } else if (position < notJoinedStart) {
                                peoplePerEventList[position - 2]
                            } else if (position < friendStart) {
                                peoplePerEventList[position - 3]
                            } else {
                                peoplePerEventList[position - 4]
                            }
                            val intent = Intent(this.requireContext(), DetailsEvent::class.java)
                            intent.putExtra("title", eventTitle)
                            intent.putExtra("start", eventStart)
                            intent.putExtra("end", eventEnd)
                            intent.putExtra("building", eventBuilding)
                            intent.putExtra("room", eventRoom)
                            intent.putExtra("desc", eventDesc)
                            intent.putExtra("organizer", eventOrganizer)
                            intent.putExtra("people", peopleInEvent.toTypedArray())
                            intent.putExtra("organizerID", organizerID)
                            intent.putExtra("studentID", userid)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param userid Parameter 1.
         * @return A new instance of fragment EventFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(userid: Int, isOrganizer: Int) =
            EventFragment().apply {
                arguments = Bundle().apply {
                    putInt("USERID", userid)
                    putInt("ISORGANIZER", isOrganizer)
                }
            }
    }
}