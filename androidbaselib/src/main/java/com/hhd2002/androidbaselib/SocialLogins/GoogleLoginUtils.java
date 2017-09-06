package com.hhd2002.androidbaselib.SocialLogins;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;


//https://m.blog.naver.com/tkddlf4209/220723503941
//https://console.developers.google.com/apis/credentials?project=deltaisland-1499522107483
public class GoogleLoginUtils {


    private static final int RC_SIGN_IN = 1000;
    private static FragmentActivity _activity;
    private static ILoginCallback _loginCallback;
    private static boolean _isInited;
    private static GoogleApiClient _googleApiClient;


    public static void login(
            FragmentActivity activity,
            ILoginCallback loginCallback) {

        _activity = activity;
        _loginCallback = loginCallback;


        
        if (!_isInited) {
            // [START configure_signin]
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            // [END configure_signin]


            // [START build_client]
            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            /* FragmentActivity *//* OnConnectionFailedListener */
            _googleApiClient = new GoogleApiClient.Builder(activity)
                    .enableAutoManage(_activity /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            _loginCallback.onFailed(new Exception(connectionResult.toString()));
                        }
                    } /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            // [END build_client]


            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(_googleApiClient);

            if (opr.isDone()) {
                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                // and the GoogleSignInResult will be available instantly.
                GoogleSignInResult result = opr.get();
                _loginCallback.onSuccessed(result);
            } else {
                // If the user has not previously signed in on this device or the sign-in has expired,
                // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                // single sign-on will occur in this branch.
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        if (googleSignInResult.getSignInAccount() != null) {
                            _loginCallback.onSuccessed(googleSignInResult);
                        }
                    }
                });
            }

            _isInited = true;
        }


        
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(_googleApiClient);
        _activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public interface ILoginCallback {
        void onSuccessed(GoogleSignInResult gsiResult);

        void onFailed(Exception ex);
    }


    public static void handleActivityResult(int requestCode, int resultCode, Intent data) {
        
        
        
        try {
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if (_loginCallback != null) {
                    if (result.isSuccess()) {
                        _loginCallback.onSuccessed(result);
                    } else {
                        _loginCallback.onFailed(new Exception(result.toString()));
                    }
                }
            }
        } catch (Exception ex) {
        }
        
        
        
    }

}
