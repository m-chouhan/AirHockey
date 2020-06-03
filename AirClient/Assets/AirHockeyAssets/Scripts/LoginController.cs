using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using Sfs2X;
using Sfs2X.Util;
using Sfs2X.Core;
using System;
using Sfs2X.Requests;
using Sfs2X.Entities.Data;
using TMPro;

public class LoginController : MonoBehaviour {

	[Tooltip("IP address or domain name of the SmartFoxServer 2X instance")]
	public string Host = "127.0.0.1";

	[Tooltip("TCP port listened by the SmartFoxServer 2X instance; used for regular socket connection in all builds except WebGL")]
	public int TcpPort = 9933;

    [Tooltip("TCP port listened by the SmartFoxServer 2X instance; used for regular socket connection in all builds except WebGL")]
    public int UdpPort = 9934;

	[Tooltip("Name of the SmartFoxServer 2X Zone to join")]
	public string Zone = "AirHockey";

    public TMP_InputField inputField;
    public TMP_Text errorText;

	private SmartFox sfs;

	void Awake() {
		Application.runInBackground = true;
	}
	
	void Update() {
		if (sfs != null)
			sfs.ProcessEvents();
	}

	public void OnLoginButtonClick() {		
		ConfigData cfg = new ConfigData();
		cfg.Host = Host;
		cfg.Port = TcpPort;
		cfg.Zone = Zone;		
		sfs = new SmartFox();
		sfs.AddEventListener(SFSEvent.CONNECTION, OnConnection);
		sfs.AddEventListener(SFSEvent.CONNECTION_LOST, OnConnectionLost);
		sfs.AddEventListener(SFSEvent.LOGIN, OnLogin);
		sfs.AddEventListener(SFSEvent.LOGIN_ERROR, OnLoginError);
		
		// Connect to SFS2X
		sfs.Connect(cfg);
	}
    	
	private void reset() {
		// Remove SFS2X listeners
		// This should be called when switching scenes, so events from the server do not trigger code in this scene
		sfs.RemoveAllEventListeners();		
	}

	private void OnConnection(BaseEvent evt) {
		if ((bool)evt.Params["success"])
		{
			Debug.Log("SFS2X API version: " + sfs.Version);
			Debug.Log("Connection mode is: " + sfs.ConnectionMode);

			// Save reference to SmartFox instance; it will be used in the other scenes
			SmartFoxConnection.Connection = sfs;
            Debug.Log("Trying to login as " + inputField.text);
			// Login
			sfs.Send(new LoginRequest(inputField.text));
		}
		else
		{
			// Remove SFS2X listeners and re-enable interface
			reset();

			// Show error message
			errorText.text = "Connection failed; is the server running at all?";
		}
	}
	
	private void OnConnectionLost(BaseEvent evt) {
		// Remove SFS2X listeners and re-enable interface
		reset();

		string reason = (string) evt.Params["reason"];

		if (reason != ClientDisconnectionReason.MANUAL) {
			// Show error message
			errorText.text = "Connection was lost; reason is: " + reason;
		}
	}
	
	private void OnLogin(BaseEvent evt) {
		// Remove SFS2X listeners and re-enable interface
		reset();
        sfs.AddEventListener(SFSEvent.UDP_INIT, OnUDPInit);
        sfs.InitUDP(Host, UdpPort);
	}

    private void OnUDPInit(BaseEvent evt)
    {
        if ((bool)evt.Params["success"])
        {
            Debug.Log("Udp init success!!");
            // Load lobby scene
            SceneManager.LoadScene("Lobby");
        }
        else
        {
            Debug.Log("UDP init failed!");
            OnLoginError(evt);
        }
    }

    private void OnLoginError(BaseEvent evt) {
		// Disconnect
		sfs.Disconnect();
		// Remove SFS2X listeners and re-enable interface
		reset();		
		// Show error message
		errorText.text = "Login failed: " + (string) evt.Params["errorMessage"];
	}
}
