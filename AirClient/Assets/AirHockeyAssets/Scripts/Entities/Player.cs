using UnityEngine;
using Sfs2X.Entities.Data;
using UnityEngine.EventSystems;
using System;
using TMPro;

public class Player : MonoBehaviour, IClickable
{
    public int id;
    public int score;
    public TMP_Text text;
    public bool touch;

    public void SetPosition(ISFSObject posObj) {
        transform.position = new Vector2(posObj.GetFloat("x"), posObj.GetFloat("y"));
    }

    public void SetPosition(float x, float y)
    {
        transform.position = new Vector2(x, y);
    }

    public void SetPosition(Vector2 pos)
    {
        transform.position = pos;
    }

    public void SetScore(int score)
    {
        this.score = score;
        text.text = score.ToString();
        Debug.Log("score set to " + score + " for " + name);
    }

    public SFSObject ToSFS() {
        SFSObject sfsObject = new SFSObject();
        //no need to put id, server already knows that
        //and we don't want to push redundant data
        sfsObject.PutFloat("x", transform.position.x);
        sfsObject.PutFloat("y", transform.position.y);
        return sfsObject;
    }

    public void FromSFS(ISFSObject data)
    {
        id = data.GetInt("id");
        transform.position = new Vector2(data.GetFloat("x"), data.GetFloat("y"));
    }

    // capture the point only if it lies towards players touchable area
    // i.e either -ve x axis or +ve x axis
    public bool capture(Vector2 touchPoint)
    {
        return touch &&
            ((touchPoint.y < 0 && transform.position.y < 0) || (touchPoint.y >= 0 && transform.position.y >= 0));
    }

    public void SetTextComponent(TextMeshProUGUI text) {
        this.text = text;
    }
}
