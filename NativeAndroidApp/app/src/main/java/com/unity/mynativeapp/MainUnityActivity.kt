package com.unity.mynativeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import com.company.product.OverrideUnityActivity
import com.google.gson.GsonBuilder
import com.unity3d.player.UnityPlayer
import org.json.JSONArray

class MainUnityActivity : OverrideUnityActivity() {
    // Setup activity layout

    private var locationString = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addControlsToUnityFrame()
        val intent = intent
        locationString = intent.getStringExtra("location").toString()
        Log.d("Location", locationString)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
        setIntent(intent)
    }

    fun handleIntent(intent: Intent?) {
        if (intent == null || intent.extras == null) return
        if (intent.extras!!.containsKey("doQuit")) if (mUnityPlayer != null) {
            finish()
        }
    }

    override fun showMainActivity(setToColor: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("setColor", setToColor)
        startActivity(intent)
    }

    override fun onUnityPlayerUnloaded() {
        showMainActivity("")
    }

    fun addControlsToUnityFrame() {
        val layout: FrameLayout = mUnityPlayer
        run {
            val searchView = SearchView(this)
            val listView = ListView(this)
            SocketHandler.setSocket()
            SocketHandler.establishConnection()
            val mSocket = SocketHandler.getSocket()
            var selectedLoc = location("", "", "", "", "")
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    locationString = selectedLoc.unityString()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    mSocket.emit("searchQuery", newText)
                    mSocket.once("searchQuery") { args ->
                        if (args[0] != null) {
                            val allList = args[0] as JSONArray

                            runOnUiThread {
                                val gson = GsonBuilder().create()
                                val locationList = gson.fromJson(allList.toString(), Array<location>::class.java).toList()
                                val listAdapter = ArrayAdapter<location>(this@MainUnityActivity, android.R.layout.simple_list_item_1, locationList)
                                listView.adapter = listAdapter
                                listView.setOnItemClickListener { parent, view, position, id ->
                                    selectedLoc = locationList[position]
                                    searchView.setQuery(locationList[position].toString(), true)
                                }
                            }
                        }
                    }
                    return false
                }
            })
            searchView.x = 10f
            searchView.y = 500f
            listView.x = 20f
            listView.y = 700f
            layout.addView(searchView, 300, 200)
            layout.addView(listView, 300, 200)
        }
        run {
            val myButton = Button(this)
            myButton.text = "Send Msg"
            myButton.x = 320f
            myButton.y = 500f
            myButton.setOnClickListener {
                UnityPlayer.UnitySendMessage(
                    "Cube",
                    "ChangeColor",
                    "yellow"
                )
            }
            layout.addView(myButton, 300, 200)
        }
        run {
            val myButton = Button(this)
            myButton.text = "Unload"
            myButton.x = 630f
            myButton.y = 500f
            myButton.setOnClickListener { mUnityPlayer.unload() }
            layout.addView(myButton, 300, 200)
        }
        run {
            val myButton = Button(this)
            myButton.text = "Finish"
            myButton.x = 630f
            myButton.y = 800f
            myButton.setOnClickListener { finish() }
            layout.addView(myButton, 300, 200)
        }
    }
}