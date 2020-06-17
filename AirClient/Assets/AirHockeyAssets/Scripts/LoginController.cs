using UnityEngine;
using UnityEngine.SceneManagement;
using TMPro;
using UniRx;
using Sfs2X.Requests;

public class LoginController : MonoBehaviour {

    public TMP_InputField inputField;
    public TMP_Text error;
    public GameObject loginView;

    void Awake() {
		Application.runInBackground = true;
        inputField.text = PlayerPrefs.GetString("name");
	}
	
    public void ToggleLoginView()
    {
        loginView.SetActive(!loginView.activeSelf);
    }

    public void OnLoginButtonClick() {
        if (string.IsNullOrEmpty(inputField.text))
        {
            error.text = "username is empty!!";
            return;
        }

        NetWrapper.Instance.Login(inputField.text).Subscribe(
            (ev) => Debug.Log(ev), 
            (ev) => error.text = ev.Message,
            () => {
                PlayerPrefs.SetString("name", inputField.text);
                NetWrapper.Instance
                    .JoinRoom(new JoinRoomRequest("The Lobby"))
                    .Subscribe(
                        (item) => {},
                        err => {
                            Debug.Log(err.Message);
                            error.text = err.Message;
                        },
                        () => Scenes.Load("Lobby", "username", inputField.text)
                    ).AddTo(this);
            }
        ).AddTo(this);
    }
}