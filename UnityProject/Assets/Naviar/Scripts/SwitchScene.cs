using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.EventSystems;
public class SwitchScene : MonoBehaviour
{

  public void toBuild(string scene){
    SceneManager.LoadScene(scene);
  }
   public void changeScene(string building,string floor){
     string val= "Naviar/Scenes/"+building+"/"+building+floor;
     SceneManager.LoadScene(val);
   }

}