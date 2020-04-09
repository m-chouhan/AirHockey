using UnityEngine;
using Sfs2X;
using Sfs2X.Core;
using Sfs2X.Requests;
using Sfs2X.Entities.Data;
using System;
using UnityEngine.SceneManagement;

public class AirHockeyController : MonoBehaviour
{
    private SmartFox sfs;
    private int playerId;

    void Start()
    {
        Application.runInBackground = true;
        if (SmartFoxConnection.IsInitialized)
        {
            sfs = SmartFoxConnection.Connection;
            InitGame();
        }
        else
        {
            //no point starting the game, need to relogin
            SceneManager.LoadScene("Login");
            return;
        }
    }

    void Update()
    {

    }

    public void InitGame()
    {
        sfs.AddEventListener(SFSEvent.EXTENSION_RESPONSE, OnExtensionResponse);
        // Setup my properties
        playerId = sfs.MySelf.PlayerId;
        SFSObject payload = new SFSObject();
        payload.PutInt("a", 10); payload.PutInt("b", 20);
        sfs.Send(new ExtensionRequest("ready", payload, sfs.LastJoinedRoom));
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
