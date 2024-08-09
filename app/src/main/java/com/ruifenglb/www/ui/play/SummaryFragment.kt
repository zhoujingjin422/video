package com.ruifenglb.www.ui.play

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ColorUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.github.StormWyrm.wanandroid.base.fragment.BaseFragment
import com.ruifenglb.www.R
import com.ruifenglb.www.bean.VodBean
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_summary.*

class SummaryFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_summary
    }

    private lateinit var playActivity : NewPlayActivity

    @SuppressLint("SetTextI18n")
    override fun initView() {
        super.initView()
        playActivity = mActivity as NewPlayActivity
        val vodBean = arguments?.getParcelable(VOD_BEAN) as? VodBean
        vodBean?.run {
            tvTitle.text = vodName
//            tvYear.text = "年代：${vod_year}.${type.typeName}.${vod_area}"
            Desc_Vod_class.text = vod_year + " / " + vod_area.replace(",", " / ") + " / " + vod_class.replace(",", " / ")
            if(!vod_actor.isEmpty()) {
                if(type.typeName.equals("电影") || type.typeName.equals("电视剧")) {
                    tvActor.text = "演员：" + vod_actor.replace(",", " / ")
                }else if(type.typeName.equals("综艺")){
                    tvActor.text = "嘉宾：" + vod_actor.replace(",", " / ")
                }else{
                    //动漫 少儿 纪录片过滤
                    tvActor.visibility = View.GONE
                }
            }else{
                tvActor.visibility = View.GONE
            }
//            tvType.text = "类型：" + vod_class
            if(type.typeName != "电影" || type.type_id != 1) {
                tvStatus.text = "状态：$vodRemarks"
            }else{
                tvStatus.visibility = View.GONE
            }
            tvPlayNumber.text = "" + vod_hits
            if(vod_score == "0" || vod_score == "0.0" || vod_score == null ){
                tvScore_not.visibility = View.VISIBLE
                tvScore_Score.visibility = View.GONE
                tvScore.visibility = View.GONE
            }else{
                tvScore_not.visibility = View.GONE
                tvScore.text = vod_score
            }
            if(vod_blurb == null || vod_blurb == ""){
                tvSummary.text = "暂无详细介绍";
            }else {
                tvSummary.text = vod_blurb
            }
            val mation: MultiTransformation<Bitmap> = MultiTransformation(CenterCrop(), RoundedCornersTransformation(15, 0, RoundedCornersTransformation.CornerType.ALL))
            Glide.with(mActivity)
                    .load(vod_pic)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(RequestOptions.bitmapTransform(mation))
                    .into(Desc_Vod_Pic)
            ivCloseIntro.setOnClickListener {
                playActivity.hideSummary()
              //  playActivity.showVideoDetail()
            }
        }
    }

    companion object {
        const val VOD_BEAN = "vodBean"

        @JvmStatic
        fun newInstance(vodBean: VodBean): SummaryFragment = SummaryFragment().apply {
            arguments = Bundle().apply {
                putParcelable(VOD_BEAN, vodBean)
            }
        }
    }
}
