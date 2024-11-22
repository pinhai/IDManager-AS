package com.hai.idmanager.utils

import android.util.Log
import com.hai.idmanager.BuildConfig

/**

 * Author：admin_h on 2021/4/12 15:56
    日志工具类
 */
object LogUtil {
    private const val VERBOSE = 1
    private const val DEBUG = 2
    private const val INFO = 3
    private const val WARNING = 4
    private const val ERROR = 5

    private var showLog: Boolean = false

    /**
     * 手动控制是否显示log
     */
    fun initIfNeed(showLog: Boolean){
        this.showLog = showLog
    }

    //发布时为ERROR，这样只会打印错误信息
    private val level by lazy {
        if(BuildConfig.DEBUG || showLog) VERBOSE else ERROR
    }

    fun v(tag: String, msg: String){
        if(level <= VERBOSE) Log.v(tag, msg)
    }

    fun d(tag: String, msg: String){
        if(level <= DEBUG) Log.d(tag, msg)
    }

    fun i(tag: String, msg: String){
        if(level <= INFO) Log.i(tag, msg)
    }

    fun w(tag: String, msg: String){
        if(level <= WARNING) Log.w(tag, msg)
    }

    fun e(tag: String, msg: String){
        if(level <= ERROR) Log.e(tag, msg)
    }

}