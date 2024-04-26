package com.unity.mynativeapp

import android.content.Intent
import android.graphics.Color
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
    //function to send location
    fun sendLocationMsg(location: String){
        UnityPlayer.UnitySendMessage("Messenger","sendDataToScene",location)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        locationString = intent.getStringExtra("location").toString()
        loadUnityScene(locationString)
        sendLocationMsg(locationString)
        if (locationString != "Naviar/Scenes/Buildings") {
            addControlsToUnityFrame()
        } else {
            addControls2()
        }
        handleIntent(intent)
    }
    private fun loadUnityScene(sceneName: String) {
        UnityPlayer.UnitySendMessage("Messenger", "toScene", sceneName)
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
                                val listAdapter = ArrayAdapter(this@MainUnityActivity, android.R.layout.simple_list_item_1, locationList)
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
            searchView.x = 25f
            searchView.y = 100f
            searchView.setBackgroundColor(Color.WHITE)
            listView.x = 25f
            listView.y = 200f
            listView.setBackgroundColor(Color.WHITE)
            layout.addView(searchView, 920, 100)
            layout.addView(listView, 920, 300)
        }
        run {
            val myButton = Button(this)
            myButton.text = "Search"
            myButton.x = 25f
            myButton.y = 500f
            myButton.setOnClickListener {
                Log.d("checkString",locationString)
                sendLocationMsg(locationString)
            }
            layout.addView(myButton, 300, 200)
        }
        run {
            val myButton = Button(this)
            myButton.text = "Return"
            myButton.x = 335f
            myButton.y = 500f
            myButton.setOnClickListener {
                locationString = ""
                finish()
            }
            layout.addView(myButton, 300, 200)
        }
    }

    fun addControls2() {
        val layout: FrameLayout = mUnityPlayer
        run {
            val myButton = Button(this)
            myButton.text = "Return"
            myButton.x = 335f
            myButton.y = 800f
            myButton.setOnClickListener {
                locationString = ""
                finish()
            }
            layout.addView(myButton, 300, 200)
        }
    }
}