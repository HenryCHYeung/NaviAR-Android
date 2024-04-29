using System.Collections;
using System.Collections.Generic;
using TMPro;
using UnityEngine;
using UnityEngine.AI;
using UnityEngine.SceneManagement;

[RequireComponent(typeof(NavMeshAgent))]
public class SetNavigation : MonoBehaviour
{
    private GameObject pathFields;
    private GameObject destination;
    public List<GameObject> navTargetObjects = new List<GameObject>();
    private GameObject navCurrObject;
    private NavMeshPath path;//created based on the distance of the user position and targeted location
    private LineRenderer line;//visual line produced when path is created
    private bool lineToggle=false;//set line active false when starting app
    private bool dest=false;
    private bool finish=false;
    // Start is called before the first frame update
    private int optionIndex;
    private Messenger msg;
    private NavMeshSnapper snapper;
    Scene currentScene;
    void Start()
    {
      SceneManager.sceneLoaded += OnSceneLoaded;
      msg=GameObject.Find("Messenger").GetComponent<Messenger>();
      pathFields = GameObject.Find("PointofInterest");
      Debug.Log(pathFields!=null);
      if (pathFields != null) {
          int childCountPathFields = pathFields.transform.childCount;
          for (int i = 0; i < childCountPathFields; i++){
              navTargetObjects.Add(pathFields.transform.GetChild(i).gameObject);
          }
          finish=true;
      } else {
          Debug.LogError("PointsofIntrest GameObject not found!");
      }
      currentScene = SceneManager.GetActiveScene();
      line=transform.GetComponent<LineRenderer>();//get line component from this game object
      path = new NavMeshPath();
      snapper=gameObject.GetComponent<NavMeshSnapper>();
    }
     void OnSceneLoaded(Scene scene, LoadSceneMode mode)
    {
        navTargetObjects.Clear();
        finish = false;
        lineToggle = false;
    }
    public void setDestination(GameObject d){
      dest=true;
      destination=d;
    }
  
    // Update is called once per frame
    void Update(){
      if(msg.roomNum !="" && finish && (currentScene.name!="Naviar/Scenes/Buildings")){
        //Debug.Log(msg.roomNum);
        foreach (GameObject obj in navTargetObjects){
          if(obj.name == msg.roomNum){
            navCurrObject=obj;
          }
          lineToggle=true;
        }
      }
    
      if(dest){
        //navCurrObject=destination; //debug line render// uncomment to test bathroom
        //lineToggle=true;//set true to calculate path //uncomment to test bathroom
        Debug.Log("HI"+navCurrObject.name);
      }

      if(lineToggle){//when linetoggle is true execute the line
          Debug.Log(dest+","+lineToggle+"Line toggle is on!"+navCurrObject); 
          NavMesh.CalculatePath(transform.position, navCurrObject.transform.position,NavMesh.AllAreas, path);//create navpath base on user new location and distance to waypoint
          line.positionCount=path.corners.Length;//create number of vertice points base on corners
          line.SetPositions(path.corners);// set the vertices
          line.enabled=true;//show line
          //Debug.Log(lineToggle+" corner: "+ path.corners.Length +" Target: "+ navCurrObject.name);
      }

      if ((currentScene.name=="Naviar/Scenes/Buildings") && (pathFields != null)) {
        GameObject currentChild = pathFields.transform.GetChild(0).gameObject;
        if(currentChild!=null){
          snapper.SnapNavToNavMesh(currentChild);
          foreach (GameObject obj in navTargetObjects){
            if(!obj.name.Equals(currentChild.name)){
              navTargetObjects.Add(currentChild);
            }
          }
          finish=true;
        }
      }
      if((navTargetObjects.Count !=0) && finish && (currentScene.name=="Naviar/Scenes/Buildings")){
        navCurrObject=navTargetObjects[0];
        lineToggle=true;
        TMP_Text g= GameObject.Find("gps").GetComponent<TMP_Text>();
        g.SetText("");
        }
      }
    }   
    
