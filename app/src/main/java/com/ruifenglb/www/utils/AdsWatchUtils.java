package com.ruifenglb.www.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ruifenglb.www.download.SPUtils;

public class AdsWatchUtils {
    public static String DJ_NUM = "dj_num";
    public static String TV_NUM = "tv_num";
    /**
     * 电视剧激励逻辑
     * @param context
     * @return
     */
    public static boolean needShowAds(Context context){
        int tvNum = SPUtils.getInt(context, TV_NUM, 0);
        if (tvNum>=5){
            return true;
        }
        return false;
    }
    /**
     * 短剧看激励视频逻辑
     */
    public static boolean needShowDJAds(Context context){
        int djNum = SPUtils.getInt(context, DJ_NUM, 0);
        if (djNum < 10) {
            return false;
        } else if (djNum == 10) {
            return true;
        } else {
            return (djNum - 10) % 5 == 0;
        }
    }
}
