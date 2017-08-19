package com.openthos.filem.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.openthos.filem.MainActivity;

public class LocalCache {
    private static LocalCache localCache;
    private static SharedPreferences sPreferences;

    private LocalCache(Context context) {
        sPreferences = context.getSharedPreferences(context.getPackageName(),
                Context.MODE_PRIVATE);
    }

    //单例模式
    public static LocalCache getInstance(Context context) {
        if (localCache == null) {
            localCache = new LocalCache(context);
        }
        return localCache;
    }

    public static void setViewTag(String tag){
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(MainActivity.KEY_VIEW_TAG, tag);
        /**
         *  对比ｃommit
         *  apply: １．提交到内存，然后提交到硬件磁盘
         *  　　　　　２．多个并发操作时，apply效率高．．
         *  。 　　　 3. apply没有返回值．错误没有提示．
        　*/
        editor.apply();
    }

    public static String getViewTag(){
        return  sPreferences.getString(MainActivity.KEY_VIEW_TAG, null);
    }

    public static void setSearchText(String query){
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString("searchText", query);
        editor.apply();
    }

    public static String getSearchText(){
        return  sPreferences.getString("searchText", null);
    }
}
