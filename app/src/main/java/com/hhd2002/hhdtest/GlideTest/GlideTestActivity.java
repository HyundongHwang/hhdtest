package com.hhd2002.hhdtest.GlideTest;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hhd2002.androidbaselib.HhdUtil;
import com.hhd2002.androidbaselib.IHhdSampleActivity;
import com.hhd2002.hhdtest.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.eventbus.util.AsyncExecutor;
import org.parceler.Parcel;

import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.bumptech.glide.request.RequestOptions.centerCropTransform;


public class GlideTestActivity
        extends AppCompatActivity
        implements IHhdSampleActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000;
    private CustomAdapter adapter;
    private EditText etSearch;
    private String EVENT_NAME_RELOAD_IMAGES = "EVENT_NAME_RELOAD_IMAGES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_glide_test);
        _Request_READ_EXT_PER();

//        Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(new OkHttpClient()));

        RecyclerView rvObj = (RecyclerView) findViewById(R.id.rv_obj);
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvObj.setLayoutManager(lm);

        adapter = new CustomAdapter(item -> startActivity(GlideTestDetailActivity.newIntent(this, item)));

        rvObj.setAdapter(adapter);

        etSearch = (EditText) findViewById(R.id.et_search);

        findViewById(R.id.btn_naver).setOnClickListener(v -> onClickNaver());
        findViewById(R.id.btn_daum).setOnClickListener(v -> onClickDaum());
        findViewById(R.id.btn_local).setOnClickListener(v -> onClickLocal());
    }

    private void _Request_READ_EXT_PER() {
        // Here, thisActivity is the current activity
        int isPer = ContextCompat.checkSelfPermission(
                this,
                "android.permission.READ_EXTERNAL_STORAGE");

        if (isPer != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    "android.permission.READ_EXTERNAL_STORAGE")) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{"android.permission.READ_EXTERNAL_STORAGE"},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void onClickLocal() {
        adapter.items.clear();

        AsyncExecutor.create().execute(() -> {
            Uri mediaStoreThumbnailUriBase = Uri.parse("content://media/external/images/media");
            Cursor cur = null;

            try {
                ContentResolver cr = getContentResolver();
                cur = cr.query(mediaStoreThumbnailUriBase, null, null, null, null);
                int cursorRowCount = cur.getCount();

                if (cursorRowCount == 0)
                    return;

                for (int row = 0; row < cursorRowCount; row++) {
                    Item newItem = new Item();

                    cur.moveToPosition(row);
                    int colCount = cur.getColumnCount();

                    for (int col = 0; col < colCount; col++) {
                        String columnName = cur.getColumnName(col);
                        String value = cur.getString(col);

                        switch (columnName) {
                            case "_id":
                                newItem.mediaStoreRealUri = Uri.withAppendedPath(mediaStoreThumbnailUriBase, value);
                                break;
                            case "width":
                                newItem.width = Integer.parseInt(value);
                                break;
                            case "height":
                                newItem.height = Integer.parseInt(value);
                                break;
                        }
                    }

                    adapter.items.add(0, newItem);
                }
            } catch (Exception e) {
                Log.i("hhddebug", "e : " + e);
            } finally {
                if (cur != null) {
                    cur.close();
                }
            }

            EventBus.getDefault().post(EVENT_NAME_RELOAD_IMAGES);
        });
    }

    private void onClickNaver() {
        AsyncExecutor.create().execute(() -> {
            OkHttpClient client = new OkHttpClient();
            String searchStr = URLEncoder.encode(etSearch.getText().toString(), "UTF-8");
            String url = "http://openapi.naver.com/search?key=c1b406b32dbbbbeee5f2a36ddc14067f&query=" + searchStr + "&target=image&start=1&display=10";

            Request req = new Request
                    .Builder()
                    .url(url)
                    .build();

            Response res = client.newCall(req).execute();
            String resStr = res.body().string();

//            XStream xStream = new XStream();
//            xStream.alias("rss", NaverModels.Rss.class);
//            xStream.alias("channel", NaverModels.Channel.class);
//            xStream.alias("item", NaverModels.Item.class);
//
//            resStr = resStr.replace("</display>", "</display><items>");
//            resStr = resStr.replace("</channel>", "</items></channel>");
//
//            NaverModels.Rss rss = (NaverModels.Rss) xStream.fromXML(resStr);
//            adapter.items.clear();
//
//            for (NaverModels.Item item : rss.channel.items) {
//                Item newItem = new Item();
//                adapter.items.add(newItem);
//                newItem.thumbnailUrl = item.thumbnail;
//                newItem.title = item.title;
//                newItem.link = item.link;
//                newItem.width = item.sizewidth;
//                newItem.height = item.sizeheight;
//            }
//
//            EventBus.getDefault().post(EVENT_NAME_RELOAD_IMAGES);
        });
    }

    private void onClickDaum() {
        AsyncExecutor.create().execute(() -> {
            OkHttpClient client = new OkHttpClient();
            String searchStr = URLEncoder.encode(etSearch.getText().toString(), "UTF-8");
            String url = "http://apis.daum.net/search/image?output=json&apikey=12134b96690b90ec58897cb715d57a1e&q=" + searchStr + "&result=20&pageno=1";

            Request req = new Request
                    .Builder()
                    .url(url)
                    .build();

            Response res = client.newCall(req).execute();
            String resStr = res.body().string();
            DaumModels daumModels = new Gson().fromJson(resStr, DaumModels.class);
            adapter.items.clear();

            for (DaumModels.Item item : daumModels.channel.item) {
                Item newItem = new Item();
                adapter.items.add(newItem);
                newItem.thumbnailUrl = item.thumbnail;
                newItem.title = item.title;
                newItem.link = item.link;
                newItem.width = item.width;
                newItem.height = item.height;
            }

            EventBus.getDefault().post(EVENT_NAME_RELOAD_IMAGES);
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(String msg) {
        if (msg.equals(EVENT_NAME_RELOAD_IMAGES)) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public String getSampleDesc() {
        return "Glide, RecyclerView, OkHttpClient, XML, XStream, AsyncExecutor, Json, Naver OpenAPI, Daum OpenAPI";
    }

    public static interface CustomAdapterLisener {
        public void onClickItem(Item item);
    }

    public static class CustomAdapter extends RecyclerView.Adapter {
        private CustomAdapterLisener adapterLisener;
        public ArrayList<Item> items = new ArrayList<>();

        public CustomAdapter(CustomAdapterLisener adapterLisener) {
            this.adapterLisener = adapterLisener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View convertView = inflater.inflate(R.layout.item_glide_test, parent, false);
            return new ItemVh(convertView, adapterLisener);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Item item = items.get(position);
            ItemVh vh = (ItemVh) holder;
            vh.onBindViewHolder(item, position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public static class ItemVh extends RecyclerView.ViewHolder {

        private View convertView;
        private ImageView imgObj;
        private Item item;

        public ItemVh(View convertView, CustomAdapterLisener adapterLisener) {
            super(convertView);
            this.convertView = convertView;
            imgObj = (ImageView) convertView.findViewById(R.id.img_obj);
            imgObj.setOnClickListener((v) -> {
                if (item.mediaStoreRealUri == null)
                    return;

                adapterLisener.onClickItem(item);
            });
        }

        public void onBindViewHolder(Item item, int position) {
            this.item = item;
            int displayWidth = HhdUtil.getDisplayWidth(convertView.getContext()) / 3;
            int displayHeight = displayWidth * item.height / Math.max(item.width, 1);
            imgObj.getLayoutParams().width = displayWidth;
            imgObj.getLayoutParams().height = displayHeight;

            if (!HhdUtil.isStringNullOrEmpty(item.thumbnailUrl)) {
                Glide.with(convertView.getContext())
                        .load(item.thumbnailUrl)
                        .transition(withCrossFade())
//                        .preload(displayWidth, displayHeight)
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .crossFade()
//                        .centerCrop()
                        .apply(centerCropTransform())
                        .into(imgObj);
            } else if (item.mediaStoreRealUri != null) {
                Glide.with(convertView.getContext())
                        .load(item.mediaStoreRealUri)
                        .transition(withCrossFade())
//                        .loadFromMediaStore(item.mediaStoreRealUri)
//                        .override(displayWidth, displayHeight)
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .crossFade()
                        .into(imgObj);
            }
        }
    }

    @Parcel
    public static class Item {
        public String pubDate;
        public String title;
        public int height;
        public String link;
        public int width;
        public String thumbnailUrl;
        public Uri mediaStoreRealUri;
    }
}
