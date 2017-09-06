package com.hhd2002.androidbaselib.SampleUI;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hhd2002.androidbaselib.R;

import java.util.ArrayList;

public class HhdSampleUiHelper {

    private final Activity activity;
    private final RvAdapter adapter;
    private final LinearLayout llayout;
    private final RecyclerView rvObj;

    public HhdSampleUiHelper(Activity activity) {
        this.activity = activity;

        //activity.requestWindowFeature(Window.FEATURE_ACTION_BAR);

        if (this.activity instanceof AppCompatActivity) {
            AppCompatActivity acActivity = (AppCompatActivity) this.activity;
            android.support.v7.app.ActionBar actionBar = acActivity.getSupportActionBar();

            if (actionBar != null) {
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayUseLogoEnabled(true);
                actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
                actionBar.setTitle(activity.getLocalClassName());
                actionBar.setIcon(R.mipmap.ic_launcher_round);
                actionBar.show();
            }
        } else {
            ActionBar actionBar = this.activity.getActionBar();

            if (actionBar != null) {
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayUseLogoEnabled(true);
                actionBar.setTitle(activity.getLocalClassName());
                actionBar.setIcon(R.mipmap.ic_launcher_round);
                actionBar.show();
            }
        }


        LinearLayout llRoot = new LinearLayout(activity);
        activity.setContentView(llRoot);
        llRoot.setOrientation(LinearLayout.VERTICAL);

        {
            ViewGroup.LayoutParams lp = llRoot.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            llRoot.setLayoutParams(lp);
        }

        ScrollView svObj = new ScrollView(activity);
        llRoot.addView(svObj);

        {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) svObj.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = 0;
            lp.weight = 50;
            svObj.setLayoutParams(lp);
        }

        llayout = new LinearLayout(activity);
        svObj.addView(llayout);
        llayout.setOrientation(LinearLayout.VERTICAL);

        {
            ViewGroup.LayoutParams lp = llayout.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            llayout.setLayoutParams(lp);
        }

        LinearLayout llToolbar = new LinearLayout(activity);
        llRoot.addView(llToolbar);
        llToolbar.setOrientation(LinearLayout.HORIZONTAL);

        {
            ViewGroup.LayoutParams lp = llToolbar.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            llToolbar.setLayoutParams(lp);
        }

        final ToggleButton btnLineNumber = new ToggleButton(activity);
        llToolbar.addView(btnLineNumber);
        btnLineNumber.setTextOn("줄번호");
        btnLineNumber.setTextOff("줄번호");
        btnLineNumber.setText("줄번호");
        btnLineNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.notifyDataSetChanged();
            }
        });

        {
            ViewGroup.LayoutParams lp = btnLineNumber.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            btnLineNumber.setLayoutParams(lp);
        }

        Button btnClear = new Button(activity);
        llToolbar.addView(btnClear);
        btnClear.setText("Clear");
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLog();
            }
        });

        {
            ViewGroup.LayoutParams lp = btnClear.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            btnClear.setLayoutParams(lp);
        }

        rvObj = new RecyclerView(activity);
        llRoot.addView(rvObj);

        {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) rvObj.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = 0;
            lp.weight = 50;
            rvObj.setLayoutParams(lp);
        }

        adapter = new RvAdapter(new RvAdapterListener() {
            @Override
            public boolean queryIsLineNumberOn() {
                boolean checked = btnLineNumber.isChecked();
                return checked;
            }
        });

        rvObj.setAdapter(adapter);
        rvObj.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        adapter.notifyDataSetChanged();
    }

    public void addSimpleBtn(String btnStr, View.OnClickListener onClickListener) {
        Button btn = new Button(activity);
        llayout.addView(btn);
        btn.setText(btnStr);

        {
            ViewGroup.LayoutParams lp = btn.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            btn.setLayoutParams(lp);
        }

        btn.setOnClickListener(onClickListener);
    }

    public void writeLog(String log) {
        adapter.items.add(log);
        int lastPostion = adapter.items.size() - 1;
        adapter.notifyItemInserted(lastPostion);
        rvObj.smoothScrollToPosition(lastPostion);
    }

    public void clearLog() {
        adapter.items.clear();
        adapter.notifyDataSetChanged();
    }

    public void addView(View childView, int width, int height) {
        llayout.addView(childView);

        {
            ViewGroup.LayoutParams lp = childView.getLayoutParams();
            lp.width = width;
            lp.height = height;
            childView.setLayoutParams(lp);
        }
    }

    private static class RvAdapter extends RecyclerView.Adapter {
        private RvAdapterListener listener;
        public ArrayList<String> items = new ArrayList<>();

        private RvAdapter(RvAdapterListener listener) {
            this.listener = listener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView itemView = new TextView(parent.getContext());
            return new ItemVh(itemView, listener);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String item = items.get(position);
            ItemVh itemVh = (ItemVh) holder;
            itemVh.onBindViewHolder(item, position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private static class ItemVh extends RecyclerView.ViewHolder {
        private TextView tvObj;
        private RvAdapterListener listener;

        public ItemVh(View itemView, RvAdapterListener listener) {
            super(itemView);
            this.tvObj = (TextView) itemView;
            this.listener = listener;
        }

        public void onBindViewHolder(String item, int position) {
            StringBuilder sb = new StringBuilder();

            if (listener.queryIsLineNumberOn()) {
                sb.append(String.format("[%d] ", position));
            }

            sb.append(item);
            tvObj.setText(sb.toString());
        }
    }

    private static interface RvAdapterListener {
        public boolean queryIsLineNumberOn();
    }
}