package com.ruifenglb.www.ui.pay

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.text.TextUtils

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

import com.ruifenglb.www.utils.Retrofit2Utils
import com.blankj.utilcode.util.ActivityUtils

import com.blankj.utilcode.util.ToastUtils
import com.ruifenglb.www.utils.UserUtils
import com.github.StormWyrm.wanandroid.base.exception.ResponseException
import com.github.StormWyrm.wanandroid.base.fragment.BaseFragment
import com.github.StormWyrm.wanandroid.base.net.RequestManager
import com.github.StormWyrm.wanandroid.base.net.observer.BaseObserver
import com.ruifenglb.www.base.observer.LoadingObserver
import com.github.StormWyrm.wanandroid.base.sheduler.IoMainScheduler
import com.ruifenglb.www.utils.MMkvUtils.Companion.Builds

import jaygoo.library.m3u8downloader.M3U8Library
import kotlinx.android.synthetic.main.fragment_pay.*

import org.greenrobot.eventbus.EventBus
import java.text.DecimalFormat
import java.util.*

class PayFragment : BaseFragment() {

    private var orderCode: String? = null
    private var packaged: String = ""

    override fun getLayoutId(): Int {
        return R.layout.fragment_pay
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initListener() {
        super.initListener()
//        tvMoney.setOnClickListener {
//            pointPurchase()
//        }

        week_price.setOnClickListener {
            week_price.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.bg_recharge_amount3)
            month_price.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.bg_recharge_amount2)
            year_price.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.bg_recharge_amount2)
            vip_tv_week_message.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.pay_zhekou)
            vip_tv_month_message.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.pay_zhekou_off)
            vip_tv_year_message.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.pay_zhekou_off)
            vip_tv_week_message.setTextColor(ContextCompat.getColor(M3U8Library.context, R.color.gray_999))
            vip_tv_month_message.setTextColor(ContextCompat.getColor(M3U8Library.context, R.color.gray_999))
            vip_tv_year_message.setTextColor(ContextCompat.getColor(M3U8Library.context, R.color.gray_999))
            packaged = "week"
            pointPurchase(week_money.text as String)
        }
        month_price.setOnClickListener {
            week_price.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.bg_recharge_amount2)
            month_price.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.bg_recharge_amount3)
            year_price.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.bg_recharge_amount2)
            vip_tv_week_message.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.pay_zhekou_off)
            vip_tv_month_message.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.pay_zhekou)
            vip_tv_year_message.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.pay_zhekou_off)
            vip_tv_week_message.setTextColor(ContextCompat.getColor(M3U8Library.context, R.color.gray_999))
            vip_tv_month_message.setTextColor(ContextCompat.getColor(M3U8Library.context, R.color.gray_999))
            vip_tv_year_message.setTextColor(ContextCompat.getColor(M3U8Library.context, R.color.gray_999))
            packaged = "month"
            pointPurchase(month_money.text as String)
        }
        year_price.setOnClickListener {
            week_price.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.bg_recharge_amount2)
            month_price.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.bg_recharge_amount2)
            year_price.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.bg_recharge_amount3)
            vip_tv_week_message.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.pay_zhekou_off)
            vip_tv_month_message.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.pay_zhekou_off)
            vip_tv_year_message.background = ContextCompat.getDrawable(M3U8Library.context, R.drawable.pay_zhekou)
            vip_tv_week_message.setTextColor(ContextCompat.getColor(M3U8Library.context, R.color.gray_999))
            vip_tv_month_message.setTextColor(ContextCompat.getColor(M3U8Library.context, R.color.gray_999))
            vip_tv_year_message.setTextColor(ContextCompat.getColor(M3U8Library.context, R.color.gray_999))
            packaged = "year"
            pointPurchase(year_money.text as String)
        }
    }

    override fun initLoad() {
        super.initLoad()
        getScoreList()
        getPayTip()
        val startBean = Objects.requireNonNull(Builds().loadStartBean(""))

        if (startBean?.vip_susuo == null || startBean.vip_susuo == "0") { //关闭套餐购买
            play_title.visibility = View.GONE
            play_title1.visibility = View.GONE
        } else {
            play_title1.visibility = View.VISIBLE
            play_title.visibility = View.VISIBLE
            getScoreList()
        }

        val ad = Builds().loadStartBean("")?.ads?.rechargeentry
        if (!ad?.description.isNullOrEmpty()) {
            Rechargeentry.text = ad?.description
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
        RequestManager.execute(this@PayFragment, vodService.getScoreList(),
                object : LoadingObserver<ScoreListBean>(mActivity) {
                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(data: ScoreListBean) {
                        data.list?.`_$3`?.let {
//                            val day = data.list.`_$3`.group_points_day
                            val week = data.list.`_$3`.group_points_week / 100
                            val month = data.list.`_$3`.group_points_month / 100
                            val year = data.list.`_$3`.group_points_year / 100

                            vip_tv_week_message.text = DecimalFormat("0.00").format((week.toFloat() / 7.toFloat()).toDouble()) + "元"+"/天"
                            vip_tv_month_message.text = DecimalFormat("0.00").format((month.toFloat() / 30.toFloat()).toDouble()) + "元"+"/天"
                            vip_tv_year_message.text = DecimalFormat("0.00").format((year.toFloat() / 360.toFloat()).toDouble()) + "元"+"/天"

//                            tv_score_day.text = "${day}积分"
                            week_money.text = "$week"
                            month_money.text = "$month"
                            year_money.text = "$year"
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
                    this@PayFragment,
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
                        RequestManager.execute(this@PayFragment, vodService.upgradeGroup(price, UserUtils.userInfo?.group_id.toString()),
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
