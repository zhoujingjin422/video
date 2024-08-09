package com.ruifenglb.www.ui.play

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ad.biddingsdk.AdListener
import com.app.ad.biddingsdk.AdUtils
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.StringUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dueeeke.videoplayer.player.VideoViewManager
import com.github.StormWyrm.wanandroid.base.exception.ResponseException
import com.github.StormWyrm.wanandroid.base.fragment.BaseFragment
import com.github.StormWyrm.wanandroid.base.net.RequestManager
import com.github.StormWyrm.wanandroid.base.net.observer.BaseObserver
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.ruifenglb.av.play.AvVideoView
import com.ruifenglb.av.play.VideoViewImpt
import com.ruifenglb.www.ApiConfig
import com.ruifenglb.www.App
import com.ruifenglb.www.R
import com.ruifenglb.www.bean.*
import com.ruifenglb.www.download.SPUtils
import com.ruifenglb.www.jiexi.BackListener
import com.ruifenglb.www.jiexi.JieXiUtils2
import com.ruifenglb.www.netservice.VodService
import com.ruifenglb.www.pip.PIPManager
import com.ruifenglb.www.ui.down.AllDownloadActivity
import com.ruifenglb.www.ui.down.cache.Square
import com.ruifenglb.www.ui.down.cache.SquareViewBinder
import com.ruifenglb.www.ui.feedback.FeedbackActivity
import com.ruifenglb.www.ui.home.MyDividerItemDecoration
import com.ruifenglb.www.ui.login.LoginActivity
import com.ruifenglb.www.ui.pay.PayActivity
import com.ruifenglb.www.ui.share.ShareActivity
import com.ruifenglb.www.ui.widget.HitDialog
import com.ruifenglb.www.ui.widget.HitDialog.OnHitDialogClickListener
import com.ruifenglb.www.utils.*
import com.ruifenglb.www.utils.AdsWatchUtils.DJ_NUM
import com.ruifenglb.www.utils.AdsWatchUtils.TV_NUM
import com.ruifenglb.www.utils.DensityUtils.dp2px
import com.ruifenglb.www.utils.DensityUtils.getScreenWidth
import com.ruifenglb.www.utils.MMkvUtils.Companion.Builds
import com.ruifenglb.www.utils.UserUtils.isLogin
import com.ruifenglb.www.utils.decoration.GridItemDecoration
import jaygoo.library.m3u8downloader.control.DownloadPresenter
import jaygoo.library.m3u8downloader.db.table.M3u8DoneInfo
import jaygoo.library.m3u8downloader.db.table.M3u8DownloadingInfo
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.activity_collection.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_new_play.*
import kotlinx.android.synthetic.main.activity_pay.*
import kotlinx.android.synthetic.main.fragment_play_detail.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.item_playinfo.*
import kotlinx.android.synthetic.main.item_recommend.*
import me.drakeet.multitype.MultiTypeAdapter
import org.litepal.LitePalApplication
import java.util.*

class VideoDetailFragment : BaseFragment() {

    private lateinit var mVodBean: VodBean
    private var isParse = false
    private var isCollected: Boolean = false
    private var urlIndex: Int = 0 //播放集
    private var playSourceIndex: Int = 0//播放源
    private lateinit var vod_play_list: List<PlayFromBean>//播放视频列表
    private var curType = 0//推荐 默认是相似推荐
    private var curSameTypePage = 1

    private lateinit var videoView: AvVideoView
    private var curSameActorPage = 1
    private var curParseIndex = 0//记录上一次解析到的位置，如果出现解析到是视频不能播放的话 自动解析下一条
    private var curFailIndex = -1
    private lateinit var rvLastest: RecyclerView
    private lateinit var tlPlaySource: TabLayout
    private lateinit var playActivity: NewPlayActivity




