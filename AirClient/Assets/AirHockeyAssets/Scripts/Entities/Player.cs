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
        transform.position = new Vector2(sFSObject.GetFloat("x"), sFSObject.GetFloat("y"));
    }

    public SFSObject ToSFS() {
        SFSObject sfsObject = new SFSObject();
        sfsObject.PutInt("id", id);
        sfsObject.PutFloat("x", transform.position.x);
        sfsObject.PutFloat("y", transform.position.y);
        return sfsObject;
    }

    void Awake()
    {
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
        Debug.Log("drag event : " + Camera.main.ScreenToWorldPoint(position));

        var ray = Camera.main.ScreenPointToRay(position);
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

    public void Update()
    {
        //if(Input.touchCount > 0)
        //{
        //Touch touch = Input.GetTouch(0);
        //if (touch.phase == TouchPhase.Stationary || touch.phase == TouchPhase.Moved)
        //{
        //    // get the touch position from the screen touch to world point
        //    Vector3 touchedPos = Camera.main.ScreenToWorldPoint(new Vector3(touch.position.x, touch.position.y, 10));
        //    touchedPos.z = 0;
        //    // lerp and set the position of the current object to that of the touch, but smoothly over time.
        //    transform.position = Vector3.Lerp(transform.position, touchedPos, 0.7f);
        //    dragStream.OnNext(transform.position);
        //}
        //2nd approach
        //Vector3 touchPositon = Camera.main.ScreenToWorldPoint(touch.position);
        //touchPositon.z = 0;
        //transform.position = touchPositon;
        //}
    }

}
