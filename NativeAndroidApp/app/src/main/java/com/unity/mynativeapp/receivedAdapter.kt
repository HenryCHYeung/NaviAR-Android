package com.unity.mynativeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.example.naviar2.FriendsFragment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class receivedAdapter(private val context: FriendsFragment, private val receivedArray: List<Any>,
                      private val sentStart: Int, private val currentStart: Int, private val userid: Int):
                      ArrayAdapter<Any>(context.requireContext(), R.layout.friends_list_layout, receivedArray) {
    private lateinit var view: View
    private val HEADER = 0
    private val RECEIVED = 1
    private val SENT = 2
    private val CURRENT = 3
    private val mSocket = SocketHandler.getSocket()
    override fun getItemViewType(position: Int): Int {
        return if (receivedArray[position] is String) {
            HEADER
        } else if (position >= currentStart) {
            CURRENT
        } else if (position >= sentStart) {
            SENT
        } else {
            RECEIVED
        }
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context.requireContext())
        view = when (getItemViewType(position)) {
            HEADER -> inflater.inflate(R.layout.friends_list_header, null)
            else -> {
                inflater.inflate(R.layout.friends_list_layout, null)
            }
        }
        if (getItemViewType(position) == HEADER) {
            val displayText = view.findViewById<TextView>(R.id.friendHeading)
            displayText.text = receivedArray[position].toString()
        } else {
            val friendName = view.findViewById<TextView>(R.id.friendName)
            val friendid = view.findViewById<TextView>(R.id.friendid)
            val friendemail = view.findViewById<TextView>(R.id.friendemail)
            val friendObject: friend = receivedArray[position] as friend
            val name = friendObject.first_name + " " + friendObject.last_name
            friendName.text = name
            friendid.text = friendObject.user_ID.toString()
            friendemail.text = friendObject.email
            val button1 = view.findViewById<Button>(R.id.acceptButton)
            val button2 = view.findViewById<Button>(R.id.rejectButton)
            val builder = AlertDialog.Builder(context.requireContext())

            if (getItemViewType(position) == RECEIVED) {
                button1.setOnClickListener {
                    mSocket.emit("acceptRequest", userid, friendObject.user_ID)
                    mSocket.once("acceptRequest") { args ->
                        context.requireActivity().runOnUiThread {
                            if (args[0] != null) {
                                val msg = args[0] as String
                                context.reloadFragment()
                                Toast.makeText(context.requireContext(), msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                button2.setOnClickListener {
                    builder.setTitle("Reject Friend Request")
                    builder.setMessage("Are you sure you want to reject the request?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        mSocket.emit("rejectRequest", userid, friendObject.user_ID)
                        mSocket.once("rejectRequest") { args ->
                            context.requireActivity().runOnUiThread {
                                if (args[0] != null) {
                                    val msg = args[0] as String
                                    context.reloadFragment()
                                    Toast.makeText(context.requireContext(), msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    builder.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                    builder.show()
                }
            } else if (getItemViewType(position) == SENT) {
                Log.d("SENT", "sent")
                button1.isEnabled = false
                button1.visibility = View.GONE
                button2.text = "CANCEL"

                button2.setOnClickListener {
                    builder.setTitle("Cancel Friend Request")
                    builder.setMessage("Are you sure you want to cancel your request?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        mSocket.emit("cancelRequest", userid, friendObject.user_ID)
                        mSocket.once("cancelRequest") { args ->
                            context.requireActivity().runOnUiThread {
                                if (args[0] != null) {
                                    val msg = args[0] as String
                                    context.reloadFragment()
                                    Toast.makeText(context.requireContext(), msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    builder.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                    builder.show()
                }
            } else if (getItemViewType(position) == CURRENT) {
                button1.isEnabled = false
                button1.visibility = View.GONE
                button2.text = "UNFRIEND"

                button2.setOnClickListener {
                    builder.setTitle("Unfriend")
                    builder.setMessage("Are you sure you want to unfriend this person?")
                    builder.setPositiveButton("Yes") { dialog, which ->
                        mSocket.emit("unfriend", userid, friendObject.user_ID)
                        mSocket.once("unfriend") { args ->
                            context.requireActivity().runOnUiThread {
                                if (args[0] != null) {
                                    val msg = args[0] as String
                                    context.reloadFragment()
                                    Toast.makeText(context.requireContext(), msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    builder.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                    builder.show()
                }
            }
        }
        return view
    }
}