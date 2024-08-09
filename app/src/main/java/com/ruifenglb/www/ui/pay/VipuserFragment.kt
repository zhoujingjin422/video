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
import kotlinx.android.synthetic.main.fragment_pay.*
import kotlinx.android.synthetic.main.fragment_vipuser.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_vip.*
import kotlinx.android.synthetic.main.fragment_vipuser.btnPurchaseNow
import kotlinx.android.synthetic.main.fragment_vipuser.month_money
import kotlinx.android.synthetic.main.fragment_vipuser.month_money3
import kotlinx.android.synthetic.main.fragment_vipuser.month_price
import kotlinx.android.synthetic.main.fragment_vipuser.selectedPackagePrice
import kotlinx.android.synthetic.main.fragment_vipuser.tvHit
import kotlinx.android.synthetic.main.fragment_vipuser.week_money
import kotlinx.android.synthetic.main.fragment_vipuser.week_money3
import kotlinx.android.synthetic.main.fragment_vipuser.week_price
import kotlinx.android.synthetic.main.fragment_vipuser.year_money
import kotlinx.android.synthetic.main.fragment_vipuser.year_money3
import kotlinx.android.synthetic.main.fragment_vipuser.year_price
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.DecimalFormat
import java.util.*

class VipuserFragment : BaseFragment() {

    private var orderCode: String? = null
    private var packaged: String = ""

    override fun getLayoutId(): Int {
        return R.layout.fragment_vipuser
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private var selectedPackage = "" // 用于记录当前选中的套餐类型

    override fun initListener() {
        super.initListener()


        week_price.setOnClickListener {
            updateButtonBackgrounds(R.drawable.bg_recharge_amount3, "week")
            setSelectedPackagePriceText(week_money3.text.toString())
        }

        month_price.setOnClickListener {
            updateButtonBackgrounds(R.drawable.bg_recharge_amount3, "month")
            setSelectedPackagePriceText(month_money3.text.toString())
        }

        year_price.setOnClickListener {
            updateButtonBackgrounds(R.drawable.bg_recharge_amount3, "year")
            setSelectedPackagePriceText(year_money3.text.toString())
        }

        btnPurchaseNow.setOnClickListener {
            // 确保有套餐被选中
            if (selectedPackage.isNotBlank()) {
                when (selectedPackage) {
                    "week" -> pointPurchase(week_moneyok.text.toString())
                    "month" -> pointPurchase(month_moneyok.text.toString())
                    "year" -> pointPurchase(year_moneyok.text.toString())
                }
            } else {
                // 如果没有选中任何套餐，可以提示用户先选择套餐
                ToastUtils.showShort("请选择套餐")
            }
        }



    }
    private fun setSelectedPackagePriceText(price: String) {
        selectedPackagePrice.text = price
    }

    // 更新按钮背景颜色并设置选中套餐
    private fun updateButtonBackgrounds(selectedDrawableResId: Int, packageType: String) {
        week_price.background = ContextCompat.getDrawable(M3U8Library.context, if (packageType == "week") R.drawable.bg_recharge_amount2 else R.drawable.bg_recharge_amount3)
        month_price.background = ContextCompat.getDrawable(M3U8Library.context, if (packageType == "month") R.drawable.bg_recharge_amount2 else R.drawable.bg_recharge_amount3)
        year_price.background = ContextCompat.getDrawable(M3U8Library.context, if (packageType == "year") R.drawable.bg_recharge_amount2 else R.drawable.bg_recharge_amount3)
        selectedPackage = packageType

    }

    override fun initLoad() {
        super.initLoad()
        getScoreList()
        getPayTip()
        val startBean = Objects.requireNonNull(Builds().loadStartBean(""))




        val ad = Builds().loadStartBean("")?.ads?.rechargeentryjifen
        if (!ad?.description.isNullOrEmpty()) {
            Rechargeentryjifen.text = ad?.description
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
        RequestManager.execute(this@VipuserFragment, vodService.getScoreList(),
                object : LoadingObserver<ScoreListBean>(mActivity) {
                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(data: ScoreListBean) {
                        data.list?.`_$3`?.let {
//                            val day = data.list.`_$3`.group_points_day
                            val week = data.list.`_$3`.group_points_week / 1
                            val month = data.list.`_$3`.group_points_month / 1
                            val year = data.list.`_$3`.group_points_year / 1

                            val week2 = data.list.`_$3`.group_points_week / 100
                            val month2 = data.list.`_$3`.group_points_month / 100
                            val year2 = data.list.`_$3`.group_points_year / 100


                            val week3 = data.list.`_$3`.group_points_week / 100
                            val month3 = data.list.`_$3`.group_points_month / 100
                            val year3 = data.list.`_$3`.group_points_year / 100



//                            tv_score_day.text = "${day}积分"
                            week_money.text = "$week"
                            month_money.text = "$month"
                            year_money.text = "$year"

                            week_moneyok.text = "$week2"
                            month_moneyok.text = "$month2"
                            year_moneyok.text = "$year2"


                            week_money2.text =  "$week2"+"元"
                            month_money2.text = "$month2"+"元"
                            year_money2.text = "$year2"+"元"

                            week_money3.text = "¥" + "$week3"
                            month_money3.text = "¥" + "$month3"
                            year_money3.text = "¥" + "$year3"

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
                    this@VipuserFragment,
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
                                   // upgrade(packaged, "支付成功！是否兑换会员时间？")
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
                        RequestManager.execute(this@VipuserFragment, vodService.upgradeGroup(price, UserUtils.userInfo?.group_id.toString()),
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
