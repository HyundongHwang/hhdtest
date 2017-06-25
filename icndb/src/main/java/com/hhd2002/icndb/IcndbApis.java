package com.hhd2002.icndb;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hhd on 2017-06-16.
 */

public interface IcndbApis {

    static IcndbApis create() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        IcndbApis icndbApis = new Retrofit.Builder()
                .baseUrl("http://api.icndb.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(IcndbApis.class);

        return icndbApis;
    }

    @GET("jokes/random")
    Call<GetJokesRandomResponse> GetJokesRandom(
            @Query("firstName") String firstName,
            @Query("lastName") String lastName,
            @Query("limitTo") String limitTo);

    class GetJokesRandomResponse {

        public String type;
        public Value value;

        public static class Value {
            public int id;
            public String joke;
            public String[] categories;
        }
    }
}
