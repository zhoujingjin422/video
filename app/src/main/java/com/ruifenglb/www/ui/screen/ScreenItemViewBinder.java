package com.ruifenglb.www.ui.screen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ruifenglb.www.ApiConfig;
import com.ruifenglb.www.R;
import com.ruifenglb.www.base.BaseItemClickListener;
import com.ruifenglb.www.ui.home.Vod;
import com.ruifenglb.www.utils.AppColorUtils;
import com.ruifenglb.www.utils.DensityUtils;
import kotlin.Pair;
import me.drakeet.multitype.ItemViewBinder;

import static org.litepal.LitePalApplication.getContext;

@SuppressWarnings("unused")
public class ScreenItemViewBinder extends ItemViewBinder<Vod, ScreenItemViewBinder.ViewHolder> implements View.OnClickListener {

    private BaseItemClickListener baseItemClickListener;

    public void setBaseItemClickListener(BaseItemClickListener baseItemClickListener) {
        this.baseItemClickListener = baseItemClickListener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_card_child, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Vod item) {
        holder.itemView.setTag(R.id.itemData, item);
        holder.itemView.setOnClickListener(this);

        holder.title.setText(item.getVodName());
//        holder.tip.setText(item.getVod_custom_tag());
//        holder.tip.setBackgroundResource(AppColorUtils.getTagBgResId(item.getVod_custom_tag()));
        //新加积分显示
        if (item.getVod_points_play() == 0) {
            holder.tip.setVisibility(View.GONE);
        } else {
            holder.tip.setVisibility(View.VISIBLE);
            holder.tip.setText(item.getVod_points_play() + "积分");
        }
        //新加积分显示

        holder.up_title.setText(item.getVodRemarks());

        ViewGroup.LayoutParams lp = holder.icon.getLayoutParams();
        int per_width = (DensityUtils.INSTANCE.getScreenWidth(getContext()) - DensityUtils.INSTANCE.dp2px(getContext(), 4)) / 3;
        lp.height = (int) (per_width * 1.4f);
        holder.icon.setLayoutParams(lp);
        Glide.with(holder.itemView.getContext())
                .load(ApiConfig.MOGAI_BASE_URL + item.getVodPic())
                .thumbnail(1.0f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.icon);
    }

    @Override
    public void onClick(View view) {
        Object o = view.getTag(R.id.itemData);
        if (o != null && baseItemClickListener != null) {
            baseItemClickListener.onClickItem(view, o);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private @NonNull
        ImageView icon;
        private @NonNull
        TextView tip;
        private @NonNull
        TextView up_title;
        private @NonNull
        TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.item_iv_card_child_icon);
            tip = itemView.findViewById(R.id.item_tv_card_child_tip);
            up_title = itemView.findViewById(R.id.item_tv_card_child_up_title);
            title = itemView.findViewById(R.id.item_tv_card_child_title);
        }
    }

}
