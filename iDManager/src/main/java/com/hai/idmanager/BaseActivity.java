package com.hai.idmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hai.idmanager.ui.widget.FingerScannerView;

public class BaseActivity extends Activity {
    private String TAG = "BaseActivity";
	@SuppressWarnings("unchecked")
	protected <T> T findView(int id){
        return (T) findViewById(id);
    }

    private AlertDialog fingerScannerDialog;
    private TextView tv_prompt;
    private FingerScannerView fingerScannerView;
    private FingerprintManagerCompat fingerprintManagerCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 显示指纹验证对话框
     */
    protected void showFingerScannerDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.view_finger_scanner, null);
        tv_prompt = (TextView)view.findViewById(R.id.tv_prompt);
        tv_prompt.setText(R.string.please_validate_finger_scanner);
        if(fingerScannerDialog == null){
            fingerScannerDialog = new AlertDialog.Builder(this, R.style.FullscreenWhite)
                    .setTitle(R.string.prompt)
                    .setView(view)
                    .setCancelable(false)
                    .create();

        }
        fingerScannerDialog.show();

        if(fingerprintManagerCompat == null){
            fingerprintManagerCompat = FingerprintManagerCompat.from(this);
        }
        fingerprintManagerCompat.authenticate(null, 0, null, authenticationCallback, null);
    }

    /**
     * @deprecated
     * @param parent
     */
    protected void showFingerScannerWindow(View parent){
        if(fingerScannerView == null){
            fingerScannerView = new FingerScannerView(this);

        }
        fingerScannerView.setFingerScannerState(FingerScannerView.FingerScannerState.DEFAULT);
        fingerScannerView.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    protected boolean isShowingFingerScannerDialog(){
        return fingerScannerDialog.isShowing();
    }

    protected void dismissFingerScannerDialog(){
        fingerScannerDialog.dismiss();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: 重启指纹模块");
            fingerprintManagerCompat.authenticate(null, 0, null, authenticationCallback, null);
        }
    };

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
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString){
            super.onAuthenticationHelp(helpMsgId, helpString);
            Log.v(TAG, "onAuthenticationHelp:" + helpString);
        }

        // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result){
            super.onAuthenticationSucceeded(result);
            tv_prompt.setText(R.string.validate_success);
            fingerScannerDialog.dismiss();
        }

        @Override
        // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
        public void onAuthenticationFailed(){
            super.onAuthenticationFailed();
            tv_prompt.setText(R.string.validate_failure);
        }
    };
}
