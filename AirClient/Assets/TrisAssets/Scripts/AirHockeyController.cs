using UnityEngine;
using Sfs2X;
using Sfs2X.Core;
using Sfs2X.Requests;
using Sfs2X.Entities.Data;
using Sfs2X.Entities;
using UnityEngine.SceneManagement;

public class AirHockeyController : MonoBehaviour
{
    private SmartFox sfs;
    private int playerId;

    void Awake()
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

    // Update is called once per frame
    void Update()
    {
        if (sfs != null)
            sfs.ProcessEvents();
    }

    public void InitGame()
    {
        sfs.AddEventListener(SFSEvent.CONNECTION_LOST, OnConnectionLost);
        sfs.AddEventListener(SFSEvent.PUBLIC_MESSAGE, OnPublicMessage);
        sfs.AddEventListener(SFSEvent.USER_ENTER_ROOM, OnUserEnterRoom);
        sfs.AddEventListener(SFSEvent.USER_EXIT_ROOM, OnUserExitRoom);

        sfs.AddEventListener(SFSEvent.EXTENSION_RESPONSE, OnExtensionResponse);
        // Setup my properties
        playerId = sfs.MySelf.PlayerId;
        SFSObject payload = new SFSObject();
        payload.PutInt("a", 10); payload.PutInt("b", 20);
        sfs.Send(new ExtensionRequest("ready", payload, sfs.LastJoinedRoom));
    }

    private void OnConnectionLost(BaseEvent evt)
    {
        reset();
        SceneManager.LoadScene("Login");
    }

    private void OnPublicMessage(BaseEvent evt)
    {
        User sender = (User)evt.Params["sender"];
        string message = (string)evt.Params["message"];
        Debug.Log(sender.Name + "::" + message);
    }

    private void OnUserEnterRoom(BaseEvent evt)
    {
        User user = (User)evt.Params["user"];

        // Show system message
        Debug.Log("User " + user.Name + " entered the room");
    }

    private void OnUserExitRoom(BaseEvent evt)
    {
        User user = (User)evt.Params["user"];

        if (user != sfs.MySelf)
        {
            // Show system message
            Debug.Log("User " + user.Name + " left the room");
        }
    }

    public void OnExtensionResponse(BaseEvent evt)
    {
        Debug.Log("On extension response");
        string cmd = (string)evt.Params["cmd"];
        SFSObject dataObject = (SFSObject)evt.Params["params"];
        Debug.Log(dataObject);
        switch(cmd) {
            case "start":
            case "stop":
                break;
         }
    }

    private void reset()
    {
        sfs.RemoveAllEventListeners();
    }
}
