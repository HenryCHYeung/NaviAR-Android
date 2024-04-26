using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class gpsTest : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {
        //40.76809625949448, -73.9818938027488
        float earthRadius = 6371000f;
        float latitude=40.76809f;
        float longitude=-73.98189f;
        // Convert latitude and longitude to radians
        float latRad = latitude * Mathf.Deg2Rad;
        float lonRad = longitude * Mathf.Deg2Rad;

        float x =(float).00001*earthRadius * Mathf.Cos(latRad) * Mathf.Cos(lonRad);
        float y =(float).00001*earthRadius * Mathf.Cos(latRad) * Mathf.Sin(lonRad);
        float z =(float).00001*earthRadius * Mathf.Sin(latRad);
        Debug.Log(x+","+y+","+z);
    }
}
