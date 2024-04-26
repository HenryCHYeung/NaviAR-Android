using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CampusDirectory : MonoBehaviour
{
    private GameObject startNotification,menu,sixteen,twentySix,eggc;
    private bool toggle=false;
    void Start(){
        startNotification=GameObject.Find("Image");
        menu=GameObject.Find("Menu");
        sixteen=GameObject.Find("16W");
        twentySix=GameObject.Find("26W");
        eggc=GameObject.Find("EGGC");

    }
    public void setTwentySixActive(){
        menu.SetActive(false);
        twentySix.SetActive(true);
    }
    public void setSixteenActive(){
        menu.SetActive(false);
        sixteen.SetActive(true);
    }
    public void setEGGCActive(){
        menu.SetActive(false);
        eggc.SetActive(true);
    }
    // Update is called once per frame
    void Update()
    {
        if (Input.touchCount>0)
        {
            Touch touch = Input.GetTouch(0);
            toggle=true;
            if (touch.phase == TouchPhase.Began)
            {
                startNotification.SetActive(false);
                if(toggle){
                    menu.SetActive(true);
                }
            }
        }
    }
}
