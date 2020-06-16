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
using System.Collections.Generic;
using Sfs2X.Entities;
using System.Threading.Tasks;
using UniRx;

public class LoginController : MonoBehaviour {

    public TMP_InputField inputField;
    public TMP_Text errorText;
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
            errorText.text = "username is empty!!";
            return;
        }

        NetWrapper.Instance.Login(inputField.text).Subscribe(
            (ev) => Debug.Log(ev), 
            (ev) => errorText.text = ev.Message,
            () => {
                PlayerPrefs.SetString("name", inputField.text);
                SceneManager.LoadScene("Lobby");
            }
        );
    }
}