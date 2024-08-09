package com.ruifenglb.www.ui.home;

import android.os.Parcelable;

import com.ruifenglb.www.bean.TypeBean;

public interface Vod extends Parcelable {

    String getVodName();

    String getVodBlurb();

    String getVodLang();



    String getVodRemarks();

    String getVodPic();

    String getVodPicThumb();

    String getVodPicSlide();

    String getVod_score();

    String getVod_custom_tag();

    int getVod_hits();

    String getVod_score_num();

    int getVod_points_play();

    int getVod_copyright();

    String getVod_jumpurl();

    TypeBean getType();

}
