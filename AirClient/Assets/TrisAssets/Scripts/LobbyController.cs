using UnityEngine;
using UnityEngine.UI;
using UnityEngine.EventSystems;
using UnityEngine.SceneManagement;
using System;
using System.Collections;
using System.Collections.Generic;
using Sfs2X;
using Sfs2X.Logging;
using Sfs2X.Util;
using Sfs2X.Core;
using Sfs2X.Entities;
using Sfs2X.Requests;

public class LobbyController : MonoBehaviour {

	//----------------------------------------------------------
	// UI elements
	//----------------------------------------------------------

	public Text loggedInText;
	public Transform gameListContent;
	public GameObject gameListItem;

	//----------------------------------------------------------
	// Private properties
	//----------------------------------------------------------

	private const string EXTENSION_ID = "Tris";
	private const string EXTENSION_CLASS = "sfs2x.extensions.games.tris.TrisExtension";

	private SmartFox sfs;
	private bool shuttingDown;

	//----------------------------------------------------------
	// Unity calback methods
	//----------------------------------------------------------

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

	//----------------------------------------------------------
	// Public interface methods for UI
	//----------------------------------------------------------

    
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
		settings.GroupId = "games";
		settings.IsGame = true;
		settings.MaxUsers = 2;
		settings.MaxSpectators = 0;
		settings.Extension = new RoomExtension(EXTENSION_ID, EXTENSION_CLASS);

		// Request Game Room creation to server
		sfs.Send(new CreateRoomRequest(settings, true, sfs.LastJoinedRoom));
	}

	//----------------------------------------------------------
	// Private helper methods
	//----------------------------------------------------------
	
	private void reset() {
		// Remove SFS2X listeners
		sfs.RemoveAllEventListeners();
	}
    	
	private void populateGamesList() {
		// For the gamelist we use a scrollable area containing a separate prefab button for each Game Room
		// Buttons are clickable to join the games
		List<Room> rooms = sfs.RoomManager.GetRoomList();

		foreach (Room room in rooms) {
			// Show only game rooms
			// Also password protected Rooms are skipped, to make this example simpler
			// (protection would require an interface element to input the password)
			if (!room.IsGame || room.IsHidden || room.IsPasswordProtected) {
				continue;
			}	

			int roomId = room.Id;

			GameObject newListItem = Instantiate(gameListItem) as GameObject;
			GameListItem roomItem = newListItem.GetComponent<GameListItem>();
			roomItem.nameLabel.text = room.Name;
			roomItem.roomId = roomId;

			roomItem.button.onClick.AddListener(() => OnGameItemClick(roomId));

			newListItem.transform.SetParent(gameListContent, false);
		}
	}

	private void clearGamesList() {
		foreach (Transform child in gameListContent.transform) {
			GameObject.Destroy(child.gameObject);
		}
	}

	//----------------------------------------------------------
	// SmartFoxServer event listeners
	//----------------------------------------------------------
	
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
			// Remove SFS2X listeners
			reset ();
			// Load game scene
			SceneManager.LoadScene("Game");
		} else {
            printSystemMessage("No game room available!!");
		}
	}
	
	private void OnRoomJoinError(BaseEvent evt) {
		// Show error message
		printSystemMessage("Room join failed: " + (string) evt.Params["errorMessage"]);
	}
		
	private void OnUserEnterRoom(BaseEvent evt) {
		User user = (User) evt.Params["user"];

		// Show system message
		printSystemMessage("User " + user.Name + " entered the room");
	}
	
	private void OnUserExitRoom(BaseEvent evt) {
		User user = (User) evt.Params["user"];

		if (user != sfs.MySelf) {
			// Show system message
			printSystemMessage("User " + user.Name + " left the room");
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

    public void printSystemMessage(String message) {
        Debug.Log(message);
    }
}
