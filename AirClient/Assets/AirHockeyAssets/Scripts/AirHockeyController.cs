using UnityEngine;
using Sfs2X;
using Sfs2X.Core;
using Sfs2X.Requests;
using Sfs2X.Entities.Data;
using Sfs2X.Entities;
using UnityEngine.SceneManagement;
using TMPro;

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

        switch (cmd) {
            case "start":
                // Setup my properties
                GameObject player1 = Instantiate(playerPrefab);
                GameObject player2 = Instantiate(playerPrefab);
                GameObject puckGO = Instantiate(puckPrefab);
                GameObject scoreLeft = GameObject.Find("scoreLeft");
                GameObject scoreRight = GameObject.Find("scoreRight");
                current = player1.GetComponent<Player>();
                other = player2.GetComponent<Player>();
                puck = puckGO.GetComponent<Puck>();

                current.SetId(sfs.MySelf.Id);
                current.SetPosition(dataObject);

                //TODO : can be done in a better way---
                int[] uids = dataObject.GetIntArray("userIds");
                foreach (int uid in uids) {
                    Debug.Log("uid recv :" + uid);
                    if (uid != sfs.MySelf.Id)
                    {
                        other.SetId(uid);
                        other.SetPosition(dataObject);
                    }
                }
                //---
                puck.SetPosition(dataObject.GetSFSObject("puck"));

                if(current.transform.position.x < 0)
                {
                    current.SetTextComponent(scoreLeft.GetComponent<TextMeshProUGUI>());
                    other.SetTextComponent(scoreRight.GetComponent<TextMeshProUGUI>());
                } else
                {
                    current.SetTextComponent(scoreRight.GetComponent<TextMeshProUGUI>());
                    other.SetTextComponent(scoreLeft.GetComponent<TextMeshProUGUI>());
                }

                //score always start with 0, hence not required
                //current.SetScore(
                //    dataObject.GetSFSObject(current.id.ToString()).GetInt("score")
                //);
                //other.SetScore(
                //    dataObject.GetSFSObject(other.id.ToString()).GetInt("score")
                //);

                current.EnableTouch();
                current.gameObject.name = "me";
                other.gameObject.name = "other";
                puck.gameObject.name = "puck";
                Debug.Log("other : " + other.id + ", current : " + current.id);
                break;
            case "move":
                other.SetPosition(dataObject);
                puck.SetPosition(dataObject);
                break;
            case "updateScore":
                current.SetScore(dataObject);
                other.SetScore(dataObject);
                break;
            case "reset":
                current.SetPosition(dataObject);
                other.SetPosition(dataObject);
                puck.SetPosition(dataObject);
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
