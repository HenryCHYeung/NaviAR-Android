package com.unity.mynativeapp

data class location(var building_name: String, var room_no: String, var on_floor: String, var room_type: String,
                    var loc_desc: String) {
    override fun toString(): String {
        var building = ""
        building = if (building_name == "Edward Guiliano Global Center 1855 Broadway") {
            "EGGC"
        } else if (building_name == "16W. 61st Street") {
            "16W"
        } else if (building_name == "26W. 61st Street") {
            "26W"
        } else {
            "Student Activities"
        }
        var endString = loc_desc
        var typeString = ", $room_type"
        if (room_type == "Other") {
            typeString = ""
        }
        if (loc_desc != "") {
            endString = ", $loc_desc"
        }
        return "$building, $room_no$typeString$endString"
    }

    fun unityString(): String {
        return "$building_name,$on_floor,$room_no"
    }
}