     class CommentAdapter : BaseQuickAdapter<CommentBean, BaseViewHolder>(R.layout.item_hot_comment) {
        override fun convert(helper: BaseViewHolder, item: CommentBean?) {



            helper.let {
                item?.run {


                    //////////////////////QQ7242484///////////////////////////////////////
                    it.setText(R.id.tvUser, comment_name)
                    it.setText(R.id.tvTime, TimeUtils.millis2String(comment_time * 1000))
                    it.setText(R.id.tvComment, comment_content)
                    if(group_id==2){
                      /*  it.setImageResource(R.id.tv_iSvip, R.drawable.ic_common_vip_invalid)
                        it.setVisible(R.id.tv_iSvip, true);*/
                    }else if(group_id==3){
                      /*  it.setImageResource(R.id.tv_iSvip, R.drawable.ic_common_vip)
                        it.setVisible(R.id.tv_iSvip, true)*/
                    }else{
                       // it.setVisible(R.id.tv_iSvip, false);
                    }
                    val ivAvatar = it.getView<ImageView>(R.id.ivAvatar)
                    if (user_portrait.isNotEmpty()) {
                        Glide.with(helper.convertView)
                            .load(ApiConfig.MOGAI_BASE_URL + "/" + user_portrait)
                            .apply(RequestOptions.bitmapTransform(CircleCrop()))
                            .into(ivAvatar)
                    } else {
                        Glide.with(helper.convertView)
                            .load(R.drawable.ic_default_avator)
                            .apply(RequestOptions.bitmapTransform(CircleCrop()))
                            .into(ivAvatar)
                    }
                }
            }
        }



    }
    private var reword = false
    private val recommendAdapter: RecommendAdapter by lazy {

        RecommendAdapter().apply {
            setOnItemClickListener { adapter, view, position ->
                val vodBean = adapter.getItem(position) as VodBean
                if (vodBean.type_id==29){
                    if (AdsWatchUtils.needShowAds(activity)) {
                        AdUtils.rewardTVVideo(activity, object : AdListener {
                            override fun onShow() {}
                            override fun onClose() {
                                if (reword) {
                                    reword = false
                                    playActivity.showNewVideo(vodBean)
                                }
                            }

                            override fun reword() {
                                reword = true
                                var tvNum = SPUtils.getInt(activity, DJ_NUM, 0)
                                tvNum++
                                SPUtils.setInt(activity, DJ_NUM, tvNum)
                            }
                        })
                    } else {
                        var tvNum = SPUtils.getInt(activity, DJ_NUM, 0)
                        tvNum++
                        SPUtils.setInt(activity, DJ_NUM, tvNum)
                        reword = false
                        playActivity.showNewVideo(vodBean)
                    }
                }else if (vodBean.type_id==2){

                    //电视剧第一集不看
                    if (AdsWatchUtils.needShowAds(activity)) {
                        AdUtils.rewardTVVideo(activity, object : AdListener {
                            override fun onShow() {}
                            override fun onClose() {
                                if (reword) {
                                    reword = false
                                    playActivity.showNewVideo(vodBean)
                                }
                            }

                            override fun reword() {
                                reword = true
                                var tvNum = SPUtils.getInt(activity, TV_NUM, 0)
                                tvNum++
                                SPUtils.setInt(activity, TV_NUM, tvNum)
                            }
                        })
                    } else {
                        var tvNum = SPUtils.getInt(activity, TV_NUM, 0)
                        tvNum++
                        SPUtils.setInt(activity, TV_NUM, tvNum)
                        reword = false
                        playActivity.showNewVideo(vodBean)
                    }
                }else{
                    //非电视剧，都要看激励广告
                    AdUtils.rewardVideo(activity,object :AdListener{
                        override fun onShow() {

                        }

                        override fun onClose() {
                            if (reword){
                                reword = false
                                playActivity.showNewVideo(vodBean)
                            }
                        }
                        override fun reword() {
                            reword = true
                        }
                    })
                }
            }
        }
    }

    private val selectionAdapter: SelectionAdapter by lazy {
        SelectionAdapter().apply {
            setOnItemClickListener { adapter, view, position ->
                if (urlIndex != position) {
                    if (mVodBean.type_id==29){
                        if (AdsWatchUtils.needShowAds(activity)) {
                            AdUtils.rewardTVVideo(activity, object : AdListener {
                                override fun onShow() {}
                                override fun onClose() {
                                    if (reword) {
                                        reword = false
                                        urlIndex = position
                                        playActivity.changeSelection(urlIndex, false)
                                        notifyDataSetChanged()
                                    }
                                }

                                override fun reword() {
                                    reword = true
                                    var tvNum = SPUtils.getInt(activity, DJ_NUM, 0)
                                    tvNum++
                                    SPUtils.setInt(activity, DJ_NUM, tvNum)
                                }
                            })
                        } else {
                            var tvNum = SPUtils.getInt(activity, DJ_NUM, 0)
                            tvNum++
                            SPUtils.setInt(activity, DJ_NUM, tvNum)
                            reword = false
                            urlIndex = position
                            playActivity.changeSelection(urlIndex, false)
                            notifyDataSetChanged()
                        }
                    }else if (mVodBean.type_id==2){

                        //电视剧第一集不看
                        if (AdsWatchUtils.needShowAds(activity)) {
                            AdUtils.rewardTVVideo(activity, object : AdListener {
                                override fun onShow() {}
                                override fun onClose() {
                                    if (reword) {
                                        reword = false
                                        urlIndex = position
                                        playActivity.changeSelection(urlIndex, false)
                                        notifyDataSetChanged()
                                    }
                                }

                                override fun reword() {
                                    reword = true
                                    var tvNum = SPUtils.getInt(activity, TV_NUM, 0)
                                    tvNum++
                                    SPUtils.setInt(activity, TV_NUM, tvNum)
                                }
                            })
                        } else {
                            var tvNum = SPUtils.getInt(activity, TV_NUM, 0)
                            tvNum++
                            SPUtils.setInt(activity, TV_NUM, tvNum)
                            reword = false
                            urlIndex = position
                            playActivity.changeSelection(urlIndex, false)
                            notifyDataSetChanged()
                        }
                    }else{
                        //非电视剧，都要看激励广告
                        AdUtils.rewardVideo(activity,object :AdListener{
                            override fun onShow() {

                            }

                            override fun onClose() {
                                if (reword){
                                    reword = false
                                    urlIndex = position
                                    playActivity.changeSelection(urlIndex, false)
                                    notifyDataSetChanged()
                                }
                            }
                            override fun reword() {
                                reword = true
                            }
                        })
                    }

                }
            }
        }
    }



    public var isOrder = true//播放顺序，默认为顺序播放

    override fun getLayoutId(): Int {

        return R.layout.fragment_play_detail

    }

    public fun setData(vodBean: VodBean, urlIndex: Int, playSourceIndex: Int){
        mVodBean  = vodBean
        this.urlIndex = urlIndex
        this.playSourceIndex = playSourceIndex;

        initHeaderMsg()



        getSameTypeData()
    }

