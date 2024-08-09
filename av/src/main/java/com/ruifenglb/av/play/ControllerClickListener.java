package com.ruifenglb.av.play;

import android.view.View;

public interface ControllerClickListener {
    void onClick(View view);
    void onPlayProgress(int duration, int position);
    void onSendDanmu(String content,String textcolor);
    void onLongPress();
    void onSingleTapUp();
    boolean isLogin();
}
