package com.ruifenglb.www.ads;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anythink.nativead.api.ATNativeAdView;
import com.app.ad.biddingsdk.AdUtils;
import com.ruifenglb.www.R;
import com.ruifenglb.www.ad.AdWebView;
import com.ruifenglb.www.bean.StartBean;
import me.drakeet.multitype.ItemViewBinder;

public class AdViewBinder extends ItemViewBinder<StartBean.Ad, AdViewBinder.ViewHolder> {

    private ViewHolder viewHolder;

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        viewHolder = new ViewHolder(inflater.inflate(R.layout.item_ads, parent, false));
        return viewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull StartBean.Ad ad) {
        AdUtils.nativeExpressAd((Activity) holder.itemView.getContext(),holder.adWebView);
//        if (ad == null || ad.getStatus() == 0 || ad.getDescription() == null || ad.getDescription().isEmpty()) {
//            holder.adWebView.setVisibility(View.GONE);
//            holder.blankView.setVisibility(View.GONE);
//        } else {
//            holder.adWebView.setVisibility(View.VISIBLE);
//            holder.blankView.setVisibility(View.VISIBLE);
//            holder.adWebView.loadHtmlBody(ad.getDescription());
//        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private @NonNull
        final LinearLayout adWebView;
        //final View blankView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            adWebView = itemView.findViewById(R.id.adWebView);
          // blankView = itemView.findViewById(R.id.blank_view);
        }
    }

}
