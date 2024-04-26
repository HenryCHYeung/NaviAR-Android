using UnityEngine;
using UnityEngine.AI;
using System.Collections.Generic;

public class NavMeshSnapper : MonoBehaviour
{
    public List<GameObject> buildings;

    void Start()
    {
        GameObject build = GameObject.Find("buildings");
        if (build != null)
        {
            int childCountBuildings = build.transform.childCount;
            for (int i = 0; i < childCountBuildings; i++)
            {
                buildings.Add(build.transform.GetChild(i).gameObject);
            }
        }
        else
        {
            Debug.LogError("Buildings GameObject not found!");
        }
    }

    public void SnapNavToNavMesh(GameObject nav)
    {
        Vector3 nearestPoint = Vector3.zero;
        float nearestDistance = Mathf.Infinity;

        // Iterate through each building
        foreach (GameObject building in buildings)
        {
            // Get the position of the building
            Vector3 buildingPosition = building.transform.position;

            // Find the nearest point on the NavMesh to the building's position
            NavMeshHit hit;
            if (NavMesh.SamplePosition(buildingPosition, out hit, Mathf.Infinity, NavMesh.AllAreas))
            {
                // Calculate the distance between the building and the nearest NavMesh point
                float distance = Vector3.Distance(buildingPosition, hit.position);

                // Update nearest point if this point is closer
                if (distance < nearestDistance)
                {
                    nearestPoint = hit.position;
                    nearestDistance = distance;
                }
            }
            else
            {
                Debug.LogWarning("No NavMesh point found near the building's position.");
            }
        }

        // Set the position of the nav GameObject to the nearest NavMesh point
        nav.transform.position = nearestPoint;
    }
}
