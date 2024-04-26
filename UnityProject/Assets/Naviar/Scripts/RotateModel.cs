using UnityEngine;

public class RotateModel : MonoBehaviour
{
    public Transform modelTransform;

    // Variables to store touch positions
    private Vector2 touchStartPos;
    private Vector2 touchEndPos;

    // Variables to store initial rotation and scale
    private Quaternion initialRotation;
    private Vector3 initialScale;

    // Flag to differentiate between single and multi-touch
    private bool isPinching = false;

    void Start()
    {
        if (modelTransform == null)
        {
            Debug.LogError("Model transform is not assigned!");
        }
        else
        {
            // Record initial rotation and scale
            initialRotation = modelTransform.rotation;
            initialScale = modelTransform.localScale;
        }
    }

    void Update()
    {
        if (Input.touchCount == 1 && !isPinching)
        {
            Touch touch = Input.GetTouch(0);

            switch (touch.phase)
            {
                case TouchPhase.Began:
                    // Record initial touch position
                    touchStartPos = touch.position;
                    break;

                case TouchPhase.Moved:
                    // Calculate delta movement
                    Vector2 deltaPos = touch.position - touchStartPos;

                    // Rotate based on delta movement
                    float rotateFactorX = -deltaPos.y * 0.1f; // Rotate around X-axis for vertical movement
                    float rotateFactorY = deltaPos.x * 0.1f; // Rotate around Y-axis for horizontal movement
                    modelTransform.rotation = initialRotation * Quaternion.Euler(rotateFactorX, rotateFactorY, 0);
                    break;

                case TouchPhase.Ended:
                    // Update initial rotation
                    initialRotation = modelTransform.rotation;
                    break;
            }
        }
        else if (Input.touchCount == 2)
        {
            isPinching = true;

            Touch touch0 = Input.GetTouch(0);
            Touch touch1 = Input.GetTouch(1);

            Vector2 touch0PrevPos = touch0.position - touch0.deltaPosition;
            Vector2 touch1PrevPos = touch1.position - touch1.deltaPosition;

            float prevMagnitude = (touch0PrevPos - touch1PrevPos).magnitude;
            float currentMagnitude = (touch0.position - touch1.position).magnitude;

            float difference = currentMagnitude - prevMagnitude;

            float zoomSpeed = 0.01f;
            Vector3 newScale = modelTransform.localScale + new Vector3(difference, difference, difference) * zoomSpeed;

            // Clamp scale to prevent scaling too small
            float minScale = 0.1f;
            newScale = Vector3.Max(newScale, Vector3.one * minScale);

            modelTransform.localScale = newScale;
        }
        else
        {
            isPinching = false;
        }
    }
}
