using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class PlaceCollision: MonoBehaviour
{
    private void OnTriggerEnter(Collider other)
    {

        if (other.CompareTag("Player"))
        {
           TMP_Text g= GameObject.Find("gps").GetComponent<TMP_Text>();
            g.text="You are nearby your destination.";
        }
    }
}

