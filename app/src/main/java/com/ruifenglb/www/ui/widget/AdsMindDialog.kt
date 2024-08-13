package com.ruifenglb.www.ui.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import com.blankj.utilcode.util.ScreenUtils
import com.ruifenglb.www.R
import kotlinx.android.synthetic.main.dialog_ads_mind.tvAppUpdate
import kotlinx.android.synthetic.main.dialog_ads_mind.tvUpdate

class AdsMindDialog(context: Context,  private val watch:()->Unit) : Dialog(context, R.style.DefaultDialogStyle) {
    init {
        setContentView(R.layout.dialog_ads_mind)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        window!!.attributes?.apply {
            gravity = Gravity.CENTER
            width = (ScreenUtils.getScreenWidth() * 0.8).toInt()
        }
        tvAppUpdate.setOnClickListener {
            dismiss()
        }
        tvUpdate.setOnClickListener {
            dismiss()
            watch.invoke()
        }

    }
}