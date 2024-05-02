using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class GpsToUnity : MonoBehaviour
{
    //public string test="40.769759926885385,-73.98177261792353";//Bank
    // Origin coordinates in latitude and longitude

    public Vector2 truncate(double latitude, double longitude)
    {
        // Adjust the multiplier for 7 decimal places
        double truncatedLatitude = System.Math.Truncate(latitude * 10000000) / 10000000;
        double truncatedLongitude = System.Math.Truncate(longitude * 10000000) / 10000000;

        // Return the truncated coordinates as a Vector2
        return new Vector2((float)truncatedLatitude, (float)truncatedLongitude);
    }

    public Vector3 setPoint(double lat, double lon)
    {
        // Truncate origin and target coordinates
        Vector2 originCoordinates = truncate(40.76824754802539, -73.98398336790302);
        Vector2 targetCoordinates = truncate(lat, lon);

        // Conversion factors (rough estimates)
        double LatitudeToMeters = 111111;
        double LongitudeToMeters = 111111;

        // Calculate displacement in latitude and longitude
        double deltaLatitude = targetCoordinates.x - originCoordinates.x;
        double deltaLongitude = targetCoordinates.y - originCoordinates.y;

        // Convert displacement to meters (keep as double)
        double deltaLatitudeInMeters = deltaLatitude * LatitudeToMeters;
        double deltaLongitudeInMeters = deltaLongitude * LongitudeToMeters * System.Math.Cos(originCoordinates.x * Mathf.Deg2Rad);
        Debug.Log(deltaLatitudeInMeters+","+deltaLongitudeInMeters);
        Debug.Log("New Position in Unity Space: " + transform.position);
        return new Vector3((float)deltaLongitudeInMeters, -225.79f, (float)deltaLatitudeInMeters);
        //createPoint(test);
   }
   public Vector3 setPointForStrings(string message)
    {
        string[] msg=message.Split(',');
        double lat=double.Parse(msg[0]);
        double lon=double.Parse(msg[1]);
        // Truncate origin and target coordinates
        Vector2 originCoordinates = truncate(40.76824754802539, -73.98398336790302);
        Vector2 targetCoordinates = truncate(lat, lon);

        // Conversion factors (rough estimates)
        double LatitudeToMeters = 111111;
        double LongitudeToMeters = 111111;

        // Calculate displacement in latitude and longitude
        double deltaLatitude = targetCoordinates.x - originCoordinates.x;
        double deltaLongitude = targetCoordinates.y - originCoordinates.y;

        // Convert displacement to meters (keep as double)
        double deltaLatitudeInMeters = deltaLatitude * LatitudeToMeters;
        double deltaLongitudeInMeters = deltaLongitude * LongitudeToMeters * System.Math.Cos(originCoordinates.x * Mathf.Deg2Rad);
        Debug.Log(deltaLatitudeInMeters+","+deltaLongitudeInMeters);
        Debug.Log("New Position in Unity Space: " + transform.position);
        return new Vector3((float)deltaLongitudeInMeters, -267.91f, (float)deltaLatitudeInMeters);
        //createPoint(test);
   }
}

   

