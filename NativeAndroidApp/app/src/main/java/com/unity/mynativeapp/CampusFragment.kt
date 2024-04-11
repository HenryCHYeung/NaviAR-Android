package com.example.naviar2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil.setContentView
import com.google.gson.GsonBuilder
import com.unity.mynativeapp.MainUnityActivity
import com.unity.mynativeapp.R
import com.unity.mynativeapp.SocketHandler
import com.unity.mynativeapp.databinding.FragmentCampusBinding
import com.unity.mynativeapp.event
import com.unity.mynativeapp.location
import org.json.JSONArray

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CampusFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CampusFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentCampusBinding
    private lateinit var listAdapter: ArrayAdapter<location>
    private lateinit var locationList: List<location>;
    private lateinit var searchView: SearchView
    private var selectedLoc: location = location("", "", "", "", "")
    private var unityString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCampusBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mSocket = SocketHandler.getSocket()
        locationList = listOf()
        searchView = view.findViewById(R.id.searchBar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val selected = view.findViewById<TextView>(R.id.selected)
                selected.text = "Selected Location: $query"
                unityString = selectedLoc.unityString()
                Log.d("UNITYSTR", unityString)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mSocket.emit("searchQuery", newText)
                mSocket.once("searchQuery") { args ->
                    if (args[0] != null) {
                        val allList = args[0] as JSONArray

                        runOnUiThread {
                            val gson = GsonBuilder().create()
                            locationList = gson.fromJson(allList.toString(), Array<location>::class.java).toList()
                            listAdapter = ArrayAdapter<location>(requireContext(), android.R.layout.simple_list_item_1, locationList)
                            binding.locList.adapter = listAdapter
                            binding.locList.setOnItemClickListener { parent, view, position, id ->
                                selectedLoc = locationList[position]
                                searchView.setQuery(locationList[position].toString(), true)
                            }
                        }
                    }
                }
                return false
            }
        })

        view.findViewById<Button>(R.id.unityButton2)?.setOnClickListener {
            val intent = Intent(activity, MainUnityActivity::class.java)
            val example = "16W. 61st Street,8,822"
            intent.putExtra("location", example)
            startActivity(intent)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CampusFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CampusFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }


}