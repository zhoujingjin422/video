package com.ruifenglb.www.ui.play;

import static com.ruifenglb.www.utils.AdsWatchUtils.DJ_NUM;
import static com.ruifenglb.www.utils.AdsWatchUtils.TV_NUM;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.ad.biddingsdk.AdListener;
import com.app.ad.biddingsdk.AdUtils;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

import com.dueeeke.videocontroller.component.GestureView;
import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;


import com.ruifenglb.av.play.AvVideoController;
import com.ruifenglb.av.play.AvVideoView;
import com.ruifenglb.av.play.ControllerClickListener;
import com.ruifenglb.av.play.HdClickListener;
import com.ruifenglb.www.App;
import com.ruifenglb.www.R;
import com.ruifenglb.www.ads.AdViewBinder;
import com.ruifenglb.www.base.BaseSupportActivity;
import com.ruifenglb.www.bean.BaseResult;
import com.ruifenglb.www.bean.PageResult;
import com.ruifenglb.www.bean.PlayFromBean;
import com.ruifenglb.www.bean.RecommendBean;
import com.ruifenglb.www.bean.StartBean;
import com.ruifenglb.www.bean.VodBean;
import com.ruifenglb.www.jiexi.BackListener;
import com.ruifenglb.www.jiexi.JieXiUtils;
import com.ruifenglb.www.netservice.PlayService;
import com.ruifenglb.www.network.RetryWhen;
import com.ruifenglb.www.pip.PIPManager;
import com.ruifenglb.www.ui.home.Vod;
import com.ruifenglb.www.ui.login.LoginActivity;
import com.ruifenglb.www.utils.AdsWatchUtils;
import com.ruifenglb.www.utils.Retrofit2Utils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.drakeet.multitype.MultiTypeAdapter;
import com.ruifenglb.www.utils.AgainstCheatUtil;
import com.ruifenglb.www.utils.UserUtils;

public class PlayActivity extends BaseSupportActivity implements ControllerClickListener, HdClickListener, BackListener, VideoView.OnStateChangeListener {
    public static final String KEY_VOD = "KEY_VOD";
    public static final String KEY_SHOW_PROGRESS = "KEY_SHOW_PROGRESS";

