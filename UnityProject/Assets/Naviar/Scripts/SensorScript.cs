using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.InputSystem;
using UnityEngine.Android;
using TMPro;

public class SensorScript : MonoBehaviour
{
    public TMP_Text gpsText;
    public bool isUpdating;
    private Vector3 userLocation;
    private GpsToUnity convertor;
    void Start(){
        convertor= gameObject.GetComponent<GpsToUnity>();
    }
    void Update(){
        if(!isUpdating){
        StartCoroutine(GpsStart());
        isUpdating=!isUpdating;
        }
    }
    IEnumerator GpsStart()
    {
        //Start accelerometer
        InputSystem.EnableDevice(Accelerometer.current);
        var acceleration = Accelerometer.current.acceleration.ReadValue();
        Debug.Log(acceleration.ToString());
        //Get Compass
        Debug.Log(Input.compass.rawVector.ToString());
        if(!Permission.HasUserAuthorizedPermission(Permission.FineLocation)){
            Debug.Log("Location not enabled on device or app does not have permission to access location");
            Permission.RequestUserPermission(Permission.FineLocation);
            Permission.RequestUserPermission(Permission.CoarseLocation);
        }
        if (!Input.location.isEnabledByUser){
            yield return new WaitForSeconds(5);
        }
        Input.location.Start();
        // Waits until the location service initializes
        int maxWait = 20;
        while (Input.location.status == LocationServiceStatus.Initializing && maxWait > 0)
        {
            yield return new WaitForSeconds(1);
            maxWait--;
        }

        // If the service didn't initialize in 20 seconds this cancels location service use.
        if (maxWait < 1)
        {
            Debug.Log("Timed out");
            yield break;
        }

        // If the connection failed this cancels location service use.
        if (Input.location.status == LocationServiceStatus.Failed)
        {
            Debug.LogError("Unable to determine device location");
            yield break;
        }
        else
        {
            userLocation=convertor.setPoint(Input.location.lastData.latitude,Input.location.lastData.longitude);
            transform.position= userLocation;
            // If the connection succeeded, this retrieves the device's current location and displays it in the Console window.
            //Debug.Log("Location:"+"\nLatitude:" + Input.location.lastData.latitude + "\nLongitude:" + Input.location.lastData.longitude + "\nAltitude:" + Input.location.lastData.altitude + "\nAccuracy:" + Input.location.lastData.horizontalAccuracy + "\nTime refreshed:" + Input.location.lastData.timestamp);
            gpsText.SetText("Location:"+"\nLatitude:" + Input.location.lastData.latitude + "\nLongitude:" + Input.location.lastData.longitude + "\nAltitude:" + Input.location.lastData.altitude + "\nAccuracy:" + Input.location.lastData.horizontalAccuracy + "\nTime refreshed:" + Input.location.lastData.timestamp);
        }

        // Stops the location service if there is no need to query location updates continuously.
        isUpdating=!isUpdating;
        Input.location.Stop();
    }
}
