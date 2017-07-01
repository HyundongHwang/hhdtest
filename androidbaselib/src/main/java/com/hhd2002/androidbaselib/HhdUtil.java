package com.hhd2002.androidbaselib;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hhd2002 on 2014. 8. 27..
 */
public class HhdUtil {

    private static final String ASSET_PATH_SEGMENT = "android_asset";
    private static final String ASSET_PREFIX = ContentResolver.SCHEME_FILE + ":///" + ASSET_PATH_SEGMENT + "/";
    private static final int ASSET_PREFIX_LENGTH = ASSET_PREFIX.length();
    private static final long MILLISECONS_PER_DAY = 24 * 60 * 60 * 1000;
    private static final long MAX_FILE_EXPIRE_TIME = 20 * MILLISECONS_PER_DAY;

    private static Display display;
    private static HashMap<String, SPrefModel> sPrefCache = new HashMap<>();
    private static DateFormat mediumDateFormat;
    private static Locale mediumDateFormatLocale;


    public static ArrayList<File> getAllExternalFiles() {
        ArrayList<File> files = new ArrayList<File>();
        return traverseDir(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), files);
    }

    public static boolean isStringNullOrEmpty(String str) {
        boolean result = (str == null || str.equals(""));
        return result;
    }

    public static float dip2Pixel(Context context, float dip) {
        Resources res = context.getResources();
        float pixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, res.getDisplayMetrics());
        return pixel;
    }

    @SuppressWarnings("deprecation")
    public static int getDisplayWidth(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            return getDisplayWidthPostHoneycombMr2(context);
        } else {
            return getDisplay(context).getWidth();
        }
    }

    public static Display getDisplay(Context context) {
        if (display == null) {
            synchronized (HhdUtil.class) {
                if (display != null)
                    return display;
                display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            }
        }
        return display;
    }

    public static void putSPrefString(Context context, String key, String value) {
        SPrefModel model = getsPrefModel(context, key);
        model.putString(value);
    }

    public static void putSPrefInt(Context context, String key, int value) {
        SPrefModel model = getsPrefModel(context, key);
        model.putInt(value);
    }

    public static void putSPrefBoolean(Context context, String key, boolean value) {
        SPrefModel model = getsPrefModel(context, key);
        model.putBoolean(value);
    }

    public static String getSPrefString(String key) {
        SPrefModel model = sPrefCache.get(key);

        if (model == null)
            return null;

        return (String) model.cachedValue;
    }

    public static int getSPrefInt(String key) {
        SPrefModel model = sPrefCache.get(key);

        if (model == null)
            return 0;

        return (int) model.cachedValue;
    }

    public static boolean getSPrefBoolean(String key) {
        SPrefModel model = sPrefCache.get(key);

        if (model == null)
            return false;

        return (boolean) model.cachedValue;
    }

    public static boolean isAssetUri(Uri uri) {
        return ContentResolver.SCHEME_FILE.equals(uri.getScheme()) && !uri.getPathSegments().isEmpty()
                && ASSET_PATH_SEGMENT.equals(uri.getPathSegments().get(0));
    }

    public static String toAssetPath(Uri uri) {
        return uri.toString().substring(ASSET_PREFIX_LENGTH);
    }

    public static Uri assetPathToUri(String path) {
        final String assetPath = path.startsWith("/") ? path.substring(1) : path;
        return Uri.parse(ASSET_PREFIX + path);
    }

    public static long getTimestampFromyyyyMMdd(String yyyymmddDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            return dateFormat.parse(yyyymmddDateString).getTime();
        } catch (ParseException e) {
            Log.i("hhddebug", "HhdUtil.getTimestampFromYYYYMMDD failed to parse date");
            return new Date().getTime();
        }
    }

    public static long getTimestampFromRfc3339(String rfc3339DateString) {
        Time time = new Time();
        time.parse3339(rfc3339DateString);
        return time.toMillis(false);
    }

    public static long getFileRemainedExpireTime(String dateStr) {
        long timeStamp = getTimestampFromRfc3339(dateStr);
        long curTime = System.currentTimeMillis();
        return timeStamp + MAX_FILE_EXPIRE_TIME - curTime - 1000L;
    }

    public static String getyyyyMMddString(String rfc3339DateString) {
        Time time = new Time();
        time.parse3339(rfc3339DateString);
        time.switchTimezone(TimeZone.getDefault().getID());
        return time.format("%Y%m%d");
    }

    public static String getFormattedLocalDate(String rfc3339DateString) {
        return getFormattedLocalDate(getTimestampFromRfc3339(rfc3339DateString));
    }

    public static String getFormattedLocalDate(long timestamp) {
        if (mediumDateFormat == null || Locale.getDefault() != mediumDateFormatLocale) {
            mediumDateFormatLocale = Locale.getDefault();
            mediumDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, mediumDateFormatLocale);
        }

        return mediumDateFormat.format(new Date(timestamp));
    }

    public static long currentTimeMillis() {
        return System.nanoTime() / 1000000;
    }

    public static String generateAttachCalendarViewDateTime(String displayStartAt, String displayEndAt, boolean isAllDay) {
        boolean isSameDay = getSameDay(displayStartAt, displayEndAt);
        boolean isSameYear = getSameYear(displayStartAt, displayEndAt);
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.M.d (EEE)");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy.M.d (EEE) a h:mm");
        SimpleDateFormat monthFormat = new SimpleDateFormat("M.d (EEE)");

        Date displayStartDate;
        Date displayEndDate;

        try {
            displayStartDate = df.parse(displayStartAt);
            displayEndDate = df.parse(displayEndAt);
        } catch (Throwable e) {
            Log.i("hhddebug", "HhdUtil.generateAttachCalendarViewDateTime e : " + e);
            return null;
        }

        String startDateString;
        String endDateString = null;

        if (isAllDay && isSameDay) {
            startDateString = dateFormat.format(displayStartDate);
        } else if (isAllDay && !isSameDay) {
            startDateString = dateFormat.format(displayStartDate);
            if (isSameYear) {
                endDateString = monthFormat.format(displayEndDate);
            } else {
                endDateString = dateFormat.format(displayEndDate);
            }
        } else if (!isAllDay && isSameDay) {
            startDateString = dateTimeFormat.format(displayStartDate);
        } else {
            startDateString = dateFormat.format(displayStartDate);
            if (isSameYear) {
                endDateString = monthFormat.format(displayEndDate);
            } else {
                endDateString = dateFormat.format(displayEndDate);
            }
        }

        return startDateString + (TextUtils.isEmpty(endDateString) ? "" : " ~ " + endDateString);
    }


    private void createNoMedia(File dir) {
        if (dir.exists()) {
            File noMedia = new File(dir, ".nomedia");
            if (!noMedia.exists()) {
                try {
                    noMedia.createNewFile();
                } catch (Throwable e) {
                }
            }
        }
    }


    public File getExternalStorageDataDir(Context context) {
        File dir = new File(String.format("%s/Android/data/%s", getExternalStorageDirectory(), getPackageName(context)));
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // arnold test code - since API level 9
        // Logger.d(String.format(">>> isExternalStorageRemovable : %d", Environment.isExternalStorageRemovable()));

        return dir;
    }

    public String getPackageName(Context context) {
        String packageName = context.getPackageName();
        return packageName;
    }


    public File getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory();
    }


    private static boolean getSameDay(String displayStartAt, String displayEndAt) {
        String startDate = displayStartAt.substring(0, 8);
        String endDate = displayEndAt.substring(0, 8);
        return startDate.equals(endDate);
    }

    private static boolean getSameYear(String displayStartAt, String displayEndAt) {
        String startYear = displayStartAt.substring(0, 4);
        String endYear = displayEndAt.substring(0, 4);
        return startYear.equals(endYear);
    }


    private static ArrayList<File> traverseDir(File dir, ArrayList<File> files) {
        File listFile[] = dir.listFiles();

        for (int i = 0; i < listFile.length; i++) {
            if (listFile[i].isDirectory()) {
                files.add(listFile[i]);
                traverseDir(listFile[i], files);
            } else {
                files.add(listFile[i]);
            }
        }

        return files;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private static int getDisplayWidthPostHoneycombMr2(Context context) {
        Point size = new Point();
        getDisplay(context).getSize(size);
        return size.x;
    }

    private static SPrefModel getsPrefModel(Context context, String key) {
        SPrefModel model = sPrefCache.get(key);

        if (model == null) {
            model = new SPrefModel(context, key);
            sPrefCache.put(key, model);
        }

        return model;
    }


    public static String getCurDateTimeStr() {
        Date now = new Date();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

        String str = sdf.format(now);
        return str;
    }


    public static String getCurDateTimeStrUtc() {
        Date now = new Date();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss UTC");

        sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
        String str = sdf.format(now);
        return str;
    }


    public static Gson getGson() {
        //noinspection UnnecessaryLocalVariable
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson;
    }
    
    
    
    

    private static class SPrefModel {
        private String key;
        private SharedPreferences pref;
        private SharedPreferences.Editor editor;
        private Timer timer;
        private Object cachedValue;

        public SPrefModel(Context context, String key) {
            this.key = key;
            pref = context.getSharedPreferences(key, Context.MODE_PRIVATE);
            editor = pref.edit();
            timer = new Timer();
        }

        public void putString(String value) {
            if (value.equals(cachedValue))
                return;

            editor.putString(key, value);
            cachedValue = value;
            commitDelayed();
        }

        public void putInt(int value) {
            if (cachedValue != null && value == (int) cachedValue)
                return;

            editor.putInt(key, value);
            cachedValue = value;
            commitDelayed();
        }

        public void putBoolean(boolean value) {
            if (cachedValue != null && value == (boolean) cachedValue)
                return;

            editor.putBoolean(key, value);
            cachedValue = value;
            commitDelayed();
        }

        public String getString() {
            if (!isStringNullOrEmpty((String) cachedValue))
                return (String) cachedValue;

            cachedValue = pref.getString(key, "");
            return (String) cachedValue;
        }

        public int getInt() {
            if (cachedValue != null)
                return (int) cachedValue;

            cachedValue = pref.getInt(key, 0);
            return (int) cachedValue;
        }

        public boolean getBoolen() {
            if (cachedValue != null)
                return (boolean) cachedValue;

            cachedValue = pref.getBoolean(key, false);
            return (boolean) cachedValue;
        }

        public void remove() {
            editor.remove(key);
            cachedValue = null;
            commitDelayed();
        }

        private void commitDelayed() {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    editor.commit();
                }
            }, 500);
        }

        public static Uri toImageMediaStoreUri(long id) {
            return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        }

        public static Uri toVideoMediaStoreUri(long id) {
            return ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
        }
    }
}
