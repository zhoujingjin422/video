package com.app.ad.biddingsdk;


import static com.app.ad.biddingsdk.AdConfig.AppID;
import static com.app.ad.biddingsdk.AdConfig.TakuAppKey;
import static com.app.ad.biddingsdk.SelfRenderViewUtil.bindSelfRenderView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.aliyun.sls.android.producer.utils.Utils;
import com.anythink.banner.api.ATBannerExListener;
import com.anythink.banner.api.ATBannerView;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.ATEventInterface;
import com.anythink.core.api.ATNetworkConfirmInfo;
import com.anythink.core.api.ATSDK;
import com.anythink.core.api.AdError;
import com.anythink.interstitial.a.d;
import com.anythink.interstitial.api.ATInterstitial;
import com.anythink.interstitial.api.ATInterstitialListener;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.ATNativeNetworkListener;
import com.anythink.nativead.api.ATNativePrepareInfo;
import com.anythink.nativead.api.NativeAd;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoListener;
import com.anythink.sdk.R;
import com.anythink.splashad.api.ATSplashAd;
import com.anythink.splashad.api.ATSplashAdExtraInfo;
import com.anythink.splashad.api.ATSplashExListener;

public class AdUtils {

    private  ATSplashAd splashAd;
    private  ATRewardVideoAd mRewardVideoAd;
    private  ATRewardVideoAd mRewardTVVideoAd;
    private  ATNative atNative;
    private  ATNative atBannerNative;
    private  ATInterstitial mInterstitialAd;
    private  ATBannerView mBannerView;
    private static AdUtils adUtils;

    private AdUtils() {
    }

    public static AdUtils getInstance(){
        if (adUtils==null){
            adUtils = new AdUtils();
        }
        return adUtils;
    }
    //初始化
    public  void init(Application app){
//初始化SDK该接口不会采集用户信息
        ATSDK.init(app, AppID, TakuAppKey);
//调用init后请再调用start，否则可能影响广告填充，造成收入下降
        ATSDK.start();
        ATSDK.integrationChecking(app);
        ATSDK.setNetworkLogDebug(true);//应用上线前须关闭

    }
    public void initSplashAdd(Activity mContext){
        splashAd = new ATSplashAd(mContext, AdConfig.开屏, null, 4000);
        splashAd.loadAd();
    }

    //开屏广告
    public  void splashAd(Activity mContext,ViewGroup container, AdListener listener) {
        if (splashAd!=null&&splashAd.isAdReady()) {
            splashAd.setAdListener( new ATSplashExListener() {
                @Override
                public void onDeeplinkCallback(ATAdInfo atAdInfo, boolean b) {

                }

                @Override
                public void onDownloadConfirm(Context context, ATAdInfo atAdInfo, ATNetworkConfirmInfo atNetworkConfirmInfo) {

                }

                @Override
                public void onAdLoaded(boolean b) {

                }

                @Override
                public void onAdLoadTimeout() {
                    listener.onClose();
                }

                @Override
                public void onNoAdError(AdError adError) {
                    listener.onClose();
                }

                @Override
                public void onAdShow(ATAdInfo atAdInfo) {
                    listener.onShow();
                }

                @Override
                public void onAdClick(ATAdInfo atAdInfo) {

                }

                @Override
                public void onAdDismiss(ATAdInfo atAdInfo, ATSplashAdExtraInfo atSplashAdExtraInfo) {
                    listener.onClose();
                }
            });
            //container大小至少占屏幕75%
            splashAd.show(mContext, container);
        }else{
            //重新加载
            initSplashAdd(mContext);
            listener.onClose();
        }
    }

