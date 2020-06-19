using Sfs2X.Entities.Data;
using UniRx;
using UnityEngine;
using UnityEngine.SceneManagement;

public class GC : MonoBehaviour
{
    public GameObject gameEndPanel;
    public Camera cam;
    public Player current, other;
    public Puck puck;

    void Awake()
    {
        Application.runInBackground = true;

        switch (Scenes.getParam("Mode"))
        {
            case "Multiplayer-Online":
                current.touch = true;
                TouchAdapter
                    .FetchTouchEvents(current.gameObject)
                    .Where(ev => current.capture(ev))
                    .Subscribe(ev =>
                    {
                        current.SetPosition(ev.x, ev.y);
                        NetWrapper.Instance.PushGameEvent("move", current.ToSFS(), true);
                    })
                    .AddTo(this);

                var gameStream = NetWrapper.Instance.FetchGameEvents();
                /*
                var startEvent = gameStream
                                    .Where(ev => ((string)ev.Params["cmd"]).Equals("start"));
                var resetEvent = gameStream
                                    .Where(ev => ((string)ev.Params["cmd"]).Equals("reset"));
                var scoreUpdateEvent = gameStream
                                    .Where(ev => ((string)ev.Params["cmd"]).Equals("scoreUpdate"));
                var moveEvent = gameStream
                                    .Where(ev => ((string)ev.Params["cmd"]).Equals("move"))
                                    .Select(ev => {
                                        SFSObject data = (SFSObject)ev.Params["params"];
                                        var pos = data.GetSFSObject(other.id.ToString());
                                        return new Vector2(pos.GetFloat("x"), pos.GetFloat("y"));
                                    });
                var endEvent = gameStream
                                    .Where(ev => ((string)ev.Params["cmd"]).Equals("end"));
                moveEvent
                    .Subscribe(ev => {
                        other.SetPosition(ev.x, ev.y);
                    }).AddTo(this);
                */
                gameStream.Subscribe(ev => GameEventHandler((string)ev.Params["cmd"],
                                            (SFSObject)ev.Params["params"]));

                NetWrapper.Instance.PushGameEvent("ready", new SFSObject(), false);
                break;
            default:
            case "Multiplayer-Local":
                current.touch = true;
                other.touch = true;
                TouchAdapter
                    .FetchTouchEvents(current.gameObject)
                    .Where(ev => current.capture(ev))
                    .Subscribe(ev =>
                    {
                        current.SetPosition(ev.x, ev.y);
                    })
                    .AddTo(this);
                TouchAdapter
                    .FetchTouchEvents(other.gameObject)
                    .Where(ev => other.capture(ev))
                    .Subscribe(ev =>
                    {
                        other.SetPosition(ev.x, ev.y);
                    })
                    .AddTo(this);
                break;
            case "Singleplayer":
                break;
        }
    }

    public void BackToLobby()
    {
        Debug.Log("back to lobby pressed!");
        SceneManager.LoadScene("Lobby");
    }

    private void GameEventHandler(string cmd, SFSObject data)
    {
        string curId = current.id.ToString();
        string otherId = other.id.ToString();

        switch (cmd)
        {
            case "start":
                current.FromSFS(data.GetSFSObject("current"));
                other.FromSFS(data.GetSFSObject("other"));
                if (current.transform.position.y > 0)
                    cam.transform.rotation = Quaternion.Euler(0, 0, 180);
                current.gameObject.name = "me";
                other.gameObject.name = "other";
                puck.gameObject.name = "puck";
                Debug.Log("other : " + other.id + ", current : " + current.id);
                break;
            case "move":
                if (data.ContainsKey(curId)) current.SetPosition(data.GetSFSObject(curId));
                if (data.ContainsKey(otherId)) other.SetPosition(data.GetSFSObject(otherId));
                if (data.ContainsKey("puck")) puck.SetPosition(data.GetSFSObject("puck"));
                break;
            case "updateScore":
                if (data.ContainsKey(curId)) current.SetScore(data.GetInt(curId));
                if (data.ContainsKey(otherId)) other.SetScore(data.GetInt(otherId));
                break;
            case "reset":
                current.SetPosition(data.GetSFSObject(curId));
                other.SetPosition(data.GetSFSObject(otherId));
                puck.SetPosition(data.GetSFSObject("puck"));
                break;
            case "end":
                bool won = data.GetUtfString("won").Equals(curId);
                Debug.Log("result : " + data.GetUtfString("won") + " has won!");
                if (!won) gameEndPanel.transform.rotation = Quaternion.Euler(0, 0, 180);
                gameEndPanel.SetActive(true);
                break;
        }
    }
}
