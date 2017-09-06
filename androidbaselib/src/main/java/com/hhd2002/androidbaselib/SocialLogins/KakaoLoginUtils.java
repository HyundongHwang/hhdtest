package com.hhd2002.androidbaselib.SocialLogins;

import android.content.Context;
import android.content.Intent;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

//https://developers.kakao.com
public class KakaoLoginUtils {


    private static Context _appContext;
    private static ILoginCallback _loginCallback;
    private static boolean _isInited;
    private static boolean _hadRegisterSessionCallback;


    public static void init(
            Context appContext,
            ILoginCallback loginCallback) {
        
        _appContext = appContext;
        _loginCallback = loginCallback;

        if (!_isInited) {
            KakaoSDK.init(_kakaoAdapter);
            Session.getCurrentSession().addCallback(_sessionCallback);
            _isInited = true;
        }

        Session.getCurrentSession().checkAndImplicitOpen();
    }



    public static void handleActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data);
        } catch (Exception ex) {
        }
    }


    public interface ILoginCallback {
        void onSuccessed(UserProfile userProfile);

        void onFailed(Exception ex);
    }


    private static KakaoAdapter _kakaoAdapter = new KakaoAdapter() {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         *
         * @return Session의 설정값.
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[]{AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return _appContext;
                }
            };
        }
    };


    private static ISessionCallback _sessionCallback = new ISessionCallback() {
        @Override
        public void onSessionOpened() {
            UserManagement.requestMe(new MeResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    _loginCallback.onFailed(new Exception(errorResult.toString()));
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    _loginCallback.onFailed(new Exception(errorResult.toString()));
                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    _loginCallback.onSuccessed(userProfile);
                }

                @Override
                public void onNotSignedUp() {
                    _loginCallback.onFailed(new Exception("onNotSignedUp"));
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            _loginCallback.onFailed(exception);
        }
    };


}


