package com.hhd2002.androidbaselib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HhdDateTimeUtils {
    private static final long MILLISECONS_PER_DAY = 24 * 60 * 60 * 1000;
    private static final long MAX_FILE_EXPIRE_TIME = 20 * MILLISECONS_PER_DAY;

    private static DateFormat mediumDateFormat;
    private static Locale mediumDateFormatLocale;



    public static long getTimestampFromyyyyMMdd(String yyyymmddDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            return dateFormat.parse(yyyymmddDateString).getTime();
        } catch (ParseException e) {
            Log.i("hhddebug", "HhdUtils.getTimestampFromYYYYMMDD failed to parse date");
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
            Log.i("hhddebug", "HhdUtils.generateAttachCalendarViewDateTime e : " + e);
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

    public String getPackageName(Context context) {
        String packageName = context.getPackageName();
        return packageName;
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

}
