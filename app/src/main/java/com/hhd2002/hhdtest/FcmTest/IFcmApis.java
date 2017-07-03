package com.hhd2002.hhdtest.FcmTest;

import com.hhd2002.androidbaselib.HhdRetrofitUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by hhd on 2017-07-03.
 */

public interface IFcmApis {

    class SendRequest {
        public IFcmApis.SendRequest.Notification notification;
        public String to;
        public String collapse_key;
        public Object data;

        public static class Notification {
            public String title;
            public String body;
            public String icon;
        }
    }


    class SendResponse {
        public long multicast_id;
        public int success;
        public int failure;
        public int canonical_ids;
        public ArrayList<IFcmApis.SendResponse.Result> results;

        class Result {
            public String message_id;
        }
    }
    
    
    static IFcmApis create() {
        IFcmApis iFcmApis = HhdRetrofitUtils.create("https://fcm.googleapis.com", IFcmApis.class);
        return iFcmApis;
    }

    @Headers({
            "Content-Type: application/json",

            //AAAAnlu9ieg:APA91...
            "Authorization: key=" + MyFcmSecureKeys.FCM_SERVER_KEY
    })
    @POST("fcm/send")
    Call<IFcmApis.SendResponse> PostSend(
            @Body IFcmApis.SendRequest body);
}













//다음은 알림 메시지입니다.
//
//POST https://fcm.googleapis.com/fcm/send HTTP/1.1
//User-Agent: Fiddler
//Content-Type: application/json
//Authorization: key=AAAAnlu9ieg:APA91...
//Host: fcm.googleapis.com
//Content-Length: 251
//
//{ "notification": {
//"title": "Portugal vs. Denmark",
//"body": "5 to 1"
//},
//"to" : "e9XB4tisj58:APA91bGsSgq3fAlP7BG7ilfSDsnP-nSvGxeuFLMRr-1-3K1Kd060PGoLDLpczR8phNEhxtkkbnRJ6n9zJef52X6zKOU-JHNxIluQHC48ShyuLnv6df34K68UaiX1qy4dnM94gwN08n05"
//}
//
//HTTP/1.1 200 OK
//Content-Type: application/json; charset=UTF-8
//Date: Mon, 26 Jun 2017 14:55:13 GMT
//Expires: Mon, 26 Jun 2017 14:55:13 GMT
//Cache-Control: private, max-age=0
//X-Content-Type-Options: nosniff
//X-Frame-Options: SAMEORIGIN
//X-XSS-Protection: 1; mode=block
//Server: GSE
//Alt-Svc: quic=":443"; ma=2592000; v="38,37,36,35"
//Accept-Ranges: none
//Vary: Accept-Encoding
//Transfer-Encoding: chunked
//
//{"multicast_id":8003195639264160362,"success":1,"failure":0,"canonical_ids":0,"results":[{"message_id":"0:1498488913950468%20fe25bf20fe25bf"}]}
//
//
//
//다음은 데이터 페이로드가 포함된 메시지입니다.
//
//POST https://fcm.googleapis.com/fcm/send HTTP/1.1
//User-Agent: Fiddler
//Content-Type: application/json
//Authorization: key=AAAAnlu9ieg:APA91...
//Host: fcm.googleapis.com
//Content-Length: 287
//
//{ "collapse_key": "score_update",
//"time_to_live": 108,
//"data": {
//"score": "4x8",
//"time": "15:16.2342"
//},
//"to" : "e9XB4tisj58:APA91bGsSgq3fAlP7BG7ilfSDsnP-nSvGxeuFLMRr-1-3K1Kd060PGoLDLpczR8phNEhxtkkbnRJ6n9zJef52X6zKOU-JHNxIluQHC48ShyuLnv6df34K68UaiX1qy4dnM94gwN08n05"
//}
//HTTP/1.1 200 OK
//Content-Type: application/json; charset=UTF-8
//Date: Mon, 26 Jun 2017 14:28:54 GMT
//Expires: Mon, 26 Jun 2017 14:28:54 GMT
//Cache-Control: private, max-age=0
//X-Content-Type-Options: nosniff
//X-Frame-Options: SAMEORIGIN
//X-XSS-Protection: 1; mode=block
//Server: GSE
//Alt-Svc: quic=":443"; ma=2592000; v="39,38,37,36,35"
//Accept-Ranges: none
//Vary: Accept-Encoding
//Transfer-Encoding: chunked
//
//{"multicast_id":7398904813536975373,"success":1,"failure":0,"canonical_ids":0,"results":[{"message_id":"0:1498487334945940%20fe25bf66d6cf16"}]}

