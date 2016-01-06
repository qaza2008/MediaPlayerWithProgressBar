package com.androidso.app.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public final class StorageUtils {
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String INDIVIDUAL_DIR_NAME = "uil-images";

    private StorageUtils() {
    }

    public static File getCacheDirectory(Context context) {
        return getCacheDirectory(context, true);
    }

    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;

        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException var5) {
            externalStorageState = "";
        }

        if (preferExternal && "mounted".equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }

        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }

        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            Log.w("Can\'t define system cache directory! \'%s\' will be used.", new Object[]{cacheDirPath}.toString());
            appCacheDir = new File(cacheDirPath);
        }

        return appCacheDir;
    }

    public static File getIndividualCacheDirectory(Context context) {
        File cacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(cacheDir, "uil-images");
        if (!individualCacheDir.exists() && !individualCacheDir.mkdir()) {
            individualCacheDir = cacheDir;
        }

        return individualCacheDir;
    }

    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if ("mounted".equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }

        if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
            appCacheDir = context.getCacheDir();
        }

        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                Log.w("Unable to create external cache directory", new Object[0].toString());
                return null;
            }

            try {
                (new File(appCacheDir, ".nomedia")).createNewFile();
            } catch (IOException var4) {
                Log.i("Can\'t create \".nomedia\" file in application external cache directory", new Object[0].toString());
            }
        }

        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        return perm == 0;
    }
}
