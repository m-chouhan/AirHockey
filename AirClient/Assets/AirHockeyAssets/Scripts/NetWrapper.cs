using UnityEngine;
using Sfs2X;
using System;
using Sfs2X.Util;
using Sfs2X.Core;
using Sfs2X.Requests;
using UniRx;
using System.Collections.Generic;
using Sfs2X.Entities;
using Sfs2X.Entities.Data;

public class NetWrapper : MonoBehaviour
{
    public static bool IsInitialized = false;
    public static NetWrapper Instance;

    private string Host = "52.66.195.155";
    private int TcpPort = 9933;
    private int UdpPort = 9934;
    private string Zone = "AirHockey";
    private readonly string EXTENSION_ID = "AirHockey";
    private readonly string EXTENSION_CLASS = "com.airhockey.AirHockeyRoomExtension";
	private SmartFox sfs;

    public Room LastJoinedRoom
    {
        get {
            return sfs?.LastJoinedRoom;
        }
    }

    private Subject<SFSObject> inGameStream;
    public IObservable<SFSObject> InGameStream { 
        get {
            return inGameStream.AsObservable();
        }
    }

    void Awake()
    {
        if(Instance == null)
        {
            Instance = this;
            sfs = new SmartFox();
            DontDestroyOnLoad(gameObject);
        } else if(Instance != this)
        {
            Destroy(gameObject);
        }
    }

    void Update()
    {
        if (sfs != null)
            sfs.ProcessEvents();
    }

    void OnApplicationQuit()
    {
        if (sfs != null && sfs.IsConnected)
        {
            sfs.Disconnect();
        }
    }

    public IObservable<string> Login(string username)
    {
        Subject<string> loginStream = new Subject<string>();
        ConfigData cfg = new ConfigData();
        cfg.Host = Host;
        cfg.Port = TcpPort;
        cfg.Zone = Zone;
        sfs.AddEventListener(SFSEvent.CONNECTION, 
            (evt) => {
                if ((bool)evt.Params["success"])
                {
                    loginStream.OnNext("connection success");
                    Debug.Log("SFS2X API version: " + sfs.Version);
                    Debug.Log("Connection mode is: " + sfs.ConnectionMode);
                    Debug.Log("Trying to login as " + username);
                    sfs.Send(new LoginRequest(username));
                }
                else
                    loginStream.OnError(new Exception("Connection failed; is the server running at all?"));
            }
        );

        sfs.AddEventListener(SFSEvent.CONNECTION_LOST,
            (evt) => loginStream.OnError(new Exception((string)evt.Params["reason"]))
        );
        sfs.AddEventListener(SFSEvent.LOGIN, (evt) => {
            loginStream.OnNext("login success");
            sfs.InitUDP(Host, UdpPort);
        });
        sfs.AddEventListener(SFSEvent.LOGIN_ERROR,
            (evt) => loginStream.OnError(new Exception((string)evt.Params["errorMessage"]))
        );
        sfs.AddEventListener(SFSEvent.UDP_INIT, (evt) => {
            if ((bool)evt.Params["success"])
            {
                loginStream.OnNext("udp init success");
                loginStream.OnCompleted();
            }
            else
                loginStream.OnError(new Exception((string)evt.Params["errorMessage"]));
        });
        // Connect to SFS2X
        sfs.Connect(cfg);
        loginStream.Finally(() => {
            sfs.RemoveAllEventListeners();
            Debug.Log("Finally (login) clean up");
        });

        return loginStream.AsObservable();
    }

    public IObservable<string> LogOut() {
        Subject<string> subject = new Subject<string>();
        sfs.AddEventListener(SFSEvent.LOGOUT, ev => subject.OnCompleted());
        sfs.Send(new LogoutRequest());
        subject.Finally(() => {
            sfs.RemoveAllEventListeners();
            Debug.Log("Finally (logout) cleanup");
        });
        return subject.AsObservable();
    }

    public IObservable<Room> CreateRoom(string roomName)
    {
        Subject<Room> subject = new Subject<Room>();
        // Configure Game Room
        RoomSettings settings = new RoomSettings(roomName)
        {
            GroupId = "default",
            IsGame = true,
            MaxUsers = 2,
            MaxSpectators = 0,
            Extension = new RoomExtension(EXTENSION_ID, EXTENSION_CLASS)
        };

        sfs.AddEventListener(SFSEvent.ROOM_JOIN, 
            ev => {
                subject.OnNext((Room)ev.Params["room"]);
                subject.OnCompleted();
            }
        );

        sfs.AddEventListener(SFSEvent.ROOM_JOIN_ERROR, 
            ev => subject.OnError(new Exception((string)ev.Params["errorMessage"])));
        // create room request and leave previous room post join
        sfs.Send(new CreateRoomRequest(settings, true, sfs.LastJoinedRoom));
        subject.Finally(() => {
            sfs.RemoveAllEventListeners();
            Debug.Log("Finally (create room) cleanup");
        });
        return subject.AsObservable();
    }

    public IObservable<Room> JoinRoom(JoinRoomRequest request)
    {
        Subject<Room> subject = new Subject<Room>();
        sfs.AddEventListener(SFSEvent.ROOM_JOIN,
            ev => {
                subject.OnNext((Room)ev.Params["room"]);
                subject.OnCompleted();
            }
        );
        sfs.AddEventListener(SFSEvent.ROOM_JOIN_ERROR,
            ev =>
                subject.OnError(new Exception((string)ev.Params["errorMessage"])));

        sfs.Send(request);
        subject.Finally(() => {
            sfs.RemoveAllEventListeners();
            Debug.Log("Finally (join room) cleanup");
        });
        return subject.AsObservable();
    }

    public List<Room> FetchRoomList()
    {
        return sfs.RoomList;
    }

    public IObservable<BaseEvent> FetchRoomEvents()
    {
        Subject<BaseEvent> subject = 
            new Subject<BaseEvent>();

        sfs.AddEventListener(SFSEvent.ROOM_ADD, 
            ev => subject.OnNext(ev));
        sfs.AddEventListener(SFSEvent.ROOM_REMOVE,
            ev => subject.OnNext(ev));
        //sfs.AddEventListener(SFSEvent.USER_ENTER_ROOM, ev => subject.OnNext(ev));
        //sfs.AddEventListener(SFSEvent.USER_EXIT_ROOM, ev => subject.OnNext(ev));
        //sfs.AddEventListener(SFSEvent.CONNECTION_LOST, ev => { });

        subject.Finally(() => {
            sfs.RemoveAllEventListeners();
            Debug.Log("Finally (join room) cleanup");
        });
        return subject.AsObservable();
    }
}