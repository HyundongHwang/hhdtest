package com.hhd2002.icndb;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.hhd2002.androidbaselib.FuncDelegate.IHhdFuncDelegateIn;

import java.io.IOException;
import retrofit2.Call;

/**
 * Created by hhd on 2017-06-16.
 */
public class JokeFinder {

    public void GetJoke(String first, String last, IHhdFuncDelegateIn<String> onResultDele) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                String first = strings[0];
                String last = strings[1];
                IcndbApis icndbApis = IcndbApis.create();

                try {
                    Call<IcndbApis.GetJokesRandomResponse> icndbJokeCall = icndbApis.GetJokesRandom(first, last, "[nerdy]");
                    IcndbApis.GetJokesRandomResponse body = icndbJokeCall.execute().body();
                    onResultDele.execute(body.value.joke);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, first, last);
    }
}
