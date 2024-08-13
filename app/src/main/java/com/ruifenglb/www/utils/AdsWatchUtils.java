package com.ruifenglb.www.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ruifenglb.www.download.SPUtils;
import com.ruifenglb.www.ui.widget.AdsMindDialog;
import com.ruifenglb.www.ui.widget.AppUpdateDialog;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class AdsWatchUtils {
    /**
     * 电视剧激励逻辑
     * @return
     */
    public static boolean needShowAds(int position){
            return (position+1) % 6 == 0;
    }
    /**
     * 短剧看激励视频逻辑
     */
    public static boolean needShowDJAds(int position){
        return (position+1) % 10 == 0;
    }
}
