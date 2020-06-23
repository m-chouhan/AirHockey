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

    public int Id { get { return sfs.MySelf.Id; } }

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

    private static Subject<T> CreateSubject<T>(string msg) {
        Subject<T> subject = new Subject<T>();
        Instance.sfs.RemoveAllEventListeners();
        subject.Finally(() =>
        {
            Debug.Log("cleanup : " + msg);
            Instance.sfs.RemoveAllEventListeners();
        });
        return subject;
    }

    //connect->login->udpinit
    public IObservable<string> Login(string username)
    {
        Subject<string> loginStream = CreateSubject<string>("Login");
        ConfigData cfg = new ConfigData
        {
            Host = Host,
            Port = TcpPort,
            Zone = Zone
        };
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

        return loginStream.AsObservable();
    }

    public IObservable<string> LogOut() {
        Subject<string> subject = CreateSubject<string>("Logout");
        sfs.AddEventListener(SFSEvent.LOGOUT, ev => subject.OnCompleted());
        sfs.Send(new LogoutRequest());
        return subject.AsObservable();
    }

    public IObservable<Room> CreateRoom(string roomName)
    {
        Subject<Room> subject = CreateSubject<Room>("create room");
        // Configure Game Room
        RoomSettings settings = new RoomSettings(roomName);
        //{
        settings.GroupId = "default";
        settings.IsGame = true;
        settings.MaxUsers = 2;
        settings.MaxSpectators = 0;
        settings.Extension = new RoomExtension(EXTENSION_ID, EXTENSION_CLASS);
        //};

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
        return subject.AsObservable();
    }

    public IObservable<Room> JoinRoom(JoinRoomRequest request)
    {
        Subject<Room> subject = CreateSubject<Room>("Join room");
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
        return subject.AsObservable();
    }

    public List<Room> FetchRoomList()
    {
        return sfs.RoomList;
    }

    public IObservable<BaseEvent> FetchRoomEvents()
    {
        Subject<BaseEvent> subject = CreateSubject<BaseEvent>("room events");

        sfs.AddEventListener(SFSEvent.ROOM_ADD, 
            ev => subject.OnNext(ev));
        sfs.AddEventListener(SFSEvent.ROOM_REMOVE,
            ev => subject.OnNext(ev));
        return subject.AsObservable();
    }

    public IObservable<BaseEvent> FetchGameEvents()
    {
        Subject<BaseEvent> subject = CreateSubject<BaseEvent>("game events");
        Debug.Log("1 " + LastJoinedRoom.GetVariable("roomvar1"));
        Debug.Log("2 " + LastJoinedRoom.GetVariable("roomvar2"));
        Debug.Log("3 " + LastJoinedRoom.GetVariable("roomvar3"));
        sfs.AddEventListener(SFSEvent.EXTENSION_RESPONSE,
            resp => 
            {
                string cmd = (string)resp.Params["cmd"];
                switch(cmd)
                {
                    case "start":
                        SFSObject data = (SFSObject) resp.Params["params"];
                        Debug.Log("[start] received " + data.ToJson());

                        string curId = sfs.MySelf.Id.ToString();
                        string [] keys = data.GetKeys();
                        string otherId = Array.Find(keys,
                                key => !key.Equals("puck") && !key.Equals(curId));

                        ISFSObject curData = data.GetSFSObject(curId);
                        ISFSObject otherData = data.GetSFSObject(otherId);
                        data.PutSFSObject("current", curData);
                        data.PutSFSObject("other", otherData);
                        data.RemoveElement(curId);
                        data.RemoveElement(otherId);
                        Debug.Log("[start] updated data is " + data.ToJson());
                        break;                    
                }
                subject.OnNext(resp);
                if (cmd.Equals("end")) subject.OnCompleted();
            }
        );

        sfs.AddEventListener(SFSEvent.ROOM_VARIABLES_UPDATE, ev =>
        {
            Debug.Log("Room var update");
            Debug.Log(ev.Params["params"]);
        });

        sfs.AddEventListener(SFSEvent.CONNECTION_LOST, ev => {
            Debug.Log("connection lost!!");
            //TODO : pause game and retry connection
        });

        sfs.AddEventListener(SFSEvent.CONNECTION_RETRY, ev => {
            Debug.Log("retrying connection!!");
            //TODO : pause game and retry connection
        });

        sfs.AddEventListener(SFSEvent.CONNECTION_RESUME, ev => {
            Debug.Log("resume connection!!");
            //TODO : pause game and retry connection
        });

        sfs.AddEventListener(SFSEvent.USER_ENTER_ROOM, ev => {
            User user = (User)ev.Params["user"];
            Room room = (Room)ev.Params["room"];
            Debug.Log(user.Name + " entered the room " + room.Name);
        });

        sfs.AddEventListener(SFSEvent.USER_EXIT_ROOM, ev => {
            Dictionary<string, object> param = new Dictionary<string,object>();
            param.Add("cmd", "userExit");
            subject.OnNext(new BaseEvent(SFSEvent.USER_EXIT_ROOM, param));

            User user = (User)ev.Params["user"];
            Room room = (Room)ev.Params["room"];
            Debug.Log(user.Name + " exited the room " + room.Name);
            subject.OnCompleted();
        });

        return subject.AsObservable();
    }

    public void PushGameEvent(string cmd, SFSObject sFSObject, bool udp = false)
    {
        sfs.Send(new ExtensionRequest(cmd, sFSObject, sfs.LastJoinedRoom, udp));
    }
}