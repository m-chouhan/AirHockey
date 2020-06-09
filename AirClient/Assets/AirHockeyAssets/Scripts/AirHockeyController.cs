using UnityEngine;
using Sfs2X;
using Sfs2X.Core;
using Sfs2X.Requests;
using Sfs2X.Entities.Data;
using Sfs2X.Entities;
using UnityEngine.SceneManagement;
using TMPro;
using System.Collections.Generic;

public class AirHockeyController : MonoBehaviour
{
    public GameObject playerAPrefab;
    public GameObject playerBPrefab;
    public GameObject puckPrefab;
    public GameObject gameWonPanel;
    public GameObject gameLosePanel;
    public TextMeshProUGUI scoreLeft; 
    public TextMeshProUGUI scoreRight;
    public Camera camera;

    private SmartFox sfs;
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

    public void BackToLobby()
    {
        reset();
        SceneManager.LoadScene("Lobby");
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
        // Show system message
        Debug.Log("User " + user.Name + " left the room");
        SceneManager.LoadScene("Lobby");
    }

    public void OnExtensionResponse(BaseEvent evt)
    {
        string cmd = (string)evt.Params["cmd"];

        SFSObject dataObject = (SFSObject)evt.Params["params"];

        switch (cmd) {
            case "start":
                Debug.Log("ext response : " + cmd);
                // Setup my properties
                GameObject player1 = Instantiate(playerAPrefab);
                GameObject player2 = Instantiate(playerBPrefab);
                GameObject puckGO = Instantiate(puckPrefab);
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
                puck.SetPosition(dataObject);

                if(current.transform.position.y < 0)
                {
                    current.SetTextComponent(scoreLeft);
                    other.SetTextComponent(scoreRight);
                } else
                {
                    current.SetTextComponent(scoreRight);
                    other.SetTextComponent(scoreLeft);
                    camera.transform.rotation = Quaternion.Euler(0, 0, 180);
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
                Debug.Log("ext response : " + cmd);
                current.SetScore(dataObject);
                other.SetScore(dataObject);
                break;
            case "reset":
                current.SetPosition(dataObject);
                other.SetPosition(dataObject);
                puck.SetPosition(dataObject);
                break;
            case "end":
                Debug.Log("ext response : " + cmd);
                bool won = dataObject.GetInt("id") == sfs.MySelf.Id;
                Debug.Log("End game " + won);
                if (won) gameWonPanel.SetActive(true); 
                else gameLosePanel.SetActive(true);
                break;
         }
    }

    public void UpdatePlayerPosition(Player player) {
        sfs.Send(new ExtensionRequest("move", player.ToSFS(), sfs.LastJoinedRoom, true));
    }

    private void reset()
    {
        List<Room> roomList = sfs.RoomManager.GetJoinedRooms();
        roomList.ForEach(room => sfs.Send(new LeaveRoomRequest(room)));
        sfs.RemoveAllEventListeners();
    }
}
