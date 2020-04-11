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
    public GameObject puckPrefab;

    private Player current, other;
    private Puck puck;

    public static AirHockeyController Instance;
    void Awake()
    {
        if (Instance == null)
            Instance = this;
        else if (Instance != this)
            Destroy(gameObject);

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
        string cmd = (string)evt.Params["cmd"];
        Debug.Log("ext response : " + cmd);

        SFSObject dataObject = (SFSObject)evt.Params["params"];

        switch(cmd) {
            case "start":
                // Setup my properties
                GameObject player1 = Instantiate(playerPrefab);
                GameObject player2 = Instantiate(playerPrefab);
                GameObject puckGO = Instantiate(puckPrefab);
                Player p1 = player1.GetComponent<Player>();
                Player p2 = player2.GetComponent<Player>();
                puck = puckGO.GetComponent<Puck>();

                p1.ParseData(dataObject.GetSFSObject("p1"));
                p2.ParseData(dataObject.GetSFSObject("p2"));
                puck.ParseData(dataObject.GetSFSObject("puck"));
                current = sfs.MySelf.Id == p1.id ? p1 : p2;
                other = sfs.MySelf.Id == p1.id ? p2 : p1;

                current.EnableTouch();
                current.gameObject.name = "me";
                other.gameObject.name = "other";
                puck.gameObject.name = "puck";
                break;
            case "move":
                other.ParseData(dataObject.GetSFSObject(other.id.ToString()));
                puck.ParseData(dataObject.GetSFSObject("puck"));
                break;
            case "stop":
                break;
         }
    }

    public void UpdatePlayerPosition(Player player) {
        sfs.Send(new ExtensionRequest("move", player.ToSFS(), sfs.LastJoinedRoom));
    }

    private void reset()
    {
        sfs.RemoveAllEventListeners();
    }
}
