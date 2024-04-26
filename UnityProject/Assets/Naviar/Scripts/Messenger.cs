using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Messenger : MonoBehaviour
{
    object data;
    public string roomNum;
    public string message;
    SwitchScene switchSceneScript;
    void Start(){
        switchSceneScript=gameObject.GetComponent<SwitchScene>();
        DontDestroyOnLoad(gameObject);
        //sendDataToScene(message);
    }
    
    public void toScene(string placeholder){
        switchSceneScript.toBuild(placeholder);
    }

    public object recieveResponse(){
        return data;
    }
    public void setData(object val){
        this.data=val;
    }

}
