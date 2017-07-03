package com.hhd2002.hhdtest.GlideTest.apis;

import com.hhd2002.androidbaselib.HhdRetrofitUtils;
import com.hhd2002.hhdtest.MySecureKeys;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by hhd on 2017-07-03.
 */

public interface IDaumApis {
    class SearchImageResponse {
        public SearchImageResponse.Channel channel;

        public static class Channel {
            public int result;
            public int pageCount;
            public String title;
            public int totalCount;
            public String description;
            public ArrayList<SearchImageResponse.Item> item;
        }

        public static class Item {
            public String pubDate;
            public String title;
            public String thumbnail;
            public String cp;
            public int height;
            public String link;
            public int width;
            public String image;
        }
    }


    static IDaumApis create() {
        IDaumApis apis = HhdRetrofitUtils.create("http://apis.daum.net", IDaumApis.class);
        return apis;
    }


    @Headers({
            "Content-Type: application/json",
    })
    @GET("search/image?output=json&apikey=" + MySecureKeys.DAUM_OPEN_API_KEY)
    Call<IDaumApis.SearchImageResponse> GetSearchImage(
            @Query("q") String q,
            @Query("result") int result,
            @Query("pageno") int pageno
    );
}


//"http://apis.daum.net/search/image?output=json&apikey=12134b96690b90ec58897cb715d57a1e&q=" + searchStr + "&result=20&pageno=1";

