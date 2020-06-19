using UnityEngine;
using System.Collections;
using Sfs2X.Entities.Data;

public class Puck : MonoBehaviour
{
    public void SetPosition(ISFSObject pos)
    {
        transform.position = new Vector2(pos.GetFloat("x"), pos.GetFloat("y"));
    }
}
