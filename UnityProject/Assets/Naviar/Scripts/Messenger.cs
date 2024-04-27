using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Messenger : MonoBehaviour
{
    object data;
    public string roomNum;
    private string building;
    private string floor;
    public string message;
    SwitchScene switchSceneScript;
    void Start(){
        switchSceneScript=gameObject.GetComponent<SwitchScene>();
        DontDestroyOnLoad(gameObject);
        sendDataToScene(message);
    }
    
    public void toScene(string placeholder){
        switchSceneScript.toBuild(placeholder);
    }

    public void sendDataToScene(string message){
        string[] tem=message.Split(",");
        building=tem[0];
        floor=tem[1];
        roomNum=tem[2];
        switchSceneScript.changeScene(building,floor);
    }

    public object recieveResponse(){
        return data;
    }
    public void setData(object val){
        this.data=val;
    }
    public string getBuilding(){
    return building;
   }
   public string getFloor(){
    return floor;
   }
}
