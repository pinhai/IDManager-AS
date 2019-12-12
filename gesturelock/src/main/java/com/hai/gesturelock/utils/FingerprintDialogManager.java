package com.hai.gesturelock.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hai.gesturelock.R;

/**
 * 指纹验证
 */
public class FingerprintDialogManager {

    private static final String TAG = FingerprintDialogManager.class.getSimpleName();

    public static final int TYPE_LOGIN = 101;  //验证用户登录
    public static final int TYPE_OPEN = 102;  //验证打开指纹解锁
    public static final int TYPE_CLOSE = 103;  //验证关闭指纹解锁

    private FingerprintDialogManager(){}
    private static FingerprintDialogManager instance;
    public static FingerprintDialogManager getInstance(){
        if(instance == null){
            instance = new FingerprintDialogManager();
        }
        return instance;
    }

    private AlertDialog fingerScannerDialog;
    private TextView tv_prompt, tv_loginByGesture;
    private FingerprintManagerCompat fingerprintManagerCompat;

    private FingerprintManagerCompat.AuthenticationCallback callbackProxy;

    /**
     * 显示指纹验证对话框
     */
    public void showFingerScannerDialog(Context context){
        showFingerScannerDialog(context, null);
    }

    public void showFingerScannerDialog(Context context, FingerprintManagerCompat.AuthenticationCallback callbackProxy){
        showFingerScannerDialog(context, TYPE_LOGIN, callbackProxy);
    }

    public void showFingerScannerDialog(Context context, int type, FingerprintManagerCompat.AuthenticationCallback callbackProxy){
        if(context == null) return;

        this.callbackProxy = callbackProxy;

        if(fingerprintManagerCompat == null){
            fingerprintManagerCompat = FingerprintManagerCompat.from(context.getApplicationContext());
        }
        if(!fingerprintManagerCompat.isHardwareDetected()){
            //未探测到指纹采集器
            return;
        }

        View view = LayoutInflater.from(context).inflate(R.layout.view_finger_scanner, null);
        tv_prompt = (TextView)view.findViewById(R.id.tv_prompt);
        tv_prompt.setText(R.string.please_validate_finger_scanner);
        tv_loginByGesture = view.findViewById(R.id.tv_loginByGesture);
        boolean cancelable;
        if(type == TYPE_LOGIN){
            //登录时显示“使用手势密码”用于切换登录方式
            tv_loginByGesture.setVisibility(View.VISIBLE);
            tv_loginByGesture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(fingerScannerDialog != null) fingerScannerDialog.dismiss();
                }
            });
            cancelable = false;
        }else {
            tv_loginByGesture.setVisibility(View.INVISIBLE);
            cancelable = true;
        }
        if(fingerScannerDialog == null){
            fingerScannerDialog = new AlertDialog.Builder(context, R.style.FullscreenWhite)
                    .setTitle(R.string.tip)
                    .setView(view)
                    .setCancelable(cancelable)
                    .create();
            fingerScannerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    fingerScannerDialog = null;
                }
            });
        }
        fingerScannerDialog.show();

        fingerprintManagerCompat.authenticate(null, 0, null, authenticationCallback, null);
    }

    /**
     * 指纹验证回调
     */
    private FingerprintManagerCompat.AuthenticationCallback authenticationCallback = new FingerprintManagerCompat.AuthenticationCallback() {
        @Override
        // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息
//        处于安全性的考虑，不允许开发者短时间内连续授权，经过粗略的测试，android允许我们在30s之后重新打开Sensor授权监听
        public void onAuthenticationError(int errMsgId, CharSequence errString){
            super.onAuthenticationError(errMsgId, errString);
            tv_prompt.setText(R.string.validate_failure_more);
            handler.sendEmptyMessageDelayed(0, 1000 * 30);

            if(callbackProxy != null) callbackProxy.onAuthenticationError(errMsgId, errString);
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString){
            super.onAuthenticationHelp(helpMsgId, helpString);
            Log.v(TAG, "onAuthenticationHelp:" + helpString);

            if(callbackProxy != null) callbackProxy.onAuthenticationHelp(helpMsgId, helpString);
        }

        // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result){
            super.onAuthenticationSucceeded(result);
            tv_prompt.setText(R.string.validate_success);
            fingerScannerDialog.dismiss();

            if(callbackProxy != null) callbackProxy.onAuthenticationSucceeded(result);
        }

        @Override
        // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
        public void onAuthenticationFailed(){
            super.onAuthenticationFailed();
            tv_prompt.setText(R.string.validate_failure);

            if(callbackProxy != null) callbackProxy.onAuthenticationFailed();
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: 重启指纹模块");
            fingerprintManagerCompat.authenticate(null, 0, null, authenticationCallback, null);
        }
    };
}
