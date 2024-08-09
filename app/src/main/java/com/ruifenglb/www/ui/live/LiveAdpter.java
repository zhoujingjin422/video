package com.ruifenglb.www.ui.live;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.ruifenglb.www.R;
import com.ruifenglb.www.bean.LiveBean;
import com.ruifenglb.www.download.SPUtils;
import com.ruifenglb.www.ui.home.Vod;
import com.ruifenglb.www.ui.login.LoginActivity;
import com.ruifenglb.www.ui.play.PlayActivity;
import com.ruifenglb.www.ui.play.X5WebActivity;
import com.ruifenglb.www.utils.LoginUtils;
import com.ruifenglb.www.utils.UserUtils;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class LiveAdpter extends RecyclerView.Adapter<LiveAdpter.ViewHolder> {
    private List<LiveBean> coll;// 消息对象数组
    private LayoutInflater mInflater;
    private Context context;

    public LiveAdpter(Context context, List<LiveBean> coll) {
        this.coll = coll;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.live_video_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LiveBean entity = coll.get(position);

        MultiTransformation mation = new MultiTransformation<>(new CenterCrop(), new RoundedCornersTransformation(20, 0, RoundedCornersTransformation.CornerType.ALL));
        Glide.with(holder.itemView.getContext())
                .load(entity.getImg())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.bitmapTransform(mation))
                .into(holder.coverImg);
        holder.tvName.setText(entity.getName());
        holder.llTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(LoginUtils.checkLogin(context)){
                        if (entity.getUrl().endsWith("///") || entity.getUrl().endsWith("m3u8")) {
                            Intent intent = new Intent(context, X5WebActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("url", entity.getUrl());
                            bundle.putString("title", "");
                            intent.putExtras(bundle);
                            ActivityUtils.startActivity(intent);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Bundle bundle = new Bundle();
                            intent.setData(Uri.parse(entity.getUrl())); //为Intent设置DATA属性
                            bundle.putString("url", entity.getUrl());
                            bundle.putString("title", entity.getName());
                            intent.putExtras(bundle);
                            ActivityUtils.startActivity(intent);
                        }
                    }

            }
        });

    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return coll.size();
    }

    public void clearData() {
        this.coll.clear();
        notifyDataSetChanged();
    }

    public void refresh(List<LiveBean> data) {
        reset(data);
        notifyDataSetChanged();
    }

    public void reset(List<LiveBean> data) {
        if (!data.isEmpty()) {
            this.coll.clear();
            for (int i = 0; i < data.size(); i++) {
                this.coll.add(data.get(i));
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout llTotal;
        public TextView tvName;
        public ImageView coverImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llTotal = (LinearLayout) itemView.findViewById(R.id.ll_total);
            coverImg = (ImageView) itemView.findViewById(R.id.live_cover);
            tvName = (TextView) itemView.findViewById(R.id.live_name);
        }
    }
}
