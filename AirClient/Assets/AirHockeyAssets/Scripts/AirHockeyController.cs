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
    public GameObject playerPrefab;

    private GameObject player1, player2;

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
        //callbacks
        sfs.AddEventListener(SFSEvent.CONNECTION_LOST, OnConnectionLost);
        sfs.AddEventListener(SFSEvent.PUBLIC_MESSAGE, OnPublicMessage);
        sfs.AddEventListener(SFSEvent.USER_ENTER_ROOM, OnUserEnterRoom);
        sfs.AddEventListener(SFSEvent.USER_EXIT_ROOM, OnUserExitRoom);
        sfs.AddEventListener(SFSEvent.EXTENSION_RESPONSE, OnExtensionResponse);

        // Setup my properties
        player1 = Instantiate(playerPrefab, new Vector3(-1.5f,0,0), Quaternion.identity);
        player1.GetComponent<Player>().user = sfs.MySelf;

        SFSObject payload = new SFSObject();
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
