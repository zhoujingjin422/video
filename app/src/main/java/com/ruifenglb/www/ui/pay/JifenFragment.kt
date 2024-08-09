package com.ruifenglb.www.ui.pay

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.ruifenglb.www.ApiConfig
import com.ruifenglb.www.R
import com.ruifenglb.www.bean.*
import com.ruifenglb.www.netservice.VodService
import com.ruifenglb.www.ui.browser.BrowserActivity
import com.ruifenglb.www.ui.widget.HitDialog
import com.ruifenglb.www.ui.widget.PayDialog
import com.ruifenglb.www.utils.AgainstCheatUtil
import com.ruifenglb.www.utils.MMkvUtils
import com.ruifenglb.www.utils.Retrofit2Utils
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ToastUtils
import com.ruifenglb.www.utils.UserUtils
import com.github.StormWyrm.wanandroid.base.exception.ResponseException
import com.github.StormWyrm.wanandroid.base.fragment.BaseFragment
import com.github.StormWyrm.wanandroid.base.net.RequestManager
import com.github.StormWyrm.wanandroid.base.net.observer.BaseObserver
import com.ruifenglb.www.base.observer.LoadingObserver
import com.github.StormWyrm.wanandroid.base.sheduler.IoMainScheduler
import com.ruifenglb.www.utils.MMkvUtils.Companion.Builds
import com.ruifenglb.www.banner.Data
import jaygoo.library.m3u8downloader.M3U8Library
import kotlinx.android.synthetic.main.fragment_jifen.*

import org.greenrobot.eventbus.EventBus
import java.text.DecimalFormat
import java.util.*
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_jifen.selectedPackagePrice
import kotlinx.android.synthetic.main.fragment_jifen.tvHit
import kotlinx.android.synthetic.main.fragment_pay.*
import kotlinx.android.synthetic.main.fragment_vipuser.*

class JifenFragment : BaseFragment() {

    private var orderCode: String? = null
    private var packaged: String = ""

    override fun getLayoutId(): Int {
        return R.layout.fragment_jifen
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private var selectedPackage = "" // 用于记录当前选中的套餐类型

    override fun initListener() {
        super.initListener()

        tvCard.setOnClickListener {
            cardBuy()
        }





    }
    private fun setSelectedPackagePriceText(price: String) {
        selectedPackagePrice.text = price
    }

    // 更新按钮背景颜色并设置选中套餐
    private fun updateButtonBackgrounds(selectedDrawableResId: Int, packageType: String) {

        selectedPackage = packageType

    }

    override fun initLoad() {
        super.initLoad()
        getScoreList()
        getPayTip()
        val startBean = Objects.requireNonNull(Builds().loadStartBean(""))
        if (startBean?.online_recharge == null || startBean.online_recharge == "0") { //关闭在线充值
            iv_play_s.visibility = View.GONE
        } else {
            iv_play_s.visibility = View.VISIBLE
        }


        val ad = Builds().loadStartBean("")?.ads?.rechargeentrykami
        if (!ad?.description.isNullOrEmpty()) {
            Rechargeentrykami.text = ad?.description
        }





    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PAY) {
            checkOrder()
        }
    }

    private fun getPayTip() {
        val vodService= Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return
        }
        vodService.payTip()
                .compose(IoMainScheduler())
                .subscribe(object : BaseObserver<BaseResult<PayTipBean>>() {
                    override fun onSuccess(data: BaseResult<PayTipBean>) {
                        if (data.isSuccessful) {
                            tvHit.text = data.msg
                            tvOnlinePay.setOnClickListener {
                                ActivityUtils.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data.data.url)))
                            }
                            tvOnlinePay2.setOnClickListener {
                                ActivityUtils.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data.data.url)))
                            }
                        }
                    }

                    override fun onError(e: ResponseException) {
                    }

                })

    }

    private fun pointPurchase(money: String) {
        if (TextUtils.isEmpty(money)) {
            ToastUtils.showShort("请选择套餐")
            return
        }
        val vodService=Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return
        }

        RequestManager.execute(this, vodService.pointPurchase(money), object : LoadingObserver<PointPurchseBean>(mActivity) {
            override fun onSuccess(data: PointPurchseBean) {
                orderCode = data.order_code//记录order_code
                PayDialog(mActivity, data)
                        .setOnPayDialogClickListener(object : PayDialog.OnPayDialogClickListener() {
                            override fun onPayTypeClick(dialog: PayDialog, payment: String) {
                                super.onPayTypeClick(dialog, payment)
                                val payUrl = "${ApiConfig.MOGAI_BASE_URL + ApiConfig.PAY}?payment=${payment}&order_code=${data.order_code}"
                                Intent(context, BrowserActivity::class.java).apply {
                                    putExtra("url", payUrl)
                                    startActivityForResult(this, REQUEST_PAY)
                                }
                            }
                        })
                        .show()
            }

            override fun onError(e: ResponseException) {
            }

        })
    }

    private fun getScoreList() {
        val vodService = Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return
        }
        RequestManager.execute(this@JifenFragment, vodService.getScoreList(),
                object : LoadingObserver<ScoreListBean>(mActivity) {
                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(data: ScoreListBean) {
                        data.list?.`_$3`?.let {
//                            val day = data.list.`_$3`.group_points_day




//                            tv_score_day.text = "${day}积分"



                        }
                    }

                    override fun onError(e: ResponseException) {
                    }

                })

    }

    private fun checkOrder() {
        val vodService=Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return
        }
        orderCode?.let {
            RequestManager.execute(
                    this@JifenFragment,
                    vodService.order(it),
                    object : BaseObserver<OrderBean>() {
                        override fun onSuccess(data: OrderBean) {
                            orderCode = null //更新状态后 重置ordercode
                            if (data.order_status == 0) {
                                ToastUtils.showShort("未支付")
                            } else {
                                EventBus.getDefault().post(LoginBean())
                                if (packaged == "") {
                                    ToastUtils.showShort("支付成功")
                                } else {
                                    upgrade(packaged, "支付成功！是否兑换会员时间？")
                                }

                            }
                        }

                        override fun onError(e: ResponseException) {
                            orderCode = null
                        }

                    }

            )
        }

    }

    private fun cardBuy() {
        val card = etCardPassword.text.trim().toString()
        if (TextUtils.isEmpty(card)) {
            ToastUtils.showShort("卡密不能为空")
            return
        }
        val vodService=Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return
        }
        RequestManager.execute(this, vodService.cardBuy(card), object : LoadingObserver<CardBuyBean>(mActivity) {
            override fun onSuccess(data: CardBuyBean) {
                ToastUtils.showShort(data.msg)
                EventBus.getDefault().post(LoginBean())
            }

            override fun onError(e: ResponseException) {
            }

        })
    }

    companion object {
        const val REQUEST_PAY = 0
    }

    private fun upgrade(price: String, hitMsg: String) {
        val vodService = Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return
        }
        HitDialog(mActivity)
                .setMessage(hitMsg)
                .setOnHitDialogClickListener(object : HitDialog.OnHitDialogClickListener() {
                    override fun onOkClick(dialog: HitDialog) {
                        super.onOkClick(dialog)
                        RequestManager.execute(this@JifenFragment, vodService.upgradeGroup(price, UserUtils.userInfo?.group_id.toString()),
                                object : LoadingObserver<CardBuyBean>(mActivity) {
                                    override fun onSuccess(data: CardBuyBean) {
                                        ToastUtils.showShort(data.msg)
                                        EventBus.getDefault().post(LoginBean())
                                    }

                                    override fun onError(e: ResponseException) {
                                    }

                                })
                    }
                })
                .show()
    }

}
