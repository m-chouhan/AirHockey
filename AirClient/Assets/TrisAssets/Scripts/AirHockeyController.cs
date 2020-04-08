using UnityEngine;
using Sfs2X;
using Sfs2X.Core;
using Sfs2X.Requests;
using Sfs2X.Entities.Data;
using System;

public class AirHockeyController : MonoBehaviour
{
    private SmartFox sfs;
    private int playerId;

    // Start is called before the first frame update
    void Start()
    {

    }

    // Update is called once per frame
    void Update()
    {

    }

    public void InitGame(SmartFox smartFox)
    {
        sfs = smartFox;
        sfs.AddEventListener(SFSEvent.EXTENSION_RESPONSE, OnExtensionResponse);

        // Setup my properties
        playerId = sfs.MySelf.PlayerId;

        sfs.Send(new ExtensionRequest("ready", new SFSObject(), sfs.LastJoinedRoom));
    }

    private void OnExtensionResponse(BaseEvent evt)
    {
        string cmd = (string)evt.Params["cmd"];
        SFSObject dataObject = (SFSObject)evt.Params["params"];
        Debug.Log(dataObject);
        switch(cmd) {
            case "start":
            case "stop":
                break;            
         }
    }
}
