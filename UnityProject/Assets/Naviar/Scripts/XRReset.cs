using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
public class XRReset : MonoBehaviour
{
    [SerializeField] GameObject arSession;
    // Start is called before the first frame update
    void Start()
    {
        arSession.transform.position=new Vector3(0f,0f,0f);
        gameObject.transform.position=new Vector3(0f,0f,0f);
    }
    private void onEnable(){
        SceneManager.sceneLoaded += OnSceneLoaded;
    }
    private void onDisable(){
        SceneManager.sceneLoaded -= OnSceneLoaded;
    }

   private void OnSceneLoaded(Scene scene, LoadSceneMode mode)
    {
        ResetXRPosition();
    }

    private void ResetXRPosition()
    {
        // Check if XR device is available
        if (gameObject && arSession)
        {
            arSession.transform.position=new Vector3(0f,0f,0f);
            gameObject.transform.position=new Vector3(0f,0f,0f);
        }
        else
        {
            Debug.LogWarning("XR device is not present. Cannot reset position.");
        }
    }
}