    public  void initRewardVideo(Activity activity){
        if (mRewardVideoAd==null||!mRewardVideoAd.isAdReady()){
            mRewardVideoAd = new ATRewardVideoAd(activity.getApplicationContext(), AdConfig.激励视频);
            mRewardVideoAd.setAdListener(new ATRewardVideoListener(){
                @Override
                public void onRewardedVideoAdLoaded() {
                    Log.e("mRewardVideoAd","onRewardedVideoAdLoaded");
                }

                @Override
                public void onRewardedVideoAdFailed(AdError adError) {
                    Log.e("mRewardVideoAd","onRewardedVideoAdFailed");
                }

                @Override
                public void onRewardedVideoAdPlayStart(ATAdInfo atAdInfo) {
                }

                @Override
                public void onRewardedVideoAdPlayEnd(ATAdInfo atAdInfo) {
                    Log.e("mRewardVideoAd","onRewardedVideoAdPlayEnd");
                }

                @Override
                public void onRewardedVideoAdPlayFailed(AdError adError, ATAdInfo atAdInfo) {
                    Log.e("mRewardVideoAd","onRewardedVideoAdPlayFailed");
                }

                @Override
                public void onRewardedVideoAdClosed(ATAdInfo atAdInfo) {
                }

                @Override
                public void onRewardedVideoAdPlayClicked(ATAdInfo atAdInfo) {

                }

                @Override
                public void onReward(ATAdInfo atAdInfo) {
                }
            });
            mRewardVideoAd.load();
        }
    }
      public  void initTVRewardVideo(Activity activity){
        if (mRewardTVVideoAd==null||!mRewardTVVideoAd.isAdReady()){
            mRewardTVVideoAd = new ATRewardVideoAd(activity.getApplicationContext(), AdConfig.电视剧_激励);
            mRewardTVVideoAd.setAdListener(new ATRewardVideoListener() {
                @Override
                public void onRewardedVideoAdLoaded() {
                    Log.e("mRewardTVVideoAd","onRewardedVideoAdLoaded");
                }

                @Override
                public void onRewardedVideoAdFailed(AdError adError) {
                    Log.e("mRewardTVVideoAd","onRewardedVideoAdFailed");
                }

                @Override
                public void onRewardedVideoAdPlayStart(ATAdInfo atAdInfo) {
                    Log.e("mRewardTVVideoAd","onRewardedVideoAdPlayStart");
                }

                @Override
                public void onRewardedVideoAdPlayEnd(ATAdInfo atAdInfo) {
                    Log.e("mRewardTVVideoAd","onRewardedVideoAdPlayEnd");
                }

                @Override
                public void onRewardedVideoAdPlayFailed(AdError adError, ATAdInfo atAdInfo) {
                    Log.e("mRewardTVVideoAd","onRewardedVideoAdPlayFailed");
                }

                @Override
                public void onRewardedVideoAdClosed(ATAdInfo atAdInfo) {
                    Log.e("mRewardTVVideoAd","onRewardedVideoAdClosed");
                }

                @Override
                public void onRewardedVideoAdPlayClicked(ATAdInfo atAdInfo) {
                    Log.e("mRewardTVVideoAd","onRewardedVideoAdPlayClicked");
                }

                @Override
                public void onReward(ATAdInfo atAdInfo) {
                    Log.e("mRewardTVVideoAd","onReward");
                }
            });
            mRewardTVVideoAd.load();
        }
    }
    //激励视频
    public  void rewardVideo(Activity activity,AdListener listener){
        if (mRewardVideoAd!=null&&mRewardVideoAd.isAdReady()) {
            mRewardVideoAd.setAdListener(new ATRewardVideoListener(){
                @Override
                public void onRewardedVideoAdLoaded() {
                    Log.e("mRewardVideoAd","onRewardedVideoAdLoaded");
                }

                @Override
                public void onRewardedVideoAdFailed(AdError adError) {
                    Log.e("mRewardVideoAd","onRewardedVideoAdFailed");
                }

                @Override
                public void onRewardedVideoAdPlayStart(ATAdInfo atAdInfo) {
                    listener.onShow();
                }

                @Override
                public void onRewardedVideoAdPlayEnd(ATAdInfo atAdInfo) {
                    Log.e("mRewardVideoAd","onRewardedVideoAdPlayEnd");
                }

                @Override
                public void onRewardedVideoAdPlayFailed(AdError adError, ATAdInfo atAdInfo) {
                    Log.e("mRewardVideoAd","onRewardedVideoAdPlayFailed");
                }

                @Override
                public void onRewardedVideoAdClosed(ATAdInfo atAdInfo) {
                    listener.onClose();
                    initRewardVideo(activity);
                }

                @Override
                public void onRewardedVideoAdPlayClicked(ATAdInfo atAdInfo) {

                }

                @Override
                public void onReward(ATAdInfo atAdInfo) {
                    listener.reword(true);
                    initRewardVideo(activity);
                }
            });
            mRewardVideoAd.show(activity);
        }else{
            //重新加载
            listener.reword(false);
            listener.onClose();
            initRewardVideo(activity);
        }
    }
    public  void rewardTVVideo(Activity activity,AdListener listener){
        if (mRewardTVVideoAd!=null&&mRewardTVVideoAd.isAdReady()) {
            mRewardTVVideoAd.setAdListener(new ATRewardVideoListener(){
                @Override
                public void onRewardedVideoAdLoaded() {

                }

                @Override
                public void onRewardedVideoAdFailed(AdError adError) {

                }

                @Override
                public void onRewardedVideoAdPlayStart(ATAdInfo atAdInfo) {
                    listener.onShow();
                }

                @Override
                public void onRewardedVideoAdPlayEnd(ATAdInfo atAdInfo) {

                }

                @Override
                public void onRewardedVideoAdPlayFailed(AdError adError, ATAdInfo atAdInfo) {

                }

                @Override
                public void onRewardedVideoAdClosed(ATAdInfo atAdInfo) {
                    listener.onClose();
                    initTVRewardVideo(activity);
                }

                @Override
                public void onRewardedVideoAdPlayClicked(ATAdInfo atAdInfo) {

                }

                @Override
                public void onReward(ATAdInfo atAdInfo) {
                    listener.reword(true);
                    initTVRewardVideo(activity);
                }
            });
            mRewardTVVideoAd.show(activity);
        }else{
            //重新加载
            listener.reword(false);
            listener.onClose();
            initTVRewardVideo(activity);
        }
    }


