package com.hai.idmanager.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

import com.hai.idmanager.R;
import com.hai.idmanager.widget.DialogManager;

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

    private Dialog fingerScannerDialog;
    private TextView tv_prompt, tv_loginByGesture;
    private FingerprintManagerCompat fingerprintManagerCompat;
    private CancellationSignal cancellationSignal;

    private FingerprintManagerCompat.AuthenticationCallback callbackProxy;

    private void initManagerCompat(Context context){
        if(fingerprintManagerCompat == null){
            fingerprintManagerCompat = FingerprintManagerCompat.from(context.getApplicationContext());
        }
        cancellationSignal = new CancellationSignal();
    }

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

        initManagerCompat(context);
        if(!isHardwareDetected(context)){
            //未探测到指纹采集器
            return;
        }

        View view = LayoutInflater.from(context).inflate(R.layout.view_finger_scanner, null);
        tv_prompt = (TextView)view.findViewById(R.id.tv_prompt);
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
            fingerScannerDialog = DialogManager.getInstance().getCenterDialog(context, view, true);
            fingerScannerDialog.setTitle(R.string.tip);
            fingerScannerDialog.setCancelable(cancelable);
            fingerScannerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    fingerScannerDialog = null;
                    cancellationSignal.cancel();
                    cancellationSignal = null;
                }
            });
        }
        fingerScannerDialog.show();

        tv_prompt.setText(R.string.please_validate_finger_scanner);
        fingerprintManagerCompat.authenticate(null, 0, cancellationSignal, authenticationCallback, null);
    }

    /**
     * 指纹验证回调
     */
    private FingerprintManagerCompat.AuthenticationCallback authenticationCallback = new FingerprintManagerCompat.AuthenticationCallback() {
        @Override
        // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息
//        处于安全性的考虑，不允许开发者 在未结束验证流程的情况下 短时间内连续授权，经过粗略的测试，android允许我们在30s之后重新打开Sensor授权监听
        public void onAuthenticationError(int errMsgId, CharSequence errString){
            tv_prompt.setText(R.string.validate_failure_more);

            if(callbackProxy != null) callbackProxy.onAuthenticationError(errMsgId, errString);
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString){
            Log.v(TAG, "onAuthenticationHelp:" + helpString);

            if(callbackProxy != null) callbackProxy.onAuthenticationHelp(helpMsgId, helpString);
        }

        // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result){
            tv_prompt.setText(R.string.validate_success);
            fingerScannerDialog.dismiss();

            if(callbackProxy != null) callbackProxy.onAuthenticationSucceeded(result);
        }

        @Override
        // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
        public void onAuthenticationFailed(){
            tv_prompt.setText(R.string.validate_failure);

            if(callbackProxy != null) callbackProxy.onAuthenticationFailed();
        }
    };

    public boolean isHardwareDetected(Context context) {
        initManagerCompat(context);
        return fingerprintManagerCompat.isHardwareDetected();
    }

}
