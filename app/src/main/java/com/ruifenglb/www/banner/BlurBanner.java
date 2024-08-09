package com.ruifenglb.www.banner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ad.biddingsdk.AdUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ruifenglb.www.bean.VodBean;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.listener.OnPageChangeListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.ruifenglb.www.R;

@SuppressWarnings("unused")
public class BlurBanner<T extends BannerData> extends Banner {

    private onBannerActionListener onBannerActionListener;
    ImageAdapter imageAdapter;


    public void setDataList(List<T> dataList) {
        List<BannerData> list = new ArrayList<>();
        if (dataList != null) {
            for (BannerData data : dataList) {
                list.add(data);
            }
        }
        imageAdapter.setDatas(list);
    }

    public void upDataList(List<T> dataList) {
        flag_is_first_bitmap = true;
        List<BannerData> list = new ArrayList<>();
        if (dataList != null) {
            for (BannerData data : dataList) {
                list.add(data);
            }
        }
        this.imageAdapter.setDatas(list);
    }

    public void setOnBannerActionListener(BlurBanner.onBannerActionListener onBannerActionListener) {
        this.onBannerActionListener = onBannerActionListener;
    }


    public BlurBanner(Context context) {
        this(context, null);
    }

    public BlurBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private static Context mContext;
    public BlurBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    private void init() {
        setBannerGalleryEffect(0, 0, 0);
//        setBannerGalleryMZ(40);
        imageAdapter = new ImageAdapter(null, this);
        setAdapter(imageAdapter);
        setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(Object data, int position) {
                if (onBannerActionListener != null)
                    onBannerActionListener.onBannerClick(position, data);
            }
        });
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (onBannerActionListener != null) {
                    onBannerActionListener.onPageChange(position, blurBitmaps.get(imageAdapter.getData(position).getBannerImg()));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            //LogUtils.a(":) 开始自动播放了！");
            start();
        } else {
            //LogUtils.a(":) 停止自动播放了！");
            stop();
        }
    }

    private boolean flag_is_first_bitmap = true;
    private LinkedHashMap<String, Bitmap> blurBitmaps = new LinkedHashMap<>();

    public void getBitmap(Bitmap bitmap, String key) {
        if (blurBitmaps.get(key) == null) {
            //bitmap = ImageUtils.compressBySampleSize(bitmap, 15);
            bitmap = ImageUtils.fastBlur(bitmap, 0.3f, 15);
            blurBitmaps.put(key, bitmap);
        }
        //第一次加载好图片的时候立刻调用一次
        if (flag_is_first_bitmap) {
            if (onBannerActionListener != null)
                onBannerActionListener.onPageChange(-1, bitmap);
            flag_is_first_bitmap = false;
        }
    }


    public interface onBannerActionListener {

        void onPageChange(int position, Bitmap bitmap);

        void onBannerClick(int position, Object o);
    }

    public static <T extends BannerData> List<String> getTextListByList(List<T> list) {
        if (list == null) return null;
        List<String> stringList = new ArrayList<>();
        for (BannerData bannerData : list) {
            stringList.add(bannerData.getBannerName());
        }
        return stringList;
    }


    /**
     * 自定义布局，下面是常见的图片样式，更多实现可以看demo，可以自己随意发挥
     */
    public static class ImageAdapter extends BannerAdapter<BannerData, ImageAdapter.BannerViewHolder> {
        BlurBanner banner;


        public ImageAdapter(List<BannerData> mDatas, BlurBanner banner) {
            //设置数据，也可以调用banner提供的方法,或者自己在adapter中实现
            super(mDatas);
            this.banner = banner;
        }

        //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
        @Override
        public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_home_banner, parent, false);
            return new BannerViewHolder(view);
        }

        @Override
        public void onBindView(BannerViewHolder holder, BannerData data, int position, int size) {
            //图片加载自己实现
            if (data instanceof VodBean){
                int typeId = ((VodBean) data).getType_id();
                if (typeId==999){
                    holder.fl_add.setVisibility(View.VISIBLE);
//                    AdUtils.bannerAd((Activity) holder.imageView.getContext(),holder.fl_add);
                    return;
                }
            }
            holder.fl_add.setVisibility(View.GONE);
            Log.e("BlurBanner.class", "Banner:"+data.getBannerImg());
            Glide.with(holder.itemView)
                    .asBitmap()
                    .load(data.getBannerImg())
//                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {


                            /**
                             * 修改bug
                             * 处理图片地址异常加载不出来崩溃问题
                             * 时间:2021年06月24日 19:57
                             */
                            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.shape_bg_orange_to_light_top);
                            if (banner != null)
                                banner.getBitmap(bitmap, String.valueOf(model));

                     //       if (banner != null)
                     //           banner.getBitmap(ImageUtils.getBitmap(R.drawable.shape_bg_orange_to_light_top), String.valueOf(model));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            if (banner != null)
                                banner.getBitmap(resource, String.valueOf(model));
                            return false;
                        }

                    })
                    .into(holder.imageView);
            holder.textView.setText(data.getBannerName());
        }

        class BannerViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textView;
            FrameLayout fl_add;

            public BannerViewHolder(View view) {
                super(view);
                this.imageView = view.findViewById(R.id.imageview);
                textView = view.findViewById(R.id.title);
                fl_add = view.findViewById(R.id.fl_add);
            }
        }
    }

}
