using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class DestinationPoint : MonoBehaviour
{
    private GameObject destination;
    private Camera mainCamera; 
    private bool rotationOn;
    private SetNavigation navScript;

    void Start()
    {
        mainCamera=GameObject.Find("TopDownCamera").GetComponent<Camera>();
        destination=GameObject.Find("Destination");
        GameObject Indicator= GameObject.Find("indicator");
        navScript = Indicator.GetComponent<SetNavigation>();
        destination.SetActive(false);
    }
    public void setRotationBool(bool value){
        rotationOn=value;
    }
    void Update(){
        if (Input.touchCount>0)
        {
            Touch touch = Input.GetTouch(0);
            if (touch.phase == TouchPhase.Began)
            {
                if (!destination.activeSelf && !rotationOn)
                {
                    destination.SetActive(true);
                }
                Vector3 touchPosition = mainCamera.ScreenToWorldPoint(new Vector3(touch.position.x,touch.position.y,mainCamera.nearClipPlane)); // Changed to use mainCamera
                touchPosition.y = 0.26f;
                Debug.Log("Touch position in world coordinates: " + touchPosition);
                destination.transform.position = touchPosition;
                navScript.setDestination(destination);
            }
        }
    }
}