    public  void initNativeExpressAd(Activity activity){
        atNative = new ATNative(activity, AdConfig.信息流, null);
        //发起广告请求
        atNative.makeAdRequest();
    }
    public  void initNativeBannerExpressAd(Activity activity){
        atBannerNative = new ATNative(activity, AdConfig.信息流, null);
        //发起广告请求
        atBannerNative.makeAdRequest();
    }
    static NativeAd mNativeAd;
    static NativeAd mBannerNativeAd;
//    //信息流
    @SuppressLint("MissingInflatedId")
    public  void nativeExpressAd(Activity activity, ViewGroup container){
        //初始化广告加载对象
        if (atNative == null){
            return;
        }

        if(!atNative.checkAdStatus().isReady()){
            return;
        }

        View inflate = LayoutInflater.from(activity).inflate(R.layout.layout_native_selfrender, null);
        ATNativeAdView  mATNativeAdView = inflate.findViewById(R.id.native_ad_view);
        View  mSelfRenderView = inflate.findViewById(R.id.native_selfrender_view); //可在xml布局定义
        container.addView(inflate);
        NativeAd nativeAd = atNative.getNativeAd();
        //开发者可以在调用getNativeAd后直接使用ATNative#makeAdRequest发起预加载下一次的广告
        //loadNativeAd();

        if (nativeAd != null) {
            if (mNativeAd != null) {
                mNativeAd.destory();
            }

            mNativeAd = nativeAd;

            ATNativePrepareInfo nativePrepareInfo = null;

            if (!mNativeAd.isNativeExpress()) {
                //自渲染
                nativePrepareInfo = new ATNativePrepareInfo();
                bindSelfRenderView(activity, mNativeAd.getAdMaterial(), mSelfRenderView, nativePrepareInfo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        container.setVisibility(View.GONE);
                    }
                });
                mNativeAd.renderAdContainer(mATNativeAdView, mSelfRenderView);
            } else {
                //模板渲染
                mNativeAd.renderAdContainer(mATNativeAdView, null);
            }
            mNativeAd.prepare(mATNativeAdView, nativePrepareInfo);
        }
    }
    @SuppressLint("MissingInflatedId")
    public  void nativeBannerExpressAd(Activity activity, ViewGroup container){
        //初始化广告加载对象
        if (atBannerNative == null){
            return;
        }

        if(!atBannerNative.checkAdStatus().isReady()){
            if(atNative.checkAdStatus().isReady()){
                atBannerNative = atNative;
            } else{
                return;
            }
        }

        View inflate = LayoutInflater.from(activity).inflate(R.layout.layout_native_selfrender, null);
        ATNativeAdView  mATNativeAdView = inflate.findViewById(R.id.native_ad_view);
        View  mSelfRenderView = inflate.findViewById(R.id.native_selfrender_view); //可在xml布局定义
        container.addView(inflate);
        NativeAd nativeAd = atBannerNative.getNativeAd();
        //开发者可以在调用getNativeAd后直接使用ATNative#makeAdRequest发起预加载下一次的广告
        //loadNativeAd();

        if (nativeAd != null) {
            if (mBannerNativeAd != null) {
                mBannerNativeAd.destory();
            }

            mBannerNativeAd = nativeAd;

            ATNativePrepareInfo nativePrepareInfo = null;

            if (!mBannerNativeAd.isNativeExpress()) {
                //自渲染
                nativePrepareInfo = new ATNativePrepareInfo();
                bindSelfRenderView(activity, mBannerNativeAd.getAdMaterial(), mSelfRenderView, nativePrepareInfo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        container.setVisibility(View.GONE);
                    }
                });
                mBannerNativeAd.renderAdContainer(mATNativeAdView, mSelfRenderView);
            } else {
                //模板渲染
                mBannerNativeAd.renderAdContainer(mATNativeAdView, null);
            }
            mBannerNativeAd.prepare(mATNativeAdView, nativePrepareInfo);
        }
    }
    public  void  initInterstitialAd(Activity activity){
        mInterstitialAd = new ATInterstitial(activity, AdConfig.插屏);
        mInterstitialAd.load();
    }
    //插屏
    public  void interstitialAd(Activity activity,AdListener adListener) {

        if (mInterstitialAd!=null&&mInterstitialAd.isAdReady()) {
            mInterstitialAd.setAdListener( new ATInterstitialListener() {
                @Override
                public void onInterstitialAdLoaded() {

                }

                @Override
                public void onInterstitialAdLoadFail(AdError adError) {
                    adListener.onClose();
                }

                @Override
                public void onInterstitialAdClicked(ATAdInfo atAdInfo) {

                }

                @Override
                public void onInterstitialAdShow(ATAdInfo atAdInfo) {
                    adListener.onShow();
                }

                @Override
                public void onInterstitialAdClose(ATAdInfo atAdInfo) {
                    adListener.onClose();
                }

                @Override
                public void onInterstitialAdVideoStart(ATAdInfo atAdInfo) {

                }

                @Override
                public void onInterstitialAdVideoEnd(ATAdInfo atAdInfo) {

                }

                @Override
                public void onInterstitialAdVideoError(AdError adError) {
                    adListener.onClose();
                }
            });
            mInterstitialAd.show(activity);
            initInterstitialAd(activity);
        }else{
            //重新加载
            initInterstitialAd(activity);
            adListener.onClose();
        }
    }
    //Banner
    public  void bannerAd(Activity activity,ViewGroup container) {
        mBannerView = new ATBannerView(activity);
        mBannerView.setPlacementId(AdConfig.横幅);
        mBannerView.setBannerAdListener(new ATBannerExListener() {
            @Override
            public void onDeeplinkCallback(boolean b, ATAdInfo atAdInfo, boolean b1) {

            }

            @Override
            public void onDownloadConfirm(Context context, ATAdInfo atAdInfo, ATNetworkConfirmInfo atNetworkConfirmInfo) {

            }

            @Override
            public void onBannerLoaded() {
                Log.e("banner","onBannerLoaded");

            }

            @Override
            public void onBannerFailed(AdError adError) {
                Log.e("banner","onBannerFailed");
            }

            @Override
            public void onBannerClicked(ATAdInfo atAdInfo) {

            }

            @Override
            public void onBannerShow(ATAdInfo atAdInfo) {

            }

            @Override
            public void onBannerClose(ATAdInfo atAdInfo) {

            }

            @Override
            public void onBannerAutoRefreshed(ATAdInfo atAdInfo) {

            }

            @Override
            public void onBannerAutoRefreshFail(AdError adError) {

            }
        });
            container.removeAllViews();
//        int width = activity.getResources().getDisplayMetrics().widthPixels;//定一个宽度值，比如屏幕宽度
//        int height = (int) (130*1.5);

//如果出现Banner有时高、有时低的情况，请使用此代码
            float ratio = 320 / 50f;//必须跟Taku后台配置的Banner广告源宽高比例一致，假设尺寸为320x50
            int width = activity.getResources().getDisplayMetrics().widthPixels;//定一个宽度值，比如屏幕宽度
            int height = (int) (width / ratio);
            mBannerView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
            container.addView(mBannerView);
            mBannerView.loadAd();
    }

    private static final String TAG= "KsContentPageADActivity";
    public static void GetsContentPageAD(Activity activity,AdFragmentListener fragmentListener) {
      /*  VideoTuBeConfig videoTuBeConfig = new VideoTuBeConfig.Builder()
                .setRewardId(AdConfig.短剧_奖励)
                .setFreeEpisodeCount(10)
                .setUnlockEpisodesCount(5)
                .build();
        OSETVideoContent.getInstance().setPosId(AdConfig.短剧).loadTuBe(activity, videoTuBeConfig, new OSETVideoTuBeListener() {

            @Override
            public void onLoadFail(String code, String e) {
                Log.e(TAG, "GetsContentPageAD onLoadFail code=" + code + " message=" + e);
            }

            @Override
            public void onLoadSuccess(VideoContentResult videoContentResult) {
                Log.e(TAG, "GetsContentPageAD onLoadSuccess");
                fragmentListener.onShow(videoContentResult.getFragment());
            }

            @Override
            public void unlockSuccess() {
                Log.d(TAG,"短剧解锁成功");
            }

            @Override
            public void unlockFail(String errorCode, String errorMessage) {
                Log.d(TAG, "短剧解锁失败 errorCode=" + errorCode + " errorMessage=" + errorMessage);
            }
        });
*/
    }
}
