package com.hhd2002.hhdtest.EventBusTest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hhd2002.hhdtest.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.eventbus.util.AsyncExecutor;
import org.greenrobot.eventbus.util.ThrowableFailureEvent;

import java.util.Random;

public class EventBusTestActivity
        extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        setContentView(R.layout.layout_event_bus_test);
        tvResult = (TextView) findViewById(R.id.tv_result);
        MyEvent stickyEvent = EventBus.getDefault().getStickyEvent(MyEvent.class);

        if (stickyEvent != null) {
            updateUiByMyEvent(stickyEvent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onClickAsyncExecuter(View view) {
        onClickAsyncExecuter(false);
    }

    public void onAsyncExecuterPostSticky(View view) {
        onClickAsyncExecuter(true);
    }

    private void onClickAsyncExecuter(final boolean useSticky) {
        Random random = new Random();
        final int a = random.nextInt(10);
        final int b = random.nextInt(10);
        Log.i("hhddebug", "4783 a : " + a);
        Log.i("hhddebug", "8361 b : " + b);

        AsyncExecutor.create().execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                long tid = Thread.currentThread().getId();

                for (int i = 0; i < 10; i++) {
                    Log.i("hhddebug", "tid : " + tid + ", i : " + i);
                    Thread.sleep(1000);
                }

                MyEvent event = new MyEvent();
                event.tid = tid;
                event.a = a;
                event.b = b;
                event.c = a + b;

                if (useSticky) {
                    EventBus.getDefault().postSticky(event);
                } else {
                    EventBus.getDefault().post(event);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MyEvent event) {
        updateUiByMyEvent(event);
    }

    private void updateUiByMyEvent(MyEvent event) {
        String log = "event : " + new Gson().toJson(event);
        Log.i("hhddebug", log);
        tvResult.setText(log);
    }

    public void onClickErrorDialog(View view) {
        AsyncExecutor.create().execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {

                try {
                    int a = 1;
                    int b = 0;
                    int c = a / b;
                } catch (Exception ex) {
                    EventBus.getDefault().post(new ThrowableFailureEvent(ex));
                }

                MyEvent event = new MyEvent();
                EventBus.getDefault().post(event);
            }
        });
    }

    public class MyEvent {
        public int a;
        public int b;
        public int c;
        public long tid;
    }
}
