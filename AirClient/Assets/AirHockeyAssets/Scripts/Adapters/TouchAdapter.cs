using System;
using UniRx;
using UnityEngine;
using UnityEngine.Events;
using UnityEngine.EventSystems;

public class TouchAdapter
{
    public static IObservable<Vector2> FetchTouchEvents(GameObject gameobject) {
        /**
        * TODO Check performance of event triggers for touch input.
        * might have to move this to Input.getTouch if things are not fine.
        * */
        Subject<Vector2> subject = new Subject<Vector2>();
        UnityAction<BaseEventData> onDrag = (data) => {
            Vector2 position = ((PointerEventData)data).position;
            Vector2 inGamePos = Camera.main.ScreenToWorldPoint(position);
            subject.OnNext(inGamePos);
        };

        EventTrigger trigger = gameobject.GetComponent<EventTrigger>();
        EventTrigger.Entry entry = new EventTrigger.Entry
        { eventID = EventTriggerType.Drag };
        entry.callback.AddListener(onDrag);
        trigger.triggers.Add(entry);

        EventTrigger.Entry endDrag = new EventTrigger.Entry
        { eventID = EventTriggerType.EndDrag };
        endDrag.callback.AddListener(onDrag);
        trigger.triggers.Add(endDrag);

        EventTrigger.Entry pointerDown = new EventTrigger.Entry
        { eventID = EventTriggerType.PointerDown };
        pointerDown.callback.AddListener(onDrag);
        trigger.triggers.Add(pointerDown);
        return subject.AsObservable();
    }
}
