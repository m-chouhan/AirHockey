using UnityEngine;
using Sfs2X.Entities.Data;
using UnityEngine.EventSystems;
using System;
using TMPro;

public class Player : MonoBehaviour, IClickable
{
    public int id = 0;
    public int score = 0;
    TextMeshProUGUI text;
    public bool touchEnabled = false;

    public void SetPosition(ISFSObject sfsObj) {
        SFSObject posObj = (SFSObject) sfsObj.GetSFSObject(id.ToString());
        if(posObj == null)
        {
            //Debug.Log("sfs null in " + name + ", id " + id);
            return;
        }

        transform.position = new Vector2(posObj.GetFloat("x"), posObj.GetFloat("y"));
    }

    public void SetScore(ISFSObject sfsObj) {
        if (!sfsObj.ContainsKey(id.ToString()))
            return;

        score = sfsObj.GetInt(id.ToString());
        text.text = score.ToString();
        Debug.Log("score set to " + score + " for " + name);
    }

    public void SetScore(int score)
    {
        this.score = score;
        text.text = score.ToString();
    }

    public SFSObject ToSFS() {
        SFSObject sfsObject = new SFSObject();
        sfsObject.PutFloat("x", transform.position.x);
        sfsObject.PutFloat("y", transform.position.y);
        return sfsObject;
    }

    void Awake()
    {
        /**
         * TODO Check performance of event triggers for touch input.
         * might have to move this to Input.getTouch if things are not fine.
         * */       
        EventTrigger trigger = GetComponent<EventTrigger>();
        EventTrigger.Entry entry = new EventTrigger.Entry
        { eventID = EventTriggerType.Drag };
        entry.callback.AddListener(OnDragEventListener);
        trigger.triggers.Add(entry);

        EventTrigger.Entry endDrag = new EventTrigger.Entry
        { eventID = EventTriggerType.EndDrag };
        endDrag.callback.AddListener(OnDragEventListener);
        trigger.triggers.Add(endDrag);

        EventTrigger.Entry pointerDown = new EventTrigger.Entry
        { eventID = EventTriggerType.PointerDown };
        pointerDown.callback.AddListener(OnDragEventListener);
        trigger.triggers.Add(pointerDown);
    }

    private void OnDragEventListener(BaseEventData data)
    {
        Vector2 position = ((PointerEventData)data).position;
        Vector2 inGamePos = Camera.main.ScreenToWorldPoint(position);
        //Debug.Log("name" + name + ", pid = " + id + ", " + capture(inGamePos));
        if (capture(inGamePos))
        {
            transform.position = Vector3.Lerp(transform.position, inGamePos, 0.2f);
            GameController.Instance.UpdatePlayerPosition(this);
        }
    }
        
    // capture the point only if it lies towards players touchable area
    // i.e either -ve x axis or +ve x axis
    public bool capture(Vector2 touchPoint)
    {
        return touchEnabled &&
            ((touchPoint.y < 0 && transform.position.y < 0) || (touchPoint.y >= 0 && transform.position.y >= 0));
    }

    internal void SetTextComponent(TextMeshProUGUI textMeshProUGUI)
    {
        text = textMeshProUGUI;
    }

    internal void SetId(int id) { this.id = id; }

    internal void EnableTouch() { touchEnabled = true; }
}
