package com.hhd2002.androidbaselib.Thread;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;

/**
 * Created by hhd on 2017-06-29.
 */

public abstract class HhdAsyncTask {
    private Handler _handler;

    @SuppressLint("StaticFieldLeak")
    private AsyncTask<Void, Void, Void> _task = new AsyncTask<Void, Void, Void>() {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            HhdAsyncTask.this.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HhdAsyncTask.this.doInBackground();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            HhdAsyncTask.this.onPostExecute();
            this.publishProgress();
        }
    };

    public void execute() {
        _task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected abstract void doInBackground();

    protected void onPreExecute() {
    }

    protected void onPostExecute() {
    }

    protected final void runOnUiThread(Runnable action) {
        _handler = new Handler();
        _handler.post(action);
    }
}