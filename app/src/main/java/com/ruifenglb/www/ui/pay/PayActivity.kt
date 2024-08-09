package com.ruifenglb.www.ui.pay

import android.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.ruifenglb.www.ApiConfig
import com.ruifenglb.www.banner.Data;

import com.ruifenglb.www.R
import com.ruifenglb.www.base.BaseActivity
import com.ruifenglb.www.bean.UserInfoBean
import com.ruifenglb.www.netservice.VodService
import com.ruifenglb.www.utils.AgainstCheatUtil
import com.ruifenglb.www.utils.Retrofit2Utils
import com.ruifenglb.www.utils.UserUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.github.StormWyrm.wanandroid.base.exception.ResponseException
import com.github.StormWyrm.wanandroid.base.net.RequestManager
import com.github.StormWyrm.wanandroid.base.net.observer.BaseObserver
import com.ruifenglb.www.download.SPUtils
import com.ruifenglb.www.utils.DateUtil
import kotlinx.android.synthetic.main.activity_pay.*

import org.greenrobot.eventbus.Subscribe
import java.util.*

class PayActivity : BaseActivity() {

    override fun getLayoutResID(): Int {
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT)
        BarUtils.setStatusBarLightMode(this, true)
        return R.layout.activity_pay
    }

    override fun onResume() {
        super.onResume()
        var vodService= Retrofit2Utils.INSTANCE.createByGson(VodService::class.java)
        if (AgainstCheatUtil.showWarn(vodService)) {
            return;
        }
        RequestManager.execute(this, vodService.userInfo(), object : BaseObserver<UserInfoBean>() {
            override fun onSuccess(data: UserInfoBean) {
                UserUtils.userInfo = data
                changeUserInfo()
            }

            override fun onError(e: ResponseException) {
            }
        })
    }

    override fun initView() {
        changeUserInfo()

        // 获取intent中传递的类型，并根据类型设置初始标题
        val type = intent.getIntExtra("type", 0)
        tv_task_title.setText(
                when (type) {
                    2 -> R.string.pay_title
                    1 -> R.string.jifen_pay_hit
                    3 -> R.string.vipuser_pay_hit
                    else -> R.string.vip_pay_hit
                }
        )

        // 使用EnhancedPagerAdapter设置ViewPager的适配器
        vpPay.adapter = EnhancedPagerAdapter(supportFragmentManager)

        // 将TabLayout与ViewPager关联
        tab.setupWithViewPager(vpPay)

        // 根据type设置ViewPager的初始页面
        when (type) {
            1 -> vpPay.setCurrentItem(1, true) // 跳转到JifenFragment
            2 -> vpPay.setCurrentItem(2, true) // 如果需要，也可以为VipuserFragment设置初始跳转
            3 -> vpPay.setCurrentItem(3, true) // 如果需要，也可以为VipuserFragment设置初始跳转
            4 -> vpPay.setCurrentItem(4, true) // 如果需要，也可以为VipuserFragment设置初始跳转
        }
    }

    override fun initListener() {
        iv_task_back.setOnClickListener {
            finish()
        }
        vpPay.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> tv_task_title.setText(R.string.pay_title) // PayFragment
                    1 -> tv_task_title.setText(R.string.jifen_pay_hit) // JifenFragment
                    2 -> tv_task_title.setText(R.string.vip_pay_hit) // VipFragment
                    3 -> tv_task_title.setText(R.string.vipuser_pay_hit) // VipuserFragment
                    4 -> tv_task_title.setText(R.string.vipuser_pay_hit) // VipuserFragment

                    else -> {
                        // 这里可以处理异常情况，虽然在当前实现中不会发生，但保持良好的错误处理习惯是有益的
                    }
                }
            }

        })
    }

    override fun isUseEventBus(): Boolean {
        return true
    }

    @Subscribe
    fun onUserInfoChanged(userinfo: UserInfoBean? = null) {
        changeUserInfo()
    }



    private fun changeUserInfo() {
        UserUtils.userInfo?.let {
            tvMessage.text = "${it.group?.group_name}"

            val isVIp = it.group.group_name.contains("VIP")
            SPUtils.setBoolean(mActivity, "isVip", isVIp)

            if (isVIp) {
                if (it.user_end_time <= 0 || Calendar.getInstance().timeInMillis / 1000 > it.user_end_time) {
                    tvExpireTime.text = "非VIP或已过期"
                } else {
                    tvExpireTime.text = "有效期至：${DateUtil.getyyyyMMddHHmm(it.user_end_time*1000)}"
                }
            } else {
                // 如果不是VIP，直接显示"非VIP或已过期"
                tvExpireTime.text = "非VIP或已过期"
            }
            tvCoin.text = StringUtils.getString(R.string.remaining_coin, it.user_gold)
            tvPoints.text = StringUtils.getString(R.string.remaining_points, it.user_points.toString())
            if (it.user_portrait.isNotEmpty()) {
                Glide.with(mActivity)
                        .load(ApiConfig.MOGAI_BASE_URL + "/" + it.user_portrait)
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .placeholder(tvAvator.getDrawable())
                        .dontAnimate()
                        .into(tvAvator)
            } else {
                Glide.with(mActivity)
                        .load(R.drawable.ic_default_avator)
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .placeholder(tvAvator.getDrawable())
                        .dontAnimate()
                        .into(tvAvator)
            }
        }
    }

    private class EnhancedPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        companion object {
            private const val PAGE_COUNT = 4
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> PayFragment()
                1 -> JifenFragment() // 添加JifenFragment兑换码
                2 -> VipFragment()   // 添加VipFragment积分兑换
                3 -> VipuserFragment() // 添加VipuserFragment充值会员
                4 -> VipFragment() // 添加VipuserFragment充值会员
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }

        override fun getCount(): Int {
            return PAGE_COUNT
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> StringUtils.getString(R.string.pay_hit)
                1 -> StringUtils.getString(R.string.jifen_pay_hit) // 为JifenFragment设置标题
                2 -> StringUtils.getString(R.string.vip_pay_hit)
                3 -> StringUtils.getString(R.string.vipuser_pay_hit) // 为VipuserFragment设置标题
                4 -> StringUtils.getString(R.string.vip_pay_hit) // 为VipuserFragment设置标题
                else -> null
            }
        }
    }
}
