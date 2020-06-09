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

	private const string EXTENSION_ID = "AirHockey";
	private const string EXTENSION_CLASS = "com.airhockey.AirHockeyExtension";

    private SmartFox sfs;
	private bool shuttingDown;

	void Awake() {
		Application.runInBackground = true;
		
		if (SmartFoxConnection.IsInitialized) {
			sfs = SmartFoxConnection.Connection;
		} else {
			SceneManager.LoadScene("Login");
			return;
		}

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
		// Join the Room
		sfs.Send(new Sfs2X.Requests.JoinRoomRequest(roomId));
	}
    	
    public void OnStartNewGameButtonClick() {
		// Configure Game Room
		RoomSettings settings = new RoomSettings(sfs.MySelf.Name + "'s game");
		settings.GroupId = "default";
		settings.IsGame = true;
		settings.MaxUsers = 2;
		settings.MaxSpectators = 0;
		settings.Extension = new RoomExtension(EXTENSION_ID, EXTENSION_CLASS);

		// Request Game Room creation to server
		sfs.Send(new CreateRoomRequest(settings, true, sfs.LastJoinedRoom));
	}

    public void OnBackButtonClick()
    {
        reset();
        // Return to login scene
        SceneManager.LoadScene("Login");
    }

    private void reset() {
		// Remove SFS2X listeners
		sfs.RemoveAllEventListeners();
	}
    	
	private void populateGamesList() {
		// For the gamelist we use a scrollable area containing a separate prefab button for each Game Room
		// Buttons are clickable to join the games
		List<Room> rooms = sfs.RoomManager.GetRoomList();
        Debug.Log("Populating game list " + rooms.Count);
		foreach (Room room in rooms) {
			// Show only game rooms
			// Also password protected Rooms are skipped, to make this example simpler
			// (protection would require an interface element to input the password)
			if (!room.IsGame || room.IsJoined || room.IsHidden || room.IsPasswordProtected) {
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
			GameObject.Destroy(child.gameObject);
		}
	}
    	
	private void OnConnectionLost(BaseEvent evt) {
		// Remove SFS2X listeners
		reset();
		if (shuttingDown == true)
			return;
		// Return to login scene
		SceneManager.LoadScene("Login");
	}
	
	private void OnRoomJoin(BaseEvent evt) {
		Room room = (Room) evt.Params["room"];
		// If we joined a Game Room, then we either created it (and auto joined) or manually selected a game to join
		if (room.IsGame) {
			reset ();
			SceneManager.LoadScene("AirHockey");
		}
	}
	
	private void OnRoomJoinError(BaseEvent evt) {
		// Show error message
		Debug.Log("Room join failed: " + (string) evt.Params["errorMessage"]);
	}
		
	private void OnUserEnterRoom(BaseEvent evt) {
		User user = (User) evt.Params["user"];

		// Show system message
		Debug.Log("User " + user.Name + " entered the room");
	}
	
	private void OnUserExitRoom(BaseEvent evt) {
		User user = (User) evt.Params["user"];

		if (user != sfs.MySelf) {
			// Show system message
			Debug.Log("User " + user.Name + " left the room");
		}
	}

	private void OnRoomAdded(BaseEvent evt) {
		Room room = (Room) evt.Params["room"];

		// Update view (only if room is game)
		if (room.IsGame) {
			clearGamesList();
			populateGamesList();
		}
	}
	
	public void OnRoomRemoved(BaseEvent evt) {
		// Update view
		clearGamesList();
		populateGamesList();
	}
}