    override fun initView() {

        super.initView()

        playActivity = mActivity as NewPlayActivity

        arguments?.run {
            mVodBean = getParcelable(VOD_BEAN) ?: null as VodBean
            urlIndex = getInt(URL_INDEX)
            playSourceIndex = getInt(PLAY_SOURCE_INDEX)
        }



        initHeaderMsg()

        getSameTypeData()

    }

    override fun onResume() {
        super.onResume()

        getCollectionState()
    }

    fun changeCurIndex(urlIndex: Int) {
        this.urlIndex = urlIndex

        selectionAdapter.notifyDataSetChanged()

        scrollCurIndex(rvLastest)
    }
    var videoViewImpt: VideoViewImpt = VideoViewManager.instance()["pip"] as AvVideoView

    @SuppressLint("SetTextI18n")
    private fun initHeaderMsg() {
        val title = requireView().findViewById<TextView>(R.id.item_tv_playinfo_title)
        val intro = requireView().findViewById<TextView>(R.id.item_tv_playinfo_intro)
        val year = requireView().findViewById<TextView>(R.id.item_tv_playinfo_year)
        val area = requireView().findViewById<TextView>(R.id.item_tv_playinfo_area)
        val type = requireView().findViewById<TextView>(R.id.item_tv_playinfo_type)
        val score = requireView().findViewById<TextView>(R.id.item_tv_playinfo_score)
        val tvLastest = requireView().findViewById<TextView>(R.id.tvLastest)
        val ivLastest = requireView().findViewById<ImageView>(R.id.iv_lastest)
        val sortVodTypePic = requireView().findViewById<TextView>(R.id.item_svv_playtypePic)
        val sortVodView = requireView().findViewById<TextView>(R.id.item_svv_playinfo)
        val checkOrder = requireView().findViewById<CheckBox>(R.id.checkOrder)
        tlPlaySource = requireView().findViewById(R.id.tlPlaySource)
        rvLastest = requireView().findViewById(R.id.rvLastest)
        val ivGovip = requireView().findViewById<LinearLayout>(R.id.iv_go_vip)
        val awvPlayerDown = requireView().findViewById<FrameLayout>(R.id.awvPlayerDown)
        AdUtils.nativeExpressAd(requireActivity(),awvPlayerDown)
        //新信息流广告VIP免广告
        //新信息流广告VIP免广告
        val bannerLayout = requireView().findViewById<FrameLayout>(R.id.bannerLayout)
        // App.getApplication().adUtils.showBannerAd(activity,bannerLayout,object :AdListener(){})
        //新信息流广告VIP免广告
        if (SPUtils.getBoolean(context, "isVip") ) {

        } else {
        AdUtils.nativeExpressAd(activity, bannerLayout);
        }
        //新信息流广告VIP免广告
  //7242484
        var isVip = false
        if (isLogin() && UserUtils.userInfo?.group_id == 3) {
            isVip = true
        }
        val startBean = Objects.requireNonNull(Builds().loadStartBean(""))
        if (startBean != null && startBean.ads != null) {
            val ads = startBean.ads
//            if (isVip || ads.player_down_isvip == null || ads.player_down_isvip.status != 1 || ads.player_down_isvip.description.isNullOrEmpty()) {
//                ivGovip.visibility = View.GONE
//            } else {
//                if (ads.player_down_isvip.description.contains("||")) {
//                    val descriptions = ads.player_down_isvip.description.split("||")
//                    requireView().findViewById<TextView>(R.id.isvip_1).text = descriptions[0]
//                    requireView().findViewById<TextView>(R.id.isvip_2).text = descriptions[1]
//                }
//                ivGovip.visibility = View.VISIBLE
//            }



            if (startBean.app_play_recommend == null || startBean.app_play_recommend != "1") {
                requireView().findViewById<LinearLayout>(R.id.item_tuijian).visibility = View.GONE
            } else {
                requireView().findViewById<LinearLayout>(R.id.item_tuijian).visibility = View.VISIBLE
            }
        } else {
//            awvPlayerDown.visibility = View.GONE
            requireView().findViewById<LinearLayout>(R.id.item_tuijian).visibility = View.GONE

        }
//7242484
        requireView().findViewById<TextView>(R.id.item_tv_playinfo_grade).setOnClickListener {
            score()

        }



        requireView().findViewById<TextView>(R.id.item_tv_playinfo_collect)
                .setOnClickListener {
                    if (UserUtils.isLogin()) {
                        if (isCollected) {
                            uncollection()
                        } else {
                            collection()
                        }
                    } else {
                        ActivityUtils.startActivity(LoginActivity::class.java)
                    }

                }
        //下载
        requireView().findViewById<TextView>(R.id.item_tv_playinfo_download)
                .setOnClickListener {
                    if (MMkvUtils.Builds().loadStartBean("")?.ads?.download?.status != 0) {
                        if (!LoginUtils.checkLogin2(activity)) {
                            HitDialog(context!!).setTitle("提示").setMessage("需登录后开通VIP才可下载，确定登录。").setOnHitDialogClickListener(object : OnHitDialogClickListener() {
                                override fun onCancelClick(dialog: HitDialog) {
                                    super.onCancelClick(dialog)
                                }

                                override fun onOkClick(dialog: HitDialog) {
                                    super.onOkClick(dialog)
                                    ActivityUtils.startActivity(LoginActivity::class.java)
                                }
                            }).show()
                        } else {
                            if (LoginUtils.checkVIP(activity, "下载需要开通vip是否去开通")) {
                                startCache()
                            }
                        }
                    } else {
                        startCache()
                    }


                }

        requireView().findViewById<TextView>(R.id.item_tv_playinfo_feedback)

                .setOnClickListener {
                   // ActivityUtils.startActivity(FeedbackActivity::class.java)
                    //val message = "视频《${mVodBean.vod_name}》播放失败,播放源:${playActivity.playFrom.player_info.show},地址${playActivity.playList!![urlIndex].url}"
                    val message = "视频《${mVodBean.vod_name}》播放失败\n播放源：${playActivity.playFrom.player_info.show}\n视频序列：${playActivity.playList!![urlIndex].name}\n请及时修复！"
                    FeedbackActivity.start(mActivity, message)
                //                    EventBus.getDefault().post("画中画")
                }
        requireView().findViewById<TextView>(R.id.item_tv_playinfo_share)
                .setOnClickListener {
                    if (!UserUtils.isLogin()) {
                        ActivityUtils.startActivity(LoginActivity::class.java)
                    } else {
                        var intn = Intent(this.mActivity, ShareActivity::class.java)
                        intn.putExtra("vom_name", mVodBean.vodName)
                        intn.putExtra("vod_pic", mVodBean.vodPic) //图片
                        Log.e("wqddg", mVodBean.toString());
                        intn.putExtra("vod_class_typeName", mVodBean.type.typeName)
                        intn.putExtra("vod_class_year", mVodBean.vod_year) //标签
                        intn.putExtra("vod_class_area", mVodBean.vod_area)
                        intn.putExtra("vod_class_class", mVodBean.vod_class.replace(",", " / "))
                        intn.putExtra("vod_blurd", mVodBean.vodBlurb) //注释
                        startActivity(intn)
                    }
                }


        tvLastest.setOnClickListener {
            playActivity.showPlayList()
        }
        requireView().findViewById<LinearLayout>(R.id.iv_go_vipm)
                .setOnClickListener {
                    if (!isLogin()) {
                        LoginActivity.start()
                    } else {
                        val intent = Intent(activity, PayActivity::class.java)
                        intent.putExtra("type", 1)
                        ActivityUtils.startActivity(intent)
                    }
                }
        ivLastest.setOnClickListener {
            playActivity.showPlayList()
        }


        title.text = mVodBean.vod_name



        year.text = mVodBean.vod_year
        area.text = mVodBean.vod_area
        type.text = mVodBean.type.typeName
        score.text = mVodBean.vod_score + "分"

        sortVodView.text = mVodBean.vod_class.replace(",", " / ")
        intro.setOnClickListener {

            playActivity.showSummary()
        }

        if (mVodBean.vodRemarks.isNotEmpty()) {
            tvLastest.text = mVodBean.vodRemarks//选集
            ivLastest.visibility = View.VISIBLE
        } else {
            ivLastest.visibility = View.GONE
        }
        if (mVodBean.type.typeName.equals("电影")) {
            sortVodTypePic.setBackgroundResource(R.drawable.playinfo_dypic);
        } else if (mVodBean.type.typeName.equals("剧集")) {
            sortVodTypePic.setBackgroundResource(R.drawable.playinfo_jjpic);
        } else if (mVodBean.type.typeName.equals("综艺")) {
            sortVodTypePic.setBackgroundResource(R.drawable.playinfo_zypic);
        } else if (mVodBean.type.typeName.equals("动漫")) {
            sortVodTypePic.setBackgroundResource(R.drawable.playinfo_dmpic);
        } else if (mVodBean.type.typeName.equals("B站")) {
            sortVodTypePic.setBackgroundResource(R.drawable.playinfo_bilipic);
        } else {
            sortVodTypePic.setBackgroundResource(R.drawable.playinfo_jlppic);
        }
//        vod_play_list.cl
        vod_play_list = mVodBean.vod_play_list
        rvLastest.layoutManager = LinearLayoutManager(mActivity).apply {
            orientation = LinearLayoutManager.HORIZONTAL

        }
        rvLastest.adapter = selectionAdapter


        if (vod_play_list.isNotEmpty()) {
            selectionAdapter.data.clear()
            tlPlaySource.removeAllTabs()
            for (i in vod_play_list.indices) {
                val playFromBean = vod_play_list[i]
                val playerInfo = playFromBean.player_info
                val urls = playFromBean.urls

                var playSource = playerInfo.show
                if (StringUtils.isEmpty(playSource)) {
                    playSource = "默认"
                }
                if (i == playSourceIndex) {
                    selectionAdapter.addData(urls)
                }

                val tab = tlPlaySource.newTab().setText(playSource)
                tlPlaySource.addTab(tab)
            }
        }

        tlPlaySource.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
                Log.d("", "")
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                Log.d("", "")
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {

                val playFromBean = vod_play_list[tlPlaySource.selectedTabPosition]

                selectionAdapter.setNewData(playFromBean.urls)

                playActivity.changePlaySource(playFromBean, tlPlaySource.selectedTabPosition)
            }

        })

        tlPlaySource.getTabAt(playSourceIndex)?.select()
        scrollCurIndex(rvLastest)


        val rvRecommand = requireView().findViewById<RecyclerView>(R.id.rvRecommand)
        val tvChange = requireView().findViewById<TextView>(R.id.tvChange)

        val tvSameType = requireView().findViewById<TextView>(R.id.tvSameType)
        val tvSameActor = requireView().findViewById<TextView>(R.id.tvSameActor)
        val dividerItemDecoration = MyDividerItemDecoration(mActivity, RecyclerView.HORIZONTAL, false)
        dividerItemDecoration.setDrawable(mActivity.resources.getDrawable(R.drawable.divider_image))
        rvRecommand.addItemDecoration(dividerItemDecoration)
        rvRecommand.layoutManager = GridLayoutManager(mActivity, 3, RecyclerView.VERTICAL, false).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return 6
                }
            }
        }
        rvRecommand.adapter = recommendAdapter
        tvChange.setOnClickListener {
            when (curType) {
                0 -> getSameTypeData()
                1 -> getSameActorData()
            }
        }
        tvSameType.setOnClickListener {
            if (curType != 0) {
                curType = 0
                tvSameType.setTextColor(ColorUtils.getColor(R.color.colorPrimary))
                tvSameActor.setTextColor(ColorUtils.getColor(R.color.white))

                getSameTypeData()

            }
        }

        checkOrder.setOnClickListener { _->
                if (mVodBean.type_id==29){
                    if (AdsWatchUtils.needShowAds(activity)) {
                        AdUtils.rewardTVVideo(activity, object : AdListener {
                            override fun onShow() {}
                            override fun onClose() {
                                if (reword) {
                                    reword = false
                                    revertPlayList(checkOrder)
                                }
                            }

                            override fun reword() {
                                reword = true
                                var tvNum = SPUtils.getInt(activity, DJ_NUM, 0)
                                tvNum++
                                SPUtils.setInt(activity, DJ_NUM, tvNum)
                            }
                        })
                    } else {
                        var tvNum = SPUtils.getInt(activity, DJ_NUM, 0)
                        tvNum++
                        SPUtils.setInt(activity, DJ_NUM, tvNum)
                        reword = false
                        revertPlayList(checkOrder)
                    }
                }else if (mVodBean.type_id==2){

                    //电视剧第一集不看
                    if (AdsWatchUtils.needShowAds(activity)) {
                        AdUtils.rewardTVVideo(activity, object : AdListener {
                            override fun onShow() {}
                            override fun onClose() {
                                if (reword) {
                                    reword = false
                                    revertPlayList(checkOrder)
                                }
                            }

                            override fun reword() {
                                reword = true
                                var tvNum = SPUtils.getInt(activity, TV_NUM, 0)
                                tvNum++
                                SPUtils.setInt(activity, TV_NUM, tvNum)
                            }
                        })
                    } else {
                        var tvNum = SPUtils.getInt(activity, TV_NUM, 0)
                        tvNum++
                        SPUtils.setInt(activity, TV_NUM, tvNum)
                        reword = false
                        revertPlayList(checkOrder)
                    }
                }else{
                    //非电视剧，都要看激励广告
                    AdUtils.rewardVideo(activity,object :AdListener{
                        override fun onShow() {

                        }

                        override fun onClose() {
                            if (reword){
                                reword = false
                                revertPlayList(checkOrder)
                            }
                        }
                        override fun reword() {
                            reword = true
                        }
                    })
                }


        }

        tvSameActor.setOnClickListener {
            if (curType != 1) {
                curType = 1
                tvSameType.setTextColor(ColorUtils.getColor(R.color.white))
                tvSameActor.setTextColor(ColorUtils.getColor(R.color.colorPrimary))

                getSameActorData()
            }
        }

      /*  rlComment.setOnClickListener {
            if (UserUtils.isLogin()) {

                CommentDialog(mActivity, "讨论")
                        .setOnCommentSubmitClickListener(object : CommentDialog.OnCommentSubmitClickListener {
                            override fun onCommentSubmit(comment: String) {
                                commitComment(comment)
                            }
                        })
                        .show()
            } else {
                LoginActivity.start()
            }

        }
        Log.d("hhhh1", "嘿嘿${selectionAdapter.data.size}")*/
    }

    private fun revertPlayList(checkOrder: CheckBox) {
        mVodBean.vod_play_list.forEachIndexed { index, playFromBean ->
            playFromBean.urls.reverse()
        }
        selectionAdapter.setNewData(mVodBean.vod_play_list[playActivity.playSourceIndex].urls)
        checkOrder.isChecked = !checkOrder.isChecked
        isOrder = checkOrder.isChecked
        playActivity.changeSelection(urlIndex, true)
    }

    private fun sendDanmu(content: String, str: String) { //从com.ruifenglb.www.ui.play.NewPlayActivity复制过来的
        if (content.isEmpty()) { //com.ruifenglb.av.play.AvVideoController下有个showDanmaku()方法可以拉起原来的弹幕输入框
            ToastUtils.showShort("请输入弹幕！")
            return
        }
        val vodService = Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return
        }
        //上传弹幕所需参数？
        Log.d("弹幕内容", "onSuccess: $content")
        Log.d("视频ID", "onSuccess: " + mVodBean.vod_id.toString())
        Log.d("这个是啥", "onSuccess: " + System.currentTimeMillis().toString())

        RequestManager.execute(  //这个函数可能有问题
                mActivity,
                vodService.sendDanmu(content, mVodBean.vod_id.toString(), str,str),
                object : BaseObserver<GetScoreBean>() {
                    override fun onSuccess(data: GetScoreBean) {
                        if (data.score != "0") {
                            Utils.runOnUiThread {
                                ToastUtils.showShort("发送弹幕成功，获得${data.score}积分")
                            }
                        }
                    }

                    override fun onError(e: ResponseException) {
                        Utils.runOnUiThread {
                            ToastUtils.showShort(e.getErrorMessage())
                        }
                    }
                })
    }

    private fun scrollCurIndex(rvLastest: RecyclerView) {

        rvLastest.smoothScrollToPosition(urlIndex)
        val mLayoutManager = rvLastest.layoutManager as LinearLayoutManager
        mLayoutManager.scrollToPositionWithOffset(urlIndex, 0)


    }

    private fun collection() {
        val vodService = Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return
        }

        RequestManager.execute(this,
                vodService.collect(1.toString(), mVodBean.vod_id.toString(), 2.toString()),
                object : BaseObserver<String>() {
                    override fun onSuccess(data: String) {
                        ToastUtils.showShort("已关注")
                        item_tv_playinfo_collect.setBackgroundResource(R.drawable.yizui);
                        isCollected = true
                        requireView().findViewById<TextView>(R.id.item_tv_playinfo_collect)

                    }

                    override fun onError(e: ResponseException) {

                    }

                })
    }

    private fun uncollection() {
        val vodService = Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return
        }
        RequestManager.execute(this,
                vodService.deleteCollect(mVodBean.vod_id.toString(), 2.toString()),
                object : BaseObserver<String>() {
                    override fun onSuccess(data: String) {
                        ToastUtils.showShort("取消成功")
                        item_tv_playinfo_collect.setBackgroundResource(R.drawable.zui);
                        isCollected = false
                        requireView().findViewById<TextView>(R.id.item_tv_playinfo_collect)
                    }

                    override fun onError(e: ResponseException) {
                    }

                }
        )
    }

    private fun getCollectionState() {
        if (UserUtils.isLogin()) {
            val vodService = Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
            if (AgainstCheatUtil.showWarn(vodService)) {
                return
            }
            RequestManager.execute(this,
                    vodService.getCollectList(1.toString(), 100.toString(), 2.toString()),
                    object : BaseObserver<Page<CollectionBean>>() {
                        override fun onSuccess(data: Page<CollectionBean>) {
                            for (bean in data.list) {
                                if (bean.data.id == mVodBean.vod_id) {
                                    isCollected = true
                                    break
                                }
                            }
                            if (isCollected) {

                                item_tv_playinfo_collect.setBackgroundResource(R.drawable.yizui);

                                requireView().findViewById<TextView>(R.id.item_tv_playinfo_collect)

                            } else {

                                item_tv_playinfo_collect.setBackgroundResource(R.drawable.zui);

                                requireView().findViewById<TextView>(R.id.item_tv_playinfo_collect)

                            }

                        }

                        override fun onError(e: ResponseException) {

                        }

                    })
        }
    }

    private fun score() {
        val vodService = Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return
        }
        ScoreDialog(mActivity)
                .setOnScoreSubmitClickListener(object : ScoreDialog.OnScoreSubmitClickListener {
                    override fun onScoreSubmit(scoreDialog: ScoreDialog, score: Float) {
                        if (score == 0f) {
                            ToastUtils.showShort("评分不能为空!")
                        } else {
                            scoreDialog.dismiss()
                            val vodService = Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
                            if (AgainstCheatUtil.showWarn(vodService)) {
                                return
                            }
                            RequestManager.execute(
                                    this@VideoDetailFragment,
                                    vodService.score(mVodBean.vod_id.toString(), score.toString()),
                                    object : BaseObserver<GetScoreBean>() {
                                        override fun onSuccess(data: GetScoreBean) {
                                            if (data.score != "0") {
                                                ToastUtils.showShort("评分成功，获得${data.score}积分")
                                            }
                                        }

                                        override fun onError(e: ResponseException) {
                                        }
                                    }
                            )
                        }
                    }
                })
                .show()
    }

    private var lastCommentTimestamp: Long = 0L // 全局变量或类成员变量

   /* private fun commitComment(commentContent: String) {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastCommentTimestamp < 180_000) { // 检查是否在1分钟内已提交过评论
            ToastUtils.showShort("请勿在三分钟内重复发送评论")
            return
        }

        lastCommentTimestamp = currentTime // 更新最后一次评论时间戳

        val vodService = Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return;
        }

        RequestManager.execute(this,
                vodService.comment(commentContent, 1.toString(), mVodBean.vod_id.toString()),
                object : BaseObserver<GetScoreBean>() {
                    override fun onSuccess(data: GetScoreBean) {
                        if (data.score == "0") {
                            ToastUtils.showShort("评论成功")
                        } else {
                            ToastUtils.showShort("评论成功,获得${data.score}积分")
                        }
                        curCommentPage = 1
                        getCommentList(true)
                    }

                    override fun onError(e: ResponseException) {
                        // 这里可以添加错误处理逻辑，例如重新设定 lastCommentTimestamp 为 currentTime，以便允许用户再次尝试评论
                        lastCommentTimestamp = currentTime
                        // 显示错误信息
                        ToastUtils.showShort("评论提交失败，请稍后再试")
                    }

                })
    }*/

    private fun replayComment(commentContent: String, commentId: String, commentPid: String) {
        val vodService = Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return
        }
        RequestManager.execute(this,
                vodService.replayComment(commentContent, 1.toString(), mVodBean.vod_id.toString(), commentId, commentPid),
                object : BaseObserver<String>() {
                    override fun onSuccess(data: String) {

                    }

                    override fun onError(e: ResponseException) {

                    }

                })
    }


    private fun getSameTypeData() {
        val vodService = Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return
        }

        RequestManager.execute(this,
                vodService.getSameTypeList(mVodBean.type_id, mVodBean.vod_class, curSameTypePage, 9),
                object : BaseObserver<Page<VodBean>>() {
                    override fun onSuccess(data: Page<VodBean>) {
                        if (data.list.isNotEmpty()) {
                            curSameTypePage++
                            recommendAdapter.setNewData(data.list)
                        }
                    }

                    override fun onError(e: ResponseException) {
                    }

                })
    }

    private fun getSameActorData() {
        val vodService = Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return
        }
        RequestManager.execute(this,
                vodService.getSameActorList(mVodBean.type_id, mVodBean.vod_actor, curSameActorPage, 9),
                object : BaseObserver<Page<VodBean>>() {
                    override fun onSuccess(data: Page<VodBean>) {
                        if (data.list.isNotEmpty()) {
                            recommendAdapter.setNewData(data.list)
                            curSameActorPage++
                        }
                    }

                    override fun onError(e: ResponseException) {

                    }

                })
    }

    fun changePlaysource(playSourceIndex: Int) {
        tlPlaySource.getTabAt(playSourceIndex)?.select()

    }


    private class RecommendAdapter : BaseQuickAdapter<VodBean, BaseViewHolder>(R.layout.item_card_recommend_child) {

        override fun convert(helper: BaseViewHolder, item: VodBean) {
            //新加积分显示
            //新加积分显示
            if (item.vod_points_play == 0) {
                helper.getView<View>(R.id.item_tv_card_child_tip).visibility = View.GONE
            } else {
                helper.getView<View>(R.id.item_tv_card_child_tip).visibility = View.VISIBLE
                helper.setText(R.id.item_tv_card_child_tip, item.vod_points_play.toString() + "积分")
            }
            //新加积分显示
            helper.setText(R.id.item_tv_card_child_title, item.vodName)
            helper.setText(R.id.item_tv_card_child_up_title, item.vodRemarks)
            val img = item.vod_pic
            helper.setText(R.id.item_tv_card_child_vod_blurb, item.vodBlurb)
            val icon = helper.getView<ImageView>(R.id.item_iv_card_child_icon)
            val lp = icon.layoutParams
            val perWidth = (getScreenWidth(LitePalApplication.getContext()) - dp2px(LitePalApplication.getContext(), 4f)) / 3
            lp.height = (perWidth * 1.4f).toInt()
            icon.layoutParams = lp

            val multiTransformation = MultiTransformation(CenterCrop(), RoundedCornersTransformation(7, 0, RoundedCornersTransformation.CornerType.ALL))

            Glide.with(helper.itemView.context)
                    .load(img)
                    .thumbnail(1.0f)
                    .apply(RequestOptions.bitmapTransform(multiTransformation))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(icon)
        }
    }

    inner class SelectionAdapter : BaseQuickAdapter<UrlBean, BaseViewHolder>(R.layout.item_video_source) {

        val rvLastestm = requireView().findViewById<LinearLayout>(R.id.rvLastestm)

        override fun convert(helper: BaseViewHolder, item: UrlBean) {

            if (mVodBean.type_id == 3) {
                helper.itemView.layoutParams = helper.itemView.layoutParams.apply {
                    width = ViewGroup.MarginLayoutParams.WRAP_CONTENT //ConvertUtils.dp2px(130f)
                    height = ConvertUtils.dp2px(50f)
                }
            } else {
                helper.itemView.layoutParams = helper.itemView.layoutParams.apply {
                    width = ViewGroup.MarginLayoutParams.WRAP_CONTENT//ConvertUtils.dp2px(50f)
                    height = ConvertUtils.dp2px(50f)
                }
            }

            val position = helper.layoutPosition
//            if (isOrder) {

            if (position == urlIndex) {
                helper.setTextColor(R.id.tv, ColorUtils.getColor(R.color.textColor7))
                helper.getView<TextView>(R.id.tv).setBackgroundResource(R.drawable.bg_video_source_bg1);
            } else {
                helper.setTextColor(R.id.tv, ColorUtils.getColor(R.color.gray_999))
                helper.getView<TextView>(R.id.tv).setBackgroundResource(R.drawable.bg_video_source1);
            }
            if (selectionAdapter.data.size > 5) {
                rvLastestm.visibility = View.VISIBLE
            } else {
                rvLastestm.visibility = View.GONE

            }

//            } else {
//                var indexPostion = 0
//                if (urlIndex == 0) {
//                    indexPostion = urlIndex
//                } else {
//                    indexPostion = selectionAdapter.data.size - 1 - urlIndex
//                }
//                if (position == indexPostion) {
//                    helper.setTextColor(R.id.tv, ColorUtils.getColor(R.color.userTopBg))
//                } else {
//                    helper.setTextColor(R.id.tv, ColorUtils.getColor(R.color.gray_999))
//                }
//            }
            val name = item.name.replace("第", "").replace("集", "")
            helper.setText(R.id.tv, name)

        }
    }

    companion object {
        const val VOD_BEAN = "vodBean"

        const val URL_INDEX = "urlIndex"

        const val PLAY_SOURCE_INDEX = "playInfoIndex"

        fun newInstance(vodBean: VodBean, urlIndex: Int, playSourceIndex: Int): VideoDetailFragment = VideoDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable(VOD_BEAN, vodBean)
                putInt(URL_INDEX, urlIndex)
                putInt(PLAY_SOURCE_INDEX, playSourceIndex)
            }
        }
    }


    private fun startCache() {
        val bottomSheetDialog = activity?.let {

            BottomSheetDialog(it)
        }
        val view: View = LayoutInflater.from(activity).inflate(R.layout.cache_all_list_layout, null)
        bottomSheetDialog?.setContentView(view)
        bottomSheetDialog?.window?.findViewById<View>(R.id.design_bottom_sheet)?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        if (vod_play_list.isEmpty()) {
//            Toast.makeText(activity, "正在请求数据，请稍后", Toast.LENGTH_SHORT).show()
//            return
//        }

        val cacheItem: ArrayList<Square> = ArrayList()
        if (vod_play_list.isNotEmpty()) {
            val playInfoBean = playActivity.playFrom

            val urlS = playInfoBean.urls
            if (urlS.isNotEmpty()) {
                for (index in urlS.indices) {
                    val urlBean = urlS[index]
                    val square = Square(urlBean.name) {
                        val downloadTitle = "${mVodBean.vodName}\t${urlBean.name}"
                        Log.e("TAG", "" + urlBean.url)
                        if (urlBean.url.contains(".m3u8?") ||urlBean.url.endsWith(".m3u8") ) {
                            val imageView = it.findViewById<ImageView>(R.id.status_tag)
                            imageView.setVisibility(View.VISIBLE)
                            imageView.setImageResource(R.drawable.ic_cache_down)
                            Toast.makeText(activity, "开始缓存第${urlBean.name}集", Toast.LENGTH_SHORT).show()
                            // 三个参数 下载地址 标题  封面图片
                            DownloadPresenter.addM3u8Task(activity, urlBean.url, downloadTitle, mVodBean.vod_pic)
                        } else {
                            var iscache = false;
                            if (!isParse) {
                                it.isSelected = true
                                val imageView = it.findViewById<ImageView>(R.id.status_tag)
                                imageView.setVisibility(View.VISIBLE)
                                imageView.setImageResource(R.drawable.ic_cache_down)
                                isParse = true

                                // 链接转换
                                val parse = playInfoBean.player_info.parse2
                                JieXiUtils2.INSTANCE.getPlayUrl(parse, urlBean.url, curParseIndex, object : BackListener {
                                    override fun onSuccess(url: String, curParseIndex: Int) {
                                        isParse = false
                                        LogUtils.eTag("TAG", "onSuccess: curParseIndex =  $curParseIndex url=${url}")
                                        url.let {
                                            if (url.endsWith(".m3u8") || url.contains(".m3u8?")) {
                                                if (!iscache) {
                                                    iscache = true
                                                    Toast.makeText(activity, "开始缓存${urlBean.name}", Toast.LENGTH_SHORT).show()
                                                    // 三个参数 下载地址 标题  封面图片
                                                    DownloadPresenter.addM3u8Task(activity, it, downloadTitle, mVodBean.vod_pic)
                                                }

                                            } else {
                                                ToastUtils.showLong("当前线路不支持缓存...")
                                            }
                                        }

                                    }

                                    override fun onError() {
                                        isParse = false
                                        ToastUtils.showLong("解析失败，请尝试切换线路缓存")
                                    }

                                    override fun onProgressUpdate(msg: String?) {

                                    }
                                }, curFailIndex)
                            } else {
                                ToastUtils.showLong("请等待上一个解析完在缓存")
                            }
                        }
                    }

                    square.isSelected = false
                    square.finished = false
                    val info: List<M3u8DownloadingInfo> = DownloadPresenter.getM3u8DownLoading(urlS[index].url)
                    if (info.isNotEmpty()) {
                        //正在下载中
                        square.isSelected = true
                    }
                    val doneInfos: List<M3u8DoneInfo> = DownloadPresenter.getM3u8Done(urlS[index].url)
                    if (doneInfos.isNotEmpty()) {
                        //已下载完成
                        square.isSelected = false
                        square.finished = true
                    }
                    cacheItem.add(square)
                }
                val selectedSet = TreeSet<Int>()
                val multiTypeAdapter = MultiTypeAdapter()
                multiTypeAdapter.register(Square::class.java, SquareViewBinder(selectedSet))
                val cacheItems = ArrayList<Any?>()
                cacheItems.addAll(cacheItem)
                multiTypeAdapter.items = cacheItems
                val allList: RecyclerView = view.findViewById(R.id.all_list)
                val title = view.findViewById<TextView>(R.id.title)
                // 查看下载
                val downCenter = view.findViewById<TextView>(R.id.down_center)
                downCenter.setOnClickListener {
                    //进入下载界面
                    activity?.let { it1 -> AllDownloadActivity.start(it1) }
                    bottomSheetDialog?.dismiss()
                }
                title.text = "缓存剧集"
                val close = view.findViewById<ImageView>(R.id.close)
                val gridLayoutManager = GridLayoutManager(activity, 3)
                gridLayoutManager.orientation = GridLayoutManager.VERTICAL
                allList.addItemDecoration(GridItemDecoration(activity, R.drawable.grid_item_decor))
                allList.layoutManager = gridLayoutManager
                allList.adapter = multiTypeAdapter
                bottomSheetDialog?.show()
                close.setOnClickListener {
                    bottomSheetDialog?.dismiss()
                }
            }
        }
    }

}
