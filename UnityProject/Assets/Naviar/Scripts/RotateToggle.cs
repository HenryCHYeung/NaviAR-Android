using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
[RequireComponent(typeof(RotateModel))]
public class RotateToggle : MonoBehaviour
{
    private GameObject destination,Indicator,mainCamera;
    private RotateModel rotateScript;
    private bool isToggledOn = false;
    // Start is called before the first frame update
    void Start()
    {
        mainCamera=GameObject.Find("TopDownCamera");
        destination=GameObject.Find("Destination");
        Indicator=GameObject.Find("indicator");
        rotateScript=GameObject.Find("TransformToggle").GetComponent<RotateModel>();
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
        Indicator.SetActive(false);
    }
    private void toggleOff(){
        Messenger msg=GameObject.Find("Messenger").GetComponent<Messenger>();
        string floor=msg.getFloor();
        Scene currentScene=SceneManager.GetActiveScene();

        if(currentScene.name==("Naviar/Scenes/Edward Guiliano Global Center 1855 Broadway/Edward Guiliano Global Center 1855 Broadway"+floor)){
            rotateScript.modelTransform.rotation = Quaternion.Euler(0f, -180f, 0f);
        }else if(currentScene.name==("Naviar/Scenes/26W. 61st Street/26W. 61st Street"+floor)){
            rotateScript.modelTransform.rotation = Quaternion.Euler(0f, 180f, 0f);
        }else{
            rotateScript.modelTransform.rotation = Quaternion.Euler(0f, -90f, 0f);
        }
        rotateScript.modelTransform.transform.localScale=new Vector3(1f,1f,1f);
        rotateScript.enabled=false;
        mainCamera.GetComponent<DestinationPoint>().setRotationBool(false);
        destination.SetActive(true);
        Indicator.SetActive(true);
    }
}
