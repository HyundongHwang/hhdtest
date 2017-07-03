package com.hhd2002.hhdtest.GlideTest;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.EditText;

import com.hhd2002.androidbaselib.HhdAsyncTask;
import com.hhd2002.androidbaselib.IHhdSampleActivity;
import com.hhd2002.androidbaselib.adapters.HhdRecyclerViewAdapter;
import com.hhd2002.androidbaselib.adapters.HhdRecyclerViewHolder;
import com.hhd2002.androidbaselib.funcdelegate.IHhdFuncDelegateIn;
import com.hhd2002.hhdtest.GlideTest.apis.IDaumApis;
import com.hhd2002.hhdtest.GlideTest.models.GlideTestImage;
import com.hhd2002.hhdtest.R;

import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;


public class GlideTestActivity
        extends AppCompatActivity
        implements IHhdSampleActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000;
    private HhdRecyclerViewAdapter _adapter;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_glide_test);
        _Request_READ_EXT_PER();
        RecyclerView rvObj = (RecyclerView) findViewById(R.id.rv_obj);
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvObj.setLayoutManager(lm);


        ArrayList<Class> itemTypeList = new ArrayList<>();
        itemTypeList.add(GlideTestImage.class);

        ArrayList<Class<? extends HhdRecyclerViewHolder>> vhTypeList = new ArrayList<>();
        vhTypeList.add(GlideTestVh.class);

        _adapter = new HhdRecyclerViewAdapter(
                this,
                itemTypeList,
                vhTypeList,
                new IHhdFuncDelegateIn<GlideTestImage>() {
                    @Override
                    public void execute(GlideTestImage glideTestImage) {
                        startActivity(GlideTestDetailActivity.newIntent(GlideTestActivity.this, glideTestImage));
                    }
                },
                null,
                null);

        rvObj.setAdapter(_adapter);

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
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
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
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void onClickLocal() {
        _adapter.items.clear();

        new HhdAsyncTask() {

            @SuppressWarnings("unused")
            @Override
            protected void doInBackground() {
                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
                int cursorRowCount = cur.getCount();


                if (cursorRowCount == 0)
                    return;

                for (int row = 0; row < 10; row++) {

                    Cursor curThumb = null;

                    try {
                        cur.moveToPosition(row);
                        int colCount = cur.getColumnCount();

                        for (int col = 0; col < colCount; col++) {
                            String columnName = cur.getColumnName(col);
                            String value = cur.getString(col);
//                            HhdLog.d("col : %d, columnName : %s, value : %s", col, columnName, value);
                        }

                        GlideTestImage newImage = new GlideTestImage();
                        newImage.sourceType = GlideTestImage.SourceTypes.Local;
                        String id = cur.getString(cur.getColumnIndex(MediaStore.MediaColumns._ID));
                        newImage.realUri = cur.getString(cur.getColumnIndex(MediaStore.MediaColumns.DATA));
                        String widthStr = cur.getString(cur.getColumnIndex(MediaStore.MediaColumns.WIDTH));
                        newImage.width = Integer.parseInt(widthStr);
                        String heightStr = cur.getString(cur.getColumnIndex(MediaStore.MediaColumns.HEIGHT));
                        newImage.height = Integer.parseInt(heightStr);
                        _adapter.items.add(0, newImage);

                        curThumb = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                                new String[]{MediaStore.Images.Thumbnails.DATA},
                                MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                                new String[]{id},
                                null);

                        curThumb.moveToFirst();
                        newImage.thumbnailUri = curThumb.getString(curThumb.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                    } catch (Exception e) {
                        Log.i("hhddebug", "e : " + e);
                    } finally {
                        if (curThumb != null) {
                            curThumb.close();
                        }
                    }
                }

                cur.close();
            }

            @Override
            protected void onPostExecute() {
                super.onPostExecute();
                _adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    private void onClickNaver() {

        new HhdAsyncTask() {
            @Override
            protected void doInBackground() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    String searchStr = URLEncoder.encode(etSearch.getText().toString(), "UTF-8");
                    String url = "http://openapi.naver.com/search?key=c1b406b32dbbbbeee5f2a36ddc14067f&query=" + searchStr + "&target=image&start=1&display=10";

                    Request req = new Request
                            .Builder()
                            .url(url)
                            .build();

                    Response res = client.newCall(req).execute();

                    //noinspection unused
                    String resStr = res.body().string();

//                    XStream xStream = new XStream();
//                    xStream.alias("rss", NaverModels.Rss.class);
//                    xStream.alias("channel", NaverModels.Channel.class);
//                    xStream.alias("item", NaverModels.Item.class);
//
//                    resStr = resStr.replace("</display>", "</display><items>");
//                    resStr = resStr.replace("</channel>", "</items></channel>");
//
//                    NaverModels.Rss rss = (NaverModels.Rss) xStream.fromXML(resStr);
//                    adapter.items.clear();
//
//                    for (NaverModels.Item item : rss.channel.items) {
//                        Item newItem = new Item();
//                        adapter.items.add(newItem);
//                        newItem.thumbnailUrl = item.thumbnail;
//                        newItem.title = item.title;
//                        newItem.link = item.link;
//                        newItem.width = item.sizewidth;
//                        newItem.height = item.sizeheight;
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onPostExecute() {
                super.onPostExecute();
                _adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    private void onClickDaum() {

        new HhdAsyncTask() {
            @Override
            protected void doInBackground() {
                try {
                    IDaumApis apis = IDaumApis.create();
                    Call<IDaumApis.SearchImageResponse> call = apis.GetSearchImage(GlideTestActivity.this.etSearch.getText().toString(), 20, 1);
                    retrofit2.Response<IDaumApis.SearchImageResponse> res = call.execute();
                    _adapter.items.clear();

                    for (IDaumApis.SearchImageResponse.Item item : res.body().channel.item) {
                        GlideTestImage newImage = new GlideTestImage();
                        newImage.sourceType = GlideTestImage.SourceTypes.Web;
                        _adapter.items.add(newImage);
                        newImage.thumbnailUri = item.thumbnail;
                        newImage.realUri = item.image;
                        newImage.width = item.width;
                        newImage.height = item.height;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onPostExecute() {
                super.onPostExecute();
                _adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public String getSampleDesc() {
        return "Glide, RecyclerView, Retrofit, XML, XStream, AsyncExecutor, Json, Naver OpenAPI, Daum OpenAPI";
    }
}
