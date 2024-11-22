package com.hai.idmanager.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.WindowManager
import com.hai.idmanager.utils.ext.getSimpleName


/**
 * Author：admin_h on 2021/4/8 18:35
 * 屏幕相关工具类
 */
@SuppressLint("StaticFieldLeak")
object ScreenUtil {

    private lateinit var context : Context
    private var screenWidth = 0
    private var displayHeight = 0
    //屏幕密度
    private var densityDpi = 0
    //逻辑密度, 屏幕缩放因子
    private var density = 0f

    /**
     * @param context
     */
    fun init(context: Context) {
        this.context = context
        val dm = context.resources.displayMetrics
        screenWidth = dm.widthPixels
        displayHeight = dm.heightPixels
        densityDpi = dm.densityDpi
        density = dm.density
    }

    fun getScreenHeight(): Int = displayHeight + getStatusBarHeight() + getNavigationBarHeight()

    fun getScreenWidth(): Int = screenWidth

    fun getStatusBarHeight(): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val height = resources.getDimensionPixelSize(resourceId)
        LogUtil.v(getSimpleName<ScreenUtil>(), "Status height:$height")
        return height
    }

    fun getNavigationBarHeight(): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val height = resources.getDimensionPixelSize(resourceId)
        LogUtil.v(getSimpleName<ScreenUtil>(), "Navi height:$height")
        return height
    }

    fun px2dp(inParam: Int): Int = (inParam / density + 0.5f).toInt()

    fun dp2px(inParam: Int): Int = (inParam * density + 0.5f).toInt()

    fun px2sp(inParam: Int): Int = (inParam / density + 0.5f).toInt()

    fun sp2px(inParam: Int): Int = (inParam * density + 0.5f).toInt()

    /**
     * 是否是平板
     *
     * @return 是平板则返回true，反之返回false
     */
    fun isPad() : Boolean{
        val isPad = (context.resources.configuration.screenLayout
            and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.getDefaultDisplay()
        val dm = DisplayMetrics()
        display.getMetrics(dm)
        val x = Math.pow((dm.widthPixels / dm.xdpi).toDouble(), 2.0)
        val y = Math.pow((dm.heightPixels / dm.ydpi).toDouble(), 2.0)
        val screenInches = Math.sqrt(x + y) // 屏幕尺寸

        return isPad || screenInches >= 7.0
    }

    /**
     * 测量View的width以及height，执行该方法后通过view.getMeasureHeight()可获取view的高度，宽度亦然
     * @param view
     */
    fun measureView(view: View) {
        var p = view.layoutParams
        if (p == null) {
            p = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width)
        val lpHeight = p.height
        val childHeightSpec = if (lpHeight > 0) {
            MeasureSpec.makeMeasureSpec(
                lpHeight,
                MeasureSpec.EXACTLY
            )
        } else {
            MeasureSpec.makeMeasureSpec(
                0,
                MeasureSpec.UNSPECIFIED
            )
        }
        view.measure(childWidthSpec, childHeightSpec)
    }

}