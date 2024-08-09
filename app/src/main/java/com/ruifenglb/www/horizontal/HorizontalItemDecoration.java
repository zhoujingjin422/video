package com.ruifenglb.www.horizontal;

/**
 * @author : yjz
 * @date : 2020/10/29 20:06
 * @des :
 */

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 定义水平方向的距离
 */
public class HorizontalItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public HorizontalItemDecoration(int space, Context mContext) {
        this.space = dip2px(space, mContext);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int totalCount = parent.getAdapter().getItemCount();
        if (position == 0) {//第一个
            outRect.left = 0;
            outRect.right = space / 2;
        } else if (position == totalCount - 1) {//最后一个
            outRect.left = space / 2;
            outRect.right = 0;
        } else {//中间其它的
            outRect.left = space / 2;
            outRect.right = space / 2;
        }
    }

    public int dip2px(float dpValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
