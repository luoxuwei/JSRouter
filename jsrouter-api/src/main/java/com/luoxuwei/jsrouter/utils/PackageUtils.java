package com.luoxuwei.jsrouter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * Created by 罗旭维 on 2021/8/13.
 */
public class PackageUtils {
    private static String NEW_VERSION_NAME;
    private static int NEW_VERSION_CODE;
    public static boolean isNewVersion(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
        } catch (Exception ex) {
            Logger.error("Get package info error.");
        }

        if (packageInfo != null) {
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
            SharedPreferences sp = context.getSharedPreferences(Consts.JSOUTER_SP_CACHE_KEY, Context.MODE_PRIVATE);
            if (!versionName.equals(sp.getString(Consts.LAST_VERSION_NAME, null)) || versionCode != sp.getInt(Consts.LAST_VERSION_CODE, -1)) {
                NEW_VERSION_NAME = versionName;
                NEW_VERSION_CODE = versionCode;
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static void updateVersion(Context context) {
        if (!TextUtils.isEmpty(NEW_VERSION_NAME) && NEW_VERSION_CODE != -1) {
            SharedPreferences sp = context.getSharedPreferences(Consts.JSOUTER_SP_CACHE_KEY, Context.MODE_PRIVATE);
            sp.edit().putString(Consts.LAST_VERSION_NAME, NEW_VERSION_NAME).putInt(Consts.LAST_VERSION_CODE, NEW_VERSION_CODE).apply();
        }
    }
}
