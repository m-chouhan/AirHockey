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
            case "Multiplayer":
                //NetWrapper.Instance.InGameStream.Subscribe();
                break;
            case "SinglePlayer":
                break;
            default:
                //current.InputAdapter = new TouchInputAdapter();
                //other.InputAdapter = new TouchInputAdapter();
                break;
        }
    }

    public void BackToLobby()
    {
        Debug.Log("back to lobby pressed!");
        SceneManager.LoadScene("Lobby");
    }

}
