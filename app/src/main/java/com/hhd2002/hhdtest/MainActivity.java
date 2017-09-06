package com.hhd2002.hhdtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import com.hhd2002.androidbaselib.Adapters.HhdListViewAdapter;
import com.hhd2002.androidbaselib.Adapters.HhdListViewHolder;

import java.util.ArrayList;

public class MainActivity
        extends AppCompatActivity {

    private SwipeRefreshLayout vgPtr;

    @SuppressLint("HandlerLeak")
    private Handler onPtrFinished = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            vgPtr.setRefreshing(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_2);

        vgPtr = (SwipeRefreshLayout) findViewById(R.id.vg_ptr);
        ListView lvObj = (ListView) findViewById(R.id.lv_obj);

        vgPtr.setColorSchemeColors(0xff5b79c2);

        vgPtr.setOnRefreshListener(() -> onPtrFinished.sendEmptyMessageDelayed(0, 3000));

        ArrayList<Class> itemTypes = new ArrayList<>();
        itemTypes.add(ItemModel.class);

        ArrayList<Class<? extends HhdListViewHolder>> vhTypes = new ArrayList<>();
        vhTypes.add(ItemVh.class);

        HhdListViewAdapter adapter = new HhdListViewAdapter(
                this,
                itemTypes,
                vhTypes,
                (AdapterListener) itemModel -> {
                    startActivity(new Intent(getBaseContext(), itemModel.activityType));
                });

        lvObj.setAdapter(adapter);

        PackageManager pm = getPackageManager();
        PackageInfo pi = null;
        String packageName = getPackageName();
        ArrayList<ItemModel> items = new ArrayList<>();

        try {
            pi = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            Log.e("hhddebug", e.toString());
        }

        for (ActivityInfo activity : pi.activities) {
            if (activity.name.contains(packageName)) {
                ItemModel newItem = new ItemModel();

                try {
                    newItem.activityType = Class.forName(activity.name);
                } catch (ClassNotFoundException e) {
                    Log.e("hhddebug", e.toString());
                }

                items.add(newItem);
            }
        }

        adapter.items.clear();
        adapter.items.addAll(items);
    }

    public static class ItemModel {
        public Class<?> activityType;
    }


    public static class ItemVh extends HhdListViewHolder {
        private View convertView;
        private ItemModel itemModel;
        private TextView tvTitle;
        private TextView tvDesc;

        @Override
        public View createNewView(Context context, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_main, parent, false);
            tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);

            convertView.setOnClickListener(v -> {
                AdapterListener listener = (AdapterListener) adapterListener;
                listener.onClickItem(itemModel);
            });

            return convertView;
        }

        @Override
        public void updateUiByItem(int position, View convertView, ViewGroup parent, Object item) {
            itemModel = (ItemModel) item;
            Object newActivity = null;

            try {
                newActivity = itemModel.activityType.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            tvTitle.setText(itemModel.activityType.getSimpleName());
            tvDesc.setText(itemModel.activityType.getName());
        }
    }


    public interface AdapterListener {
        void onClickItem(ItemModel itemModel);
    }
}
