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

        if(currentScene.name.Equals("Naviar/Scenes/Edward Guiliano Global Center 1855 Broadway/Edward Guiliano Global Center 1855 Broadway"+floor)){
            rotateScript.modelTransform.rotation = Quaternion.Euler(0, -180, 0);
        }else{
            rotateScript.modelTransform.rotation = Quaternion.Euler(0, -90, 0);
        }
        rotateScript.modelTransform.transform.localScale=new Vector3(1,1,1);
        rotateScript.enabled=false;
        mainCamera.GetComponent<DestinationPoint>().setRotationBool(false);
        destination.SetActive(true);
        Indicator.SetActive(true);
    }
}