    @BindView(R.id.avv_play)
    AvVideoView videoView;
    @BindView(R.id.rv_play_content)
    RecyclerView recyclerView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_year)
    TextView tvYear;
    @BindView(R.id.tv_actor)
    TextView tvActor;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_play_number)
    TextView tvPlayNumber;
    @BindView(R.id.tv_score)
    TextView tvScore;
    @BindView(R.id.tv_summary_hint)
    TextView tvSummaryHint;
    @BindView(R.id.tv_summary)
    TextView tvSummary;
    @BindView(R.id.scSummary)
    ScrollView scSummary;
    @BindView(R.id.iv_close_intro)
    ImageView ivCloseIntro;


    private AvVideoController controller;
    private MultiTypeAdapter adapter;
    private List<Object> items = new ArrayList<>();

    private List<String> urlList = new ArrayList<>();
    private boolean isPlay = false;
    private int urlIndex = 0;


    private VodBean mVodBean;
    private Disposable disposable;
    private static boolean reword = false;
    public static void startByVod(Vod vod, Activity activity) {
        if (vod.getVod_copyright() == 1) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(vod.getVod_jumpurl()));
            ActivityUtils.startActivity(intent);
        } else {
            if (vod instanceof VodBean){
                int typeId = ((VodBean) vod).getType_id();
                if (typeId==2){
                    //电视剧第一集不看
                    if (AdsWatchUtils.needShowAds(activity)){
                        AdUtils.rewardTVVideo(activity, new AdListener() {
                            @Override
                            public void onShow() {

                            }

                            @Override
                            public void onClose() {
                                if (reword){
                                    reword= false;
                                    Intent intent = new Intent(App.getInstance(), NewPlayActivity.class);
                                    intent.putExtra(KEY_VOD, vod);
                                    PIPManager.getInstance().stopFloatWindow();
                                    PIPManager.getInstance().reset();
                                    ActivityUtils.startActivity(intent, R.anim.slide_in_right, R.anim.no_anim);
                                }
                            }

                            @Override
                            public void reword() {
                                reword = true;
                                int tvNum = com.ruifenglb.www.download.SPUtils.getInt(activity, TV_NUM, 0);
                                tvNum++;
                                com.ruifenglb.www.download.SPUtils.setInt(activity, TV_NUM, tvNum);
                            }
                        });
                    }else{
                        int tvNum = com.ruifenglb.www.download.SPUtils.getInt(activity, TV_NUM, 0);
                        tvNum++;
                        com.ruifenglb.www.download.SPUtils.setInt(activity, TV_NUM, tvNum);
                        Intent intent = new Intent(App.getInstance(), NewPlayActivity.class);
                        intent.putExtra(KEY_VOD, vod);
                        PIPManager.getInstance().stopFloatWindow();
                        PIPManager.getInstance().reset();
                        ActivityUtils.startActivity(intent, R.anim.slide_in_right, R.anim.no_anim);
                    }
                }else if (typeId==29){
                    if (AdsWatchUtils.needShowDJAds(activity)){
                        AdUtils.rewardTVVideo(activity, new AdListener() {
                            @Override
                            public void onShow() {

                            }

                            @Override
                            public void onClose() {
                                if (reword){
                                    reword= false;
                                    Intent intent = new Intent(App.getInstance(), NewPlayActivity.class);
                                    intent.putExtra(KEY_VOD, vod);
                                    PIPManager.getInstance().stopFloatWindow();
                                    PIPManager.getInstance().reset();
                                    ActivityUtils.startActivity(intent, R.anim.slide_in_right, R.anim.no_anim);
                                }
                            }

                            @Override
                            public void reword() {
                                reword = true;
                                int tvNum = com.ruifenglb.www.download.SPUtils.getInt(activity, DJ_NUM, 0);
                                tvNum++;
                                com.ruifenglb.www.download.SPUtils.setInt(activity, TV_NUM, tvNum);
                            }
                        });
                    }else{
                        int tvNum = com.ruifenglb.www.download.SPUtils.getInt(activity, "", 0);
                        tvNum++;
                        com.ruifenglb.www.download.SPUtils.setInt(activity, DJ_NUM, tvNum);
                        Intent intent = new Intent(App.getInstance(), NewPlayActivity.class);
                        intent.putExtra(KEY_VOD, vod);
                        PIPManager.getInstance().stopFloatWindow();
                        PIPManager.getInstance().reset();
                        ActivityUtils.startActivity(intent, R.anim.slide_in_right, R.anim.no_anim);
                    }
                }else{
                    AdUtils.rewardVideo(activity, new AdListener() {
                        @Override
                        public void onShow() {

                        }

                        @Override
                        public void onClose() {
                            if (reword){
                                reword= false;
                                Intent intent = new Intent(App.getInstance(), NewPlayActivity.class);
                                intent.putExtra(KEY_VOD, vod);
                                PIPManager.getInstance().stopFloatWindow();
                                PIPManager.getInstance().reset();
                                ActivityUtils.startActivity(intent, R.anim.slide_in_right, R.anim.no_anim);
                            }
                        }

                        @Override
                        public void reword() {
                            reword = true;
                        }
                    });
                }

            }
        }
    }

    public static void startByCollection(int vodId) {
        VodBean vodBean = new VodBean();
        vodBean.setVod_id(vodId);

        Intent intent = new Intent(App.getInstance(), NewPlayActivity.class);
        intent.putExtra(KEY_VOD,vodBean);
        PIPManager.getInstance().stopFloatWindow();
        PIPManager.getInstance().reset();
        ActivityUtils.startActivity(intent, R.anim.slide_in_right, R.anim.no_anim);
    }

    public static void startByPlayScore(int vodId) {
        VodBean vodBean = new VodBean();
        vodBean.setVod_id(vodId);

        Intent intent = new Intent(App.getInstance(), NewPlayActivity.class);
        intent.putExtra(KEY_VOD,vodBean);
        intent.putExtra(KEY_SHOW_PROGRESS,true);
        PIPManager.getInstance().stopFloatWindow();
        PIPManager.getInstance().reset();
        ActivityUtils.startActivity(intent, R.anim.slide_in_right, R.anim.no_anim);
    }

    public static void startByPlayScoreResult(Fragment content, int vodId) {
        VodBean vodBean = new VodBean();
        vodBean.setVod_id(vodId);
        Intent intent = new Intent(App.getInstance(), NewPlayActivity.class);
        intent.putExtra(KEY_VOD,vodBean);
        intent.putExtra(KEY_SHOW_PROGRESS,true);
        PIPManager.getInstance().stopFloatWindow();
        PIPManager.getInstance().reset();
     //   ActivityUtils.startActivity(intent, R.anim.slide_in_right, R.anim.no_anim);
        content.startActivityForResult(intent, 1);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_play;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarColor(this, Color.BLACK);
        Intent intent = getIntent();
        if (intent != null) {
            mVodBean = (VodBean) intent.getSerializableExtra(KEY_VOD);
        }
        if (mVodBean == null) {
            finish();
            return;
        }
        initView();
        getPlayInfoData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onDestroy() {
        stopData();
        JieXiUtils.INSTANCE.stopGet();
        videoView.release();
        controller.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressedSupport() {
        if (!videoView.onBackPressed()) {
            super.onBackPressedSupport();
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_av_back) {
            finish();
        }
    }

    @Override
    public void onPlayProgress(int duration, int position) {

    }

    @Override
    public void onSendDanmu(String content, String textcolor) {

    }

    @Override
    public void onLongPress() {

    }

    @Override
    public void onSingleTapUp() {

    }

    @Override
    public boolean isLogin() {
        if(!UserUtils.isLogin()){
            LoginActivity.Companion.start();
        }
        return UserUtils.isLogin();
    }

    @Override
    public void switchHd(String url) {

    }

    ///解析回调
    @Override
    public void onSuccess(String url, int curParseIndex) {
        LogUtils.e(url);
        urlList.add(url);
        if (!isPlay) {
            controller.hideJiexi();
            play(url);
            isPlay = true;
        }
    }


    @Override
    public void onError() {

    }

    @Override
    public void onProgressUpdate(String msg) {

    }


    ///播放器监听
    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == -1) {
            urlIndex = urlIndex + 1;
            if (urlIndex < urlList.size()) {
                play(urlList.get(urlIndex));
            } else {
                urlIndex = 0;
                videoView.postDelayed(() -> play(urlList.get(urlIndex)), 500);
            }
        }
    }

    private void play(String url) {


        videoView.post(new Runnable() {
            @Override
            public void run() {
                Map<String,String> headers = new HashMap<>();
                headers.put("User-Agent", " python-requests/2.24.0");
                headers.put("Accept-Encoding"," gzip, deflate");
                headers.put("Accept"," */*");
                headers.put("Connection", " keep-alive");
                if (url.contains("titan.mgtv.com")) {
                    headers.put("Referer" , " www.mgtv.com");
                    //videoView.setPlayerFactory(ExoMediaPlayerFactory.create());
                }else {
                   // MyPlayerManager.Companion.checktFactory(true);
                }
                videoView.release();
                if (url.startsWith("//")) {
                    videoView.setUrl("https:" + url,headers);
                } else {
                    videoView.setUrl(url,headers);
                }
                videoView.start();
                VideoViewManager.instance().setPlayOnMobileNetwork(true);
                controller.startPlay();
            }
        });
    }

    private void title(String string) {
        if (controller != null) {
            controller.post(new Runnable() {
                @Override
                public void run() {
                    controller.setTitle(string);
                }
            });
        }
    }

    private void initView() {
        controller = new AvVideoController(videoView, this);
        controller.setControllerClickListener(this);

        //添加手势
        GestureView gestureControlView = new GestureView(this);
        controller.addControlComponent(gestureControlView);

        videoView.setHdClickListener(this);
        //设置控制器，如需定制可继承BaseVideoController
        videoView.setVideoController(controller);
        videoView.setOnStateChangeListener(this);
        videoView.setVideoSpeed(SPUtils.getInstance().getInt(AvVideoController.KEY_SPEED_INDEX,3));

        title(mVodBean.getVodName());
        initIntro();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MultiTypeAdapter();
        AdViewBinder adViewBinder = new AdViewBinder();
        adapter.register(StartBean.Ad.class,adViewBinder);
        adapter.register(VodBean.class, new PlayInfoViewBinder().setPlayerInfoClickListener(new PlayInfoViewBinder.PlayerInfoClickListener() {
            @Override
            public void onSummary(View view) {
                recyclerView.setVisibility(View.GONE);
                scSummary.setVisibility(View.VISIBLE);
            }
        }));
        adapter.register(RecommendBean.class, new RecommendViewBinder().setOnTypeChangeLisenter(new RecommendViewBinder.OnTypeChangeLisenter() {
            @Override
            public void onTypeChange(int type) {
                if (type == 0) {
                    getsameTypeData(true);
                } else {
                    getSameActorData();
                }
            }
        }));
        recyclerView.setAdapter(adapter);
    }

    private void initIntro() {
        tvTitle.setText(mVodBean.getVodName());
        tvYear.setText("年代：" + mVodBean.getVod_year());
        tvActor.setText("主演：" + mVodBean.getVod_actor());
        tvType.setText("类型：" + mVodBean.getType().getTypeName());

        tvStatus.setText("状态：" + mVodBean.getVodRemarks());
        tvYear.setText("播放：" + mVodBean.getVod_hits() + "次");
        tvActor.setText("评分：" + mVodBean.getVod_score());

        tvSummary.setText(mVodBean.getVod_blurb());
        ivCloseIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                scSummary.setVisibility(View.GONE);
            }
        });
    }

    private void getPlayInfoData() {
        PlayService playService = Retrofit2Utils.INSTANCE.createByGson(PlayService.class);
        if (AgainstCheatUtil.showWarn(playService)) {
            return;
        }
        playService.getVod(mVodBean.getVod_id(), 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach()

                .retryWhen(new RetryWhen(3, 3))
                .subscribe(new Observer<BaseResult<VodBean>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                            disposable = null;
                        }
                        disposable = d;
                    }

                    @Override
                    public void onNext(BaseResult<VodBean> result) {
                        if (result != null) {
                            if (result.isSuccessful()) {
                                VodBean vodBean = result.getData();
                                if (vodBean != null) {
                                    mVodBean = vodBean;
                                    items.add(mVodBean);
                                    adapter.setItems(items);
                                    adapter.notifyDataSetChanged();

                                    parseData(vodBean);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        getsameTypeData(false);
                    }
                });
    }

    private void getsameTypeData(boolean isChange) {
        PlayService playService = Retrofit2Utils.INSTANCE.createByGson(PlayService.class);
        if (AgainstCheatUtil.showWarn(playService)) {
            return;
        }
        playService.getSameTypeList(mVodBean.getType_id(), mVodBean.getVod_class(), 1, 3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .onTerminateDetach()
                .retryWhen(new RetryWhen(2, 3))
                .subscribe(new Observer<PageResult<VodBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PageResult<VodBean> result) {
                        if (result != null) {
                            List<VodBean> vodBeans = result.getData().getList();
                            if (result.isSuccessful()) {
                                items.add(new RecommendBean(vodBeans, 0));
                                adapter.setItems(items);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getSameActorData() {
        PlayService playService = Retrofit2Utils.INSTANCE.createByGson(PlayService.class);
        if (AgainstCheatUtil.showWarn(playService)) {
            return;
        }
        playService.getSameActorList(mVodBean.getVod_id(), mVodBean.getVod_actor(), 1, 3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach()
                .retryWhen(new RetryWhen(2, 3))
                .subscribe(new Observer<PageResult<VodBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PageResult<VodBean> result) {
                        if (result != null) {
                            if (result.isSuccessful()) {
                                List<VodBean> vodBeans = result.getData().getList();

                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getCommentList() {

    }

    private void stopData() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    private void parseData(final VodBean vodBean) {
        controller.showJiexi();
        String s = "";
        String u = "https://v.qq.com/x/cover/lvjgpbrmo0dpz14.html";
        // 开始解析地址
        List<PlayFromBean> fromBeanList = vodBean.getVod_play_list();
        if (fromBeanList != null) {
            for (PlayFromBean fromBean : fromBeanList) {
                try {
                    s = fromBean.getPlayer_info().getParse();
                    u = fromBean.getUrls().get(0).getUrl();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                JieXiUtils.INSTANCE.getPlayUrl(s, u, this);
            }
        }
    }
}
