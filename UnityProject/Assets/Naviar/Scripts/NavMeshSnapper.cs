using UnityEngine;
using UnityEngine.AI;
//using  Unity.AI.Navigation;
using System.Collections.Generic;
using UnityEngine.UI;
using TMPro;

public class NavMeshSnapper : MonoBehaviour{

    public static void SnapNavToNavMesh(GameObject obj){
        NavMeshAgent agent = obj.GetComponent<NavMeshAgent>();
        if (agent != null && !agent.isOnNavMesh)
        {
            NavMeshHit hit;
            if (NavMesh.SamplePosition(obj.transform.position, out hit, Mathf.Infinity, NavMesh.AllAreas))
            {
                agent.Warp(hit.position);
            }
        }
    }
}
