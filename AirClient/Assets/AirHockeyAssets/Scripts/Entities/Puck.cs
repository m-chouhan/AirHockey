using UnityEngine;
using System.Collections;
using Sfs2X.Entities.Data;

public class Puck : MonoBehaviour
{
    public void SetPosition(ISFSObject sFSObject)
    {
        SFSObject posObj = (SFSObject)sFSObject.GetSFSObject("puck");
        if (posObj == null)
        {
            Debug.Log("sfs null in " + name);
            return;
        }

        transform.position = new Vector2(posObj.GetFloat("x"), posObj.GetFloat("y"));
    }
}
