using Sfs2X.Entities.Data;
using UniRx;
using UnityEngine;
using UnityEngine.SceneManagement;

public class GC : MonoBehaviour
{
    public GameObject gameWonPanel;
    public GameObject gameLosePanel;
    public Camera cam;
    public Player current, other;
    public Puck puck;

    void Awake() { 
        Application.runInBackground = true;

        Observable
            .Create<int>((arg) => { 
                for(int i = 0; i < 10; ++i)
                    arg.OnNext(i); 
                arg.OnCompleted(); 
                return Disposable.Empty; 
            })
            .Where(i => i > 5)
            //.AsUnitObservable()
            .Subscribe(item => Debug.Log(item));

        switch(Scenes.getParam("Mode"))
        {
            case "Multiplayer-Online":
                current.touchEnabled = true;
                TouchAdapter
                    .FetchTouchEvents(current.gameObject)
                    .Do(item => Debug.Log(item))
                    .Where(ev => current.capture(ev))
                    .Subscribe(ev => {
                        current.SetPosition(ev.x, ev.y);
                        NetWrapper.Instance.PushGameEvent("move", current.ToSFS());
                    })
                    .AddTo(this);
                NetWrapper.Instance
                    .FetchGameEvents()
                    .Where(ev => ((string)ev.Params["cmd"]).Equals("move"))
                    .Subscribe(ev => {
                        SFSObject data = (SFSObject)ev.Params["params"];
                        var pos = data.GetSFSObject(other.id.ToString());
                        other.SetPosition(pos.GetFloat("x"), pos.GetFloat("y"));
                    })
                    .AddTo(this);

                //NetWrapper.Instance.InGameStream.Subscribe();
                break;
            default:
            case "Multiplayer-Local":
                current.touchEnabled = true;
                other.touchEnabled = true;
                TouchAdapter
                    .FetchTouchEvents(current.gameObject)
                    .Where(ev => current.capture(ev))
                    .Subscribe(ev => {
                        current.SetPosition(ev.x, ev.y);
                    })
                    .AddTo(this);
                TouchAdapter
                    .FetchTouchEvents(other.gameObject)
                    .Where(ev => other.capture(ev))
                    .Subscribe(ev => {
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

}
