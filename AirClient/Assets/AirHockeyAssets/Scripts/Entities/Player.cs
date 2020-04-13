using UnityEngine;
using Sfs2X.Entities.Data;
using UnityEngine.EventSystems;

public class Player : MonoBehaviour, IClickable
{
    public int id;
    public int score;
    public bool touchEnabled = false;

    public void ParseData(ISFSObject sFSObject) {
        id = sFSObject.GetInt("id");
        score = sFSObject.GetInt("score");
        ParsePosition(sFSObject);
    }

    public void ParsePosition(ISFSObject sFSObject) {
        transform.position = new Vector2(sFSObject.GetFloat("x"), sFSObject.GetFloat("y"));
    }

    public SFSObject ToSFS() {
        SFSObject sfsObject = new SFSObject();
        //TODO id is redundant, server already knows that!!
        //sfsObject.PutInt("id", id);
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
        Debug.Log("name" + name + ", pid = " + id + ", " + capture(inGamePos));
        if (capture(inGamePos))
        {
            transform.position = Vector3.Lerp(transform.position, inGamePos, 0.6f);
            AirHockeyController.Instance.UpdatePlayerPosition(this);
        }
    }

    internal void EnableTouch()
    {
        touchEnabled = true;
    }

    // if capture the point only if it lies towards players touchable area
    // i.e either -ve x axis or +ve x axis
    public bool capture(Vector2 touchPoint)
    {
        return touchEnabled &&
            ((touchPoint.x < 0 && transform.position.x < 0) || (touchPoint.x >= 0 && transform.position.x >= 0));
    }

}
