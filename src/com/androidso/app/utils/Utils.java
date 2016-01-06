package com.androidso.app.utils;

import android.content.res.Resources;
import android.text.TextUtils;

public class Utils {
    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    /**
     * @param urlPath
     * @return 如果urlPath 为空 ,返回一个空串
     */
    public static String getFileName(String urlPath) {

        if (!TextUtils.isEmpty(urlPath)) {
            if (urlPath.indexOf("/") != -1) {
                return urlPath.substring(urlPath.lastIndexOf('/') + 1);
            }
            return urlPath;
        }
        return "";

    }

    public static String getTwoMaxEms(String originStr) {
        if (originStr == null)
            originStr = "";
        if (originStr.length() > 2) {
            return originStr.substring(0, 2);
        } else {
            return originStr;
        }

    }
}