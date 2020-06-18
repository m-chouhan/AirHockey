using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using System;
using System.Collections.Generic;
using Sfs2X;
using Sfs2X.Core;
using Sfs2X.Entities;
using Sfs2X.Requests;
using TMPro;
using UniRx;

public class LobbyController : MonoBehaviour {

    public TMP_Text loggedInText;
    public Transform listContainer;
    public GameObject listItem;
    private string username;

    void Awake() {
        Application.runInBackground = true;
        username = Scenes.getParam("username");
        Debug.Log("Coming in lobby as " + username);
        loggedInText.text = "Logged in as " + username;
        PopulateGamesList(NetWrapper.Instance.FetchRoomList());
        NetWrapper.Instance
            .FetchRoomEvents()
            .Subscribe(RoomEventHandler)
            .AddTo(this);
    }

    public void OnLogoutButtonClick() {
        NetWrapper.Instance
            .LogOut()
            .Finally(() => Scenes.Load("Login"));
    }
	
    public void OnGameItemClick(int roomId) {
        NetWrapper.Instance
            .JoinRoom(new JoinRoomRequest(roomId, "", NetWrapper.Instance.LastJoinedRoom.Id))
            .Subscribe(
                    item => {}, 
                    error => Debug.Log(error.Message),
                    () => Scenes.Load("Game_v2", "Mode", "Multiplayer-Online")
            ).AddTo(this);
    }

    public void OnStartNewGameButtonClick() {
        NetWrapper.Instance.CreateRoom(username + "'s game")
                .Subscribe(
                    item => { },
                    error => Debug.Log(error.Message),
                    () => {
                        Scenes.Load("Game_v2", "Mode", "Multiplayer");
                    }
                ).AddTo(this);
    }

    public void OnBackButtonClick()
    {
        Scenes.Load("Login");
    }

    private void PopulateGamesList(List<Room> rooms) {

        Debug.Log("Populating game list " + rooms.Count);
        foreach (Room room in rooms) {
            Debug.Log(room.Name);
            // Show only game rooms
            // Also password protected Rooms are skipped, to make this example simpler
            // (protection would require an interface element to input the password)
            if (!room.IsGame || room.IsHidden || room.IsPasswordProtected) {
                continue;
            }
            int roomId = room.Id;
            GameObject newListItem = Instantiate(listItem) as GameObject;
            ListItem roomItem = newListItem.GetComponent<ListItem>();
            roomItem.label.text = room.Name;
            roomItem.roomId = roomId;
            roomItem.button.onClick.AddListener(() => OnGameItemClick(roomId));
            newListItem.transform.SetParent(listContainer, false);
        }
    }

    private void ClearGamesList() {
        foreach (Transform child in listContainer.transform) {
            Destroy(child.gameObject);
        }
    }

    private void RoomEventHandler(BaseEvent ev)
    {
        Debug.Log(ev);
        ClearGamesList();
        PopulateGamesList(NetWrapper.Instance.FetchRoomList());
    }

    /*    	
    private void OnRoomJoin(BaseEvent evt) {
        Room room = (Room) evt.Params["room"];
        Debug.Log("on room join " + room.Name);
        // If we joined a Game Room, then 
        // 1. we either created it (and auto joined) or 
        // 2. manually selected a game to join
        if (room.IsGame) {
            reset ();
            Room lobbyRoom = sfs.RoomManager.GetRoomByName("The Lobby");
            sfs.Send(new LeaveRoomRequest(lobbyRoom));
            SceneManager.LoadScene("AirHockey");
        }
	}
			
	private void OnUserEnterRoom(BaseEvent evt) {
		User user = (User) evt.Params["user"];
		Debug.Log("User " + user.Name + " entered the room");
	}	
    */
}
