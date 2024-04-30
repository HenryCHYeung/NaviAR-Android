using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using TMPro;
[RequireComponent(typeof(RotateModel))]
public class RotateToggle : MonoBehaviour
{
    private GameObject destination,indicator,mainCamera;
    SetNavigation nav;
    private RotateModel rotateScript;
    private bool isToggledOn = false;
    // Start is called before the first frame update
    void Start()
    {
        SceneManager.sceneLoaded += OnSceneLoaded;
        mainCamera=GameObject.Find("TopDownCamera");
        destination=GameObject.Find("Destination");
        indicator=GameObject.Find("indicator");
        nav=indicator.GetComponent<SetNavigation>();
        rotateScript=GameObject.Find("TransformToggle").GetComponent<RotateModel>();
        rotateScript.enabled=false;
    }
    void OnSceneLoaded(Scene scene, LoadSceneMode mode)
    {
        mainCamera=GameObject.Find("TopDownCamera");
        destination=GameObject.Find("Destination");
        indicator=GameObject.Find("indicator");
        Debug.Log(indicator.name);
        nav=indicator.GetComponent<SetNavigation>();
        rotateScript=GameObject.Find("TransformToggle").GetComponent<RotateModel>();
        isToggledOn = false;
        rotateScript.enabled=false;
    }
    public void Toggle()
    {
        isToggledOn = !isToggledOn; 

        if (isToggledOn){
            toggleOn();
        }else{
            toggleOff();
        }
    }
    private void toggleOn(){
        rotateScript.enabled=true;
        mainCamera.GetComponent<DestinationPoint>().setRotationBool(true);
        destination.SetActive(false);
        nav.setLineToggle(false);
        indicator.SetActive(false);
    }
    private void toggleOff(){
        Messenger msg=GameObject.Find("Messenger").GetComponent<Messenger>();
        string floor=msg.getFloor();
        Scene currentScene=SceneManager.GetActiveScene();

        if(currentScene.name==("Naviar/Scenes/Edward Guiliano Global Center 1855 Broadway/Edward Guiliano Global Center 1855 Broadway"+floor)){
            rotateScript.modelTransform.rotation = Quaternion.Euler(0f, -180f, 0f);
        }else{
            rotateScript.modelTransform.rotation = Quaternion.Euler(0f, -90f, 0f);
        }
        rotateScript.modelTransform.transform.localScale=new Vector3(1f,1f,1f);
        rotateScript.enabled=false;
        mainCamera.GetComponent<DestinationPoint>().setRotationBool(false);
        destination.SetActive(true);
        indicator.SetActive(true);
        nav.setLineToggle(true);
    }
}
