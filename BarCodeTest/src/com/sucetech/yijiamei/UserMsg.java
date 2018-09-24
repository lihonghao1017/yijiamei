package com.sucetech.yijiamei;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lihh on 2018/9/20.
 */

public class UserMsg {
    private static SharedPreferences sp;
    private static final int POSITION_ERROR = 1000;// 定位经纬度偏移误差允许范围

    public static void initUserMsg(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences("UserMsg", Context.MODE_PRIVATE);
        }
    }

    public static void saveToken(String  Token) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("Token", Token);
        edit.commit();
    }

    public static String getToken() {
        return sp.getString("Token", "");
    }


    public static void saveMapZoomLevel(int size) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("ZoomLevel", size);
        edit.commit();
    }

    public static int getMapZoomLevel() {
        return sp.getInt("ZoomLevel", -1);
    }
}
