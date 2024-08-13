package com.ruifenglb.www.ui.play


import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ad.biddingsdk.AdListener
import com.app.ad.biddingsdk.AdUtils
import com.ruifenglb.www.R
import com.ruifenglb.www.bean.UrlBean
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.SnackbarUtils.dismiss
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.github.StormWyrm.wanandroid.base.fragment.BaseFragment
import com.ruifenglb.www.banner.Data
import com.ruifenglb.www.download.SPUtils
import com.ruifenglb.www.ui.widget.AdsMindDialog
import com.ruifenglb.www.utils.AdsWatchUtils
import kotlinx.android.synthetic.main.fragment_play_list.*
import kotlinx.android.synthetic.main.fragment_user.*

class PlayListFragment : BaseFragment() {
    private var spanCount = 2
    private var urlIndex = 0
    private lateinit var playActivity: NewPlayActivity
    private var reword = false
    private val selectionAdapter: SelectionAdapter by lazy {
        SelectionAdapter().apply {
            setOnItemClickListener { adapter, view, position ->
                if (urlIndex != position) {

                        if (playActivity.mVodBean.type_id==29){
                            if (AdsWatchUtils.needShowDJAds(position)) {
                                AdsMindDialog(requireActivity()){
                                    AdUtils.getInstance().rewardVideo(activity, object : AdListener {
                                        override fun onShow() {}
                                        override fun onClose() {
                                            if (reword) {
                                                reword = false
                                                urlIndex = position
                                                playActivity.changeSelection(position,false)
                                                playActivity.changeVideoUrlIndex(position)
                                                notifyDataSetChanged()
                                                dismiss()
                                            }
                                        }

                                        override fun reword(b:Boolean) {
                                            reword = true


                                        }
                                    })

                                }.show()
                            } else {
                                reword = false
                                urlIndex = position
                                playActivity.changeSelection(position,false)
                                playActivity.changeVideoUrlIndex(position)
                                notifyDataSetChanged()
                                dismiss()
                            }
                        }else if (playActivity.mVodBean.type_id==2){

                            //电视剧第一集不看
                            if (AdsWatchUtils.needShowAds(position)) {
                                AdsMindDialog(requireActivity()){
                                    AdUtils.getInstance().rewardTVVideo(activity, object : AdListener {
                                        override fun onShow() {}
                                        override fun onClose() {
                                            if (reword) {
                                                reword = false
                                                urlIndex = position
                                                playActivity.changeSelection(position,false)
                                                playActivity.changeVideoUrlIndex(position)
                                                notifyDataSetChanged()
                                                dismiss()
                                            }
                                        }

                                        override fun reword(b:Boolean) {
                                            reword = true

                                        }
                                    })


                                }.show()
                            } else {
                                reword = false
                                urlIndex = position
                                playActivity.changeSelection(position,false)
                                playActivity.changeVideoUrlIndex(position)
                                notifyDataSetChanged()
                                dismiss()
                            }
                        }else{
                            //非电视剧，都要看激励广告
                            AdsMindDialog(requireActivity()){
                                AdUtils.getInstance().rewardVideo(activity,object : AdListener {
                                    override fun onShow() {

                                    }

                                    override fun onClose() {
                                        if (reword){
                                            reword = false
                                            urlIndex = position
                                            playActivity.changeSelection(position,false)
                                            playActivity.changeVideoUrlIndex(position)
                                            notifyDataSetChanged()
                                            dismiss()
                                        }
                                    }
                                    override fun reword(b:Boolean) {
                                        reword = true
                                    }
                                })
                            }.show()
                        }
                }
            }


        }

    }

    override fun getLayoutId(): Int {

        return R.layout.fragment_play_list

    }

    override fun initView() {
        super.initView()
        ///////////////////////QQ7242484///////////////////////////////////////
        if (Data.getQQ() == "暗夜紫") {

            bj11.setBackgroundColor(ColorUtils.getColor(R.color.xkh))

        }
        if (Data.getQQ() == "原始蓝") {

            bj11.setBackgroundColor(ColorUtils.getColor(R.color.white))

        }
        //////////////////////QQ7242484///////////////////////////////////////
        playActivity = mActivity as NewPlayActivity

        arguments?.run {
            spanCount = getInt("SPAN_COUNT")
        }

        rvSelectWorks.layoutManager = GridLayoutManager(mActivity, spanCount, RecyclerView.VERTICAL, false).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return spanCount

                }
            }
        }

        rvSelectWorks.adapter = selectionAdapter

    }

    override fun initListener() {
        super.initListener()
        ivClose.setOnClickListener {
            playActivity.hidePlayList()
           // playActivity.showVideoDetail()
        }
    }

    fun showPlayList(playList : List<UrlBean>,urlIndex : Int){
        this.urlIndex = urlIndex

        selectionAdapter.setNewData(playList)

    }


    companion object {
        @JvmStatic
        fun newInstance(spanCount: Int): PlayListFragment = PlayListFragment().apply {
            arguments = Bundle().apply {
                putInt("SPAN_COUNT", spanCount)
            }
        }
    }

    inner class SelectionAdapter : BaseQuickAdapter<UrlBean, BaseViewHolder>(R.layout.item_play_list) {

        override fun convert(helper: BaseViewHolder, item: UrlBean) {
            if(spanCount == 2){
                helper.itemView.layoutParams =  helper.itemView.layoutParams.apply {
                    width = ConvertUtils.dp2px(130f)
                    height = ConvertUtils.dp2px(50f)
                }
            }else{
                helper.itemView.layoutParams =  helper.itemView.layoutParams.apply {
                    width = ConvertUtils.dp2px(50f)
                    height = ConvertUtils.dp2px(50f)
                }
            }
            val position = helper.layoutPosition

            if(position == urlIndex){
                helper.setTextColor(R.id.tv, ColorUtils.getColor(R.color.userTopBg))
                ///////////////////////QQ7242484///////////////////////////////////////
                if (Data.getQQ() == "暗夜紫") {
                    helper.setBackgroundRes(R.id.tv, R.drawable.bg_video_sourceahz)
                }
                if (Data.getQQ() == "原始蓝") {
                    helper.setBackgroundRes(R.id.tv, R.drawable.bg_video_source)

                }
                //////////////////////QQ7242484///////////////////////////////////////
            }else{
                helper.setTextColor(R.id.tv, ColorUtils.getColor(R.color.gray_999))
                ///////////////////////QQ7242484///////////////////////////////////////
                if (Data.getQQ() == "暗夜紫") {
                    helper.setBackgroundRes(R.id.tv, R.drawable.bg_video_sourceahz)
                }
                if (Data.getQQ() == "原始蓝") {
                    helper.setBackgroundRes(R.id.tv, R.drawable.bg_video_source)
                }
                //////////////////////QQ7242484///////////////////////////////////////
            }

            var name = item.name.replace("第","").replace("集","")
            helper.setText(R.id.tv, name)

        }
    }
}
