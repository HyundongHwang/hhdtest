package com.hhd2002.androidbaselib;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class HhdFileUtils {
    public static void closeStreamSafely(InputStream is) {
        if (is == null)
            return;

        try {
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeStreamSafely(OutputStream os) {
        if (os == null)
            return;

        try {
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] readAssetsAsBytes(Context context, String fileName) {
        InputStream is = null;

        try {
            AssetManager am = context.getAssets();
            is = am.open(fileName);
            byte[] buf = readInputStreamAsBytes(is);
            return buf;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeStreamSafely(is);
        }
    }

    public static byte[] readRawAsBytes(Context context, int rawId) {
        InputStream is = null;

        try {
            is = context.getResources().openRawResource(rawId);
            byte[] buf = readInputStreamAsBytes(is);
            return buf;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeStreamSafely(is);
        }
    }

    public static String getAbsolutePathInExtFilesDir(Context context, String subFilePath) {
        File extFileDir = context.getExternalFilesDir(null);
        String extFilePath = new File(extFileDir, subFilePath).getAbsolutePath();
        return extFilePath;
    }

    public static byte[] readInputStreamAsBytes(InputStream is) {
        ByteArrayOutputStream baos = null;

        try {
            baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];

            while (true) {
                int readcount = is.read(buf, 0, 1024);

                if (readcount <= 0)
                    break;

                baos.write(buf, 0, readcount);
            }

            byte[] allBuf = baos.toByteArray();
            return allBuf;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeStreamSafely(baos);
        }
    }

    public static void writeToOutputStream(byte[] buf, OutputStream os) {
        ByteArrayInputStream bais = null;

        try {
            bais = new ByteArrayInputStream(buf);
            byte[] bufTmp = new byte[1024];

            while (true) {
                int readcount = bais.read(bufTmp, 0, 1024);

                if (readcount <= 0)
                    break;

                os.write(buf, 0, readcount);
            }

            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStreamSafely(bais);
        }
    }

    public static void writeToOutputStream(String str, OutputStream os) {
        byte[] buf = HhdStringUtils.convertUtf8StringToBytes(str);
        writeToOutputStream(buf, os);
    }

    public static void writeToFile(byte[] buf, String filePath) {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(filePath);
            writeToOutputStream(buf, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStreamSafely(fos);
        }
    }

    public static void writeToFile(String str, String filePath) {
        FileOutputStream fos = null;

        try {
            byte[] buf = HhdStringUtils.convertUtf8StringToBytes(str);
            fos = new FileOutputStream(filePath);
            writeToOutputStream(buf, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStreamSafely(fos);
        }
    }

    public static String readStringFromFile(String filePath) {
        byte[] buf = readBytesFromFile(filePath);
        String str = HhdStringUtils.convertBytesToUtf8String(buf);
        return str;
    }

    public static byte[] readBytesFromFile(String filePath) {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(filePath);
            byte[] buf = readInputStreamAsBytes(fis);
            return buf;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeStreamSafely(fis);
        }
    }

    public static ArrayList<File> getAllExternalFiles() {
        ArrayList<File> files = new ArrayList<File>();
        traverseDir(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), files);
        return files;
    }

    private static void traverseDir(File dir, ArrayList<File> files) {
        File listFile[] = dir.listFiles();

        for (int i = 0; i < listFile.length; i++) {
            if (listFile[i].isDirectory()) {
                files.add(listFile[i]);
                traverseDir(listFile[i], files);
            } else {
                files.add(listFile[i]);
            }
        }
    }
}
