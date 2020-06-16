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

public class LobbyController : MonoBehaviour {

	public TMP_Text loggedInText;
	public Transform listContainer;
	public GameObject listItem;

    private SmartFox sfs;
	private bool shuttingDown;

	void Awake() {
		Application.runInBackground = true;
		
		if (NetWrapper.IsInitialized) {
		} else {
			SceneManager.LoadScene("Login");
			return;
		}

        Debug.Log("Coming in lobby as " + sfs.MySelf.Name);

		loggedInText.text = "Logged in as " + sfs.MySelf.Name;
		
		// Register event listeners
		sfs.AddEventListener(SFSEvent.CONNECTION_LOST, OnConnectionLost);
		sfs.AddEventListener(SFSEvent.ROOM_JOIN, OnRoomJoin);
		sfs.AddEventListener(SFSEvent.ROOM_JOIN_ERROR, OnRoomJoinError);
		sfs.AddEventListener(SFSEvent.USER_ENTER_ROOM, OnUserEnterRoom);
		sfs.AddEventListener(SFSEvent.USER_EXIT_ROOM, OnUserExitRoom);
		sfs.AddEventListener(SFSEvent.ROOM_ADD, OnRoomAdded);
		sfs.AddEventListener(SFSEvent.ROOM_REMOVE, OnRoomRemoved);
		
		// Populate list of available games
		populateGamesList();

		// Join the lobby Room (must exist in the Zone!)
		sfs.Send(new JoinRoomRequest("The Lobby"));
	}
	
	// Update is called once per frame
	void Update() {
		if (sfs != null)
			sfs.ProcessEvents();
	}

	void OnApplicationQuit() {
		shuttingDown = true;
	}
    
	public void OnLogoutButtonClick() {
		// Disconnect from server
		sfs.Disconnect();
	}
	
	public void OnGameItemClick(int roomId) {
		sfs.Send(new JoinRoomRequest(roomId));
	}
    	
    public void OnStartNewGameButtonClick() {
        NetWrapper.Instance.CreateRoom(sfs.MySelf.Name + "'s game");
    }

    public void OnBackButtonClick()
    {
        reset();
        sfs.Disconnect();
        SceneManager.LoadScene("Login");
    }

    private void reset() {
		sfs.RemoveAllEventListeners();
	}
    	
	private void populateGamesList() {
		List<Room> rooms = sfs.RoomManager.GetRoomList();
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

    private void clearGamesList() {
		foreach (Transform child in listContainer.transform) {
            Destroy(child.gameObject);
		}
	}
    	
	private void OnConnectionLost(BaseEvent evt) {
        Debug.Log("Connection Lost!!");
		reset();
		if (shuttingDown == true)
			return;
		// Return to login scene
		SceneManager.LoadScene("Login");
	}
	
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
	
	private void OnRoomJoinError(BaseEvent evt) {
		Debug.Log("Room join failed: " + (string) evt.Params["errorMessage"]);
	}
		
	private void OnUserEnterRoom(BaseEvent evt) {
		User user = (User) evt.Params["user"];
		Debug.Log("User " + user.Name + " entered the room");
	}
	
	private void OnUserExitRoom(BaseEvent evt) {
		User user = (User) evt.Params["user"];
		Debug.Log("User " + user.Name + " left the room");
	}

	private void OnRoomAdded(BaseEvent evt) {
		Room room = (Room) evt.Params["room"];
        Debug.Log("on room added " + room.Name);
        if (room.IsGame) {
			clearGamesList();
			populateGamesList();
		}
	}
	
	public void OnRoomRemoved(BaseEvent evt) {
        Room room = (Room)evt.Params["room"];
        Debug.Log("room removed " + room.Name);
        // Update view
        clearGamesList();
		populateGamesList();
	}
}
