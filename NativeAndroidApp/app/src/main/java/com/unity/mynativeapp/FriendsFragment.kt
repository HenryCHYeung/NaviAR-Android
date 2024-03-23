package com.example.naviar2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.unity.mynativeapp.R
import android.util.Log
import com.google.gson.GsonBuilder
import org.json.JSONArray
import com.unity.mynativeapp.SocketHandler
import com.unity.mynativeapp.databinding.FragmentFriendsBinding
import com.unity.mynativeapp.friend
import com.unity.mynativeapp.receivedAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FriendsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var userid: Int? = null
    private lateinit var binding: FragmentFriendsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userid = it.getInt("USERID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFriendsBinding.inflate(layoutInflater)
        return binding.root
    }

    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("fragmenttest", "$userid")
        val mSocket = SocketHandler.getSocket()
        var sentRequestsList: JSONArray
        var receivedRequestsList: JSONArray
        var currentFriendsList: JSONArray
        mSocket.emit("getRequests", userid)
        mSocket.on("getRequests") { args ->
            if (args[0] != null) {
                val sentRequests = args[0] as JSONArray         // People the user has sent friend requests to (can cancel)
                val receivedRequests = args[1] as JSONArray     // People that sent friend requests to the user (can accept or reject)
                val currentFriends = args[2] as JSONArray       // Current friends of the user (can unfriend)
                runOnUiThread {
                    val gson = GsonBuilder().create()
                    val sentRequestsList = gson.fromJson(sentRequests.toString(), Array<friend>::class.java).toList()
                    val receivedRequestsList = gson.fromJson(receivedRequests.toString(), Array<friend>::class.java).toList()
                    val currentFriendsList = gson.fromJson(currentFriends.toString(), Array<friend>::class.java).toList()
                    val receiveHeader = "Received Requests"
                    val sentHeader = "Sent Requests"
                    val currentHeader = "Current Friends"
                    var result = listOf<Any>(receiveHeader)
                    result = result + receivedRequestsList + sentHeader + sentRequestsList + currentHeader + currentFriendsList
                    val sentStart = receivedRequestsList.size + 2
                    val currentStart = sentStart + sentRequestsList.size + 1
                    Log.d("FULL_LIST", "$result")
                    binding.listview.adapter = receivedAdapter(this, result, sentStart, currentStart, userid!!)
                }
            }
        }

        view.findViewById<Button>(R.id.addButton)?.setOnClickListener {
            val intent = Intent(activity, AddActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param userid Parameter 1.
         * @return A new instance of fragment FriendsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(userid: Int) =
            FriendsFragment().apply {
                arguments = Bundle().apply {
                    putInt("USERID", userid)
                }
            }
    }
}