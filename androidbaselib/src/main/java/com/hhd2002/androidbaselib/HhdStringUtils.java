package com.hhd2002.androidbaselib;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HhdStringUtils {

    private static DecimalFormat df;

    private HhdStringUtils() {

    }

    public static boolean isStringNullOrEmpty(String str) {
        boolean result = (str == null || str.equals(""));
        return result;
    }
    
    public static int parseHex(String hexString) {
        int value = convertHexToDec(hexString.charAt(0));
        for (int i = 1; i < hexString.length(); i++) {
            value = value * 16 + convertHexToDec(hexString.charAt(i));
        }
        return value;
    }

    public static int parseInt(String value, int def) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return def;
        }
    }


    public static int convertHexToDec(char ch) {
        if (ch == 'a' | ch == 'A') {
            return 10;
        } else if (ch == 'b' | ch == 'B') {
            return 11;
        } else if (ch == 'c' | ch == 'C') {
            return 12;
        } else if (ch == 'd' | ch == 'D') {
            return 13;
        } else if (ch == 'e' | ch == 'E') {
            return 14;
        } else if (ch == 'f' | ch == 'F') {
            return 15;
        } else if (ch <= '9' && ch >= '0') {
            return ch - '0';
        } else
            throw new NumberFormatException("Wrong character: " + ch);
    }

    public static int getBytesLength(String source, String encoding) {
        if (source == null) return 0;
        if (Charset.isSupported(encoding)) {
            try {
                return source.getBytes(encoding).length;
            } catch (UnsupportedEncodingException e) {
            }
        }

        return source.getBytes().length;
    }

    public static String toHexString(byte[] block) {
        if (block == null) return null;
        StringBuffer buf = new StringBuffer();
        char[] hexChars = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        int len = block.length;
        int high = 0;
        int low = 0;
        for (int i = 0; i < len; i++) {
            high = ((block[i] & 0xf0) >> 4);
            low = (block[i] & 0x0f);
            buf.append(hexChars[high]);
            buf.append(hexChars[low]);
        }
        return buf.toString();
    }

    public static int getKoreanSMSBytesLength(String source) {
        if (source == null) return 0;
        return getBytesLength(source, "EUC-KR");
    }

    public static String truncate(CharSequence source, int length, String tail) {
        if (source == null)
            return null;

        if (source.length() < length) {
            length = source.length();
            tail = "";
        }
        return source.subSequence(0, length) + tail;
    }

    public static CharSequence truncateWhenUTF8(CharSequence s, int maxBytes, String tail) {
        if (s == null || tail == null) return null;
        int b = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            // ranges from http://en.wikipedia.org/wiki/UTF-8
            int skip = 0;
            int more;
            if (c <= 0x007f) {
                more = 1;
            } else if (c <= 0x07FF) {
                more = 2;
            } else if (c <= 0xd7ff) {
                //more = 3;
                more = 2;
            } else if (c <= 0xDFFF) {
                // surrogate area, consume next char as well
                more = 4;
                skip = 1;
            } else {
                more = 3;
            }

            if (b + more > maxBytes) {
                return s.subSequence(0, i) + tail;
            }
            b += more;
            i += skip;
        }
        return s;
    }

    public static boolean isNull(String str) {
        return (str == null || str.length() == 0);
    }


    public static boolean checkJapanEmailFormat(String email) {
        if (checkEmailFormat(email)) {
            String lower = email.toLowerCase(Locale.US);
            return lower.matches("^\\S+.jp");
        }
        return false;
    }

    public static boolean checkEmailFormat(String emailFormedString) {
        return emailFormedString.matches("^\\S+@\\S+$");
    }

    public static String makeFirstCharToUpperCase(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(s.substring(0, 1).toUpperCase(Locale.US));
        sb.append(s.substring(1));
        return sb.toString();
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    private static final float KBYTES = 1024f;
    private static final float MBYTES = KBYTES * KBYTES;
    private static final float GBYTES = MBYTES * KBYTES;

    public static String displayBytesSize(final long n) {
        final String size;
        final long abs = Math.abs(n);
        if (abs < KBYTES) {
            size = n + " bytes";
        } else if (abs < MBYTES) {
            size = String.format("%.2f", n / KBYTES) + " kB";
        } else if (abs < GBYTES) {
            size = String.format("%.2f", n / MBYTES) + " MB";
        } else {
            return String.format("%.2f", n / GBYTES) + " GB";
        }
        return size;
    }

    /**
     * Ellipsizes a number with terminator '+'
     *
     * @param number  an integer value to ellipsize
     * @param maximum an integer value indicates the maximum (inclusive)
     * @return an ellipsized number string
     */
    public static String ellipsizeNumber(int number, int maximum) {
        if (number <= maximum) {
            return String.valueOf(number);
        }

        return String.valueOf(maximum) + "+";
    }

    public static int getVersionIntFromString(String versionName) {
        int versionInt = 0;
        final String[] splited = versionName.split("\\.");
        for (String versionItem : splited) {
            versionInt = versionInt * 100 + Integer.valueOf(versionItem);
        }
        return versionInt;
    }

    public static boolean isUpToDate(String currentVer, String recentVer) {
        int recent = 0;
        int current = 0;
        try {
            if (recentVer == null) {
                return true;
            }
            recent = getVersionIntFromString(recentVer);
            current = getVersionIntFromString(currentVer);
        } catch (Exception e) {
            return false;
        }

        return current < recent;
    }

    private static final int UNICODE_CODEPOINT_EMOJI_START = 0x1F300;
    private static final int UNICODE_CODEPOINT_EMOJI_END = 0x1F6FF;
    private static final int UNICODE_CODEPOINT_REGIONAL_INDICATOR_START = 0x1F1E6;
    private static final int UNICODE_CODEPOINT_REGIONAL_INDICATOR_END = 0x1F1FF;

    public static boolean isStringContainsEmoji(String text) {
        for (int i = 0; i < text.length(); i++) {
            int codePoint = text.codePointAt(i);
            if ((codePoint >= UNICODE_CODEPOINT_EMOJI_START && codePoint <= UNICODE_CODEPOINT_EMOJI_END) ||
                    (codePoint >= UNICODE_CODEPOINT_REGIONAL_INDICATOR_START && codePoint <= UNICODE_CODEPOINT_REGIONAL_INDICATOR_END)) {
                return true;
            }
        }
        return false;
    }

    public static boolean equals(String lhs, String rhs) {
        final boolean emptyLhs = TextUtils.isEmpty(lhs);
        final boolean emptyRhs = TextUtils.isEmpty(rhs);
        if (emptyLhs && emptyRhs) {
            return true;
        }
        if (emptyLhs == !emptyRhs) {
            return false;
        }
        return lhs.equals(rhs);
    }

    public static int findNullTerminator(byte[] bytes) {
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] == 0) {
                return i;
            }
        }
        return -1;
    }

    public static String safelyURLEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public static String getAmountFormatString(int value) {
        if (df == null) {
            df = new DecimalFormat("#,###");
        }
        return df.format(value);
    }

    public static int getAmountFormatInt(String strDecimal) {
        if (df == null) {
            df = new DecimalFormat("#,###");
        }
        try {
            Number n = df.parse(strDecimal);
            return n.intValue();
        } catch (ParseException e) {
            return Integer.MAX_VALUE;
        }
    }

    public static final Pattern WEB_URI_PATTERN = Pattern.compile("(?:https?\\:\\/\\/|www\\.)(?:[-a-z0-9]+\\.)*[-a-z0-9]+[^\\s]*", Pattern.CASE_INSENSITIVE);
    public static final String HTTP_PREFIX = "http://";

    public static String extractScrapUrl(String source, int[] range) {
        if (hasScrapUrl(source) == false) {
            return null;
        }

        final Matcher matcher = WEB_URI_PATTERN.matcher(source);
        matcher.find();

        String extracted = matcher.group();

        if (extracted.startsWith("http") == false) {
            extracted = HTTP_PREFIX + extracted;
        }

        if (range != null && range.length == 2) {
            range[0] = matcher.start();
            range[1] = matcher.end();
        }

        return extracted;
    }


    private static boolean hasScrapUrl(String source) {
        if (TextUtils.isEmpty(source)) {
            return false;
        }

        final Matcher matcher = WEB_URI_PATTERN.matcher(source);
        return matcher.find() && TextUtils.isEmpty(matcher.group()) == false;
    }

    public static String convertBytesToUtf8String(byte[] buf) {
        String str = new String(buf, Charset.forName("utf-8"));
        return str;
    }

    public static byte[] convertUtf8StringToBytes(String str) {
        byte[] tmpBuf = str.getBytes(Charset.forName("utf-8"));
        return tmpBuf;
    }

}

