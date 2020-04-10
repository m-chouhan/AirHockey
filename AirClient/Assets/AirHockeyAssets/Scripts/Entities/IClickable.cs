
using UnityEngine;

public interface IClickable 
{
    //defines if clicked point is touchable or not
    bool capture(Vector2 touchPoint);
}