﻿using UnityEngine;
using Sfs2X.Entities;

public class Player : MonoBehaviour
{
    public User user;

    public void Update()
    {
        if(Input.touchCount > 0)
        {

            Touch touch = Input.GetTouch(0);
            if (touch.phase == TouchPhase.Stationary || touch.phase == TouchPhase.Moved)
            {
                // get the touch position from the screen touch to world point
                Vector3 touchedPos = Camera.main.ScreenToWorldPoint(new Vector3(touch.position.x, touch.position.y, 10));
                touchedPos.z = 0;
                // lerp and set the position of the current object to that of the touch, but smoothly over time.
                transform.position = Vector3.Lerp(transform.position, touchedPos, 0.8f);
            }
            //2nd approach
            //Vector3 touchPositon = Camera.main.ScreenToWorldPoint(touch.position);
            //touchPositon.z = 0;
            //transform.position = touchPositon;
        }
    }
}
