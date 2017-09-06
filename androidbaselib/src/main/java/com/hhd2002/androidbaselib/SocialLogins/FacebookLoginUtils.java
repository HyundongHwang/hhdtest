package com.hhd2002.androidbaselib.SocialLogins;

import android.app.Activity;
import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

//https://m.blog.naver.com/tkddlf4209/220724321822
//https://developers.facebook.com/quickstarts/686556748205147/?platform=android
//https://developers.facebook.com/apps/686556748205147/settings/
//f26f155b61847337c60475bdd7082b87

public class FacebookLoginUtils {


    private static boolean _isInited;
    private static ILoginCallback _loginCallback;
    private static CallbackManager _callbackManager;


    public static void init(ILoginCallback loginCallback) {
        init(null, loginCallback);
    }


    public static void init(LoginButton loginButton, ILoginCallback loginCallback) {
        _loginCallback = loginCallback;

        if (loginButton != null)
            loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

        if (_isInited)
            return;


        _callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(_callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        //loginResult.getAccessToken() 정보를 가지고 유저 정보를 가져올수 있습니다.
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        String profileUrl = "https://graph.facebook.com/" + loginResult.getAccessToken().getUserId() + "/picture?type=large";
                                        _loginCallback.onSuccessed(loginResult, object, response, profileUrl);
                                    }
                                });

                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        _loginCallback.onFailed(new Exception("FacebookCallback LoginManager onCancel"));
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        _loginCallback.onFailed(exception);
                    }
                });

        _isInited = true;
    }


    public static void login(Activity activity) {
        LoginManager.getInstance().logInWithReadPermissions(
                activity,
                Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

    }


    public static void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (_callbackManager != null) {
            try {
                _callbackManager.onActivityResult(requestCode, resultCode, data);
            } catch (Exception ex) {
            }
        }
    }


    public interface ILoginCallback {
        void onSuccessed(LoginResult loginResult, JSONObject object, GraphResponse response, String profileUrl);

        void onFailed(Exception ex);
    }
}
