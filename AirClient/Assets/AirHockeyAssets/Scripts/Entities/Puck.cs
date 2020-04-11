using UnityEngine;
using System.Collections;
using Sfs2X.Entities.Data;

public class Puck : MonoBehaviour
{
    public void ParseData(ISFSObject sFSObject)
    {
        transform.position = new Vector2(sFSObject.GetFloat("x"), sFSObject.GetFloat("y"));
    }

    public SFSObject ToSFS()
    {
        SFSObject sfsObject = new SFSObject();
        sfsObject.PutFloat("x", transform.position.x);
        sfsObject.PutFloat("y", transform.position.y);
        return sfsObject;
    }

}
