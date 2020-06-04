package com.hai.securitylock;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hai.securitylock.utils.FingerprintDialogManager;
import com.hai.securitylock.utils.PreferenceUtil;
import com.hai.securitylock.widget.GestureContentView;
import com.hai.securitylock.widget.GestureDrawline.GestureCallBack;

/**
 * 
 * 手势绘制/校验界面
 *
 */
public class GestureVerifyActivity extends Activity implements View.OnClickListener {

	public static final int REQUEST_CODE_VERIFY_GESTURE_PSW = 1011;  //验证手势密码，为了取消手势密码
	public static final int REQUEST_CODE_VERIFY_GESTURE_PSW2 = 1012;  //验证手势密码，进入APP
	public static final int RESULT_CODE_VERIFY_GESTURE_PSW = 2011;

	private TextView mTextTip;
	private FrameLayout mGestureContainer;
	private GestureContentView mGestureContentView;
	private TextView mTextVerifyFingerprint;  //切换到指纹验证

	public static void startForResult(Context context, int requestCode){
		Intent intent = new Intent(context, GestureVerifyActivity.class);
		intent.putExtra("requestCode", requestCode);
		((Activity)context).startActivityForResult(intent, requestCode);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_verify);
		setUpViews();
		setUpListeners();
	}
	
	private void setUpViews() {
		mTextTip = (TextView) findViewById(R.id.text_tip);
		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
		mTextVerifyFingerprint = (TextView) findViewById(R.id.text_verify_fingerprint);
		
		// 初始化一个显示各个点的viewGroup
		mGestureContentView = new GestureContentView(this, true, PreferenceUtil.getGesturePsw(),
				new GestureCallBack() {

					@Override
					public void onGestureCodeInput(String inputCode) {

					}

					@Override
					public void checkedSuccess() {
						mGestureContentView.clearDrawlineState(0L);
						setResult(RESULT_CODE_VERIFY_GESTURE_PSW);
						GestureVerifyActivity.this.finish();
					}

					@Override
					public void checkedFail() {
						mGestureContentView.clearDrawlineState(1000L);
						mTextTip.setVisibility(View.VISIBLE);
						mTextTip.setText(Html
								.fromHtml("<font color='#c70c1e'>密码错误</font>"));
						// 左右移动动画
						Animation shakeAnimation = AnimationUtils.loadAnimation(GestureVerifyActivity.this, R.anim.shake);
						mTextTip.startAnimation(shakeAnimation);
					}
				});
		// 设置手势解锁显示到哪个布局里面
		mGestureContentView.setParentView(mGestureContainer);

		int requestCode = getIntent().getIntExtra("requestCode", 0);
		switch (requestCode){
			case REQUEST_CODE_VERIFY_GESTURE_PSW:
				mTextVerifyFingerprint.setVisibility(View.INVISIBLE);
				break;
			case REQUEST_CODE_VERIFY_GESTURE_PSW2:
				if(PreferenceUtil.isFingerprintLockOpened()){
					mTextVerifyFingerprint.setVisibility(View.VISIBLE);
					loginByFingerprint();
				}else {
					mTextVerifyFingerprint.setVisibility(View.INVISIBLE);
				}
				break;
		}
	}
	
	private void setUpListeners() {
		mTextVerifyFingerprint.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.text_verify_fingerprint){
			//通过指纹登录
			loginByFingerprint();
		}
	}

	private void loginByFingerprint() {
			FingerprintDialogManager.getInstance().showFingerScannerDialog(this,
					FingerprintDialogManager.TYPE_LOGIN,
					new FingerprintManagerCompat.AuthenticationCallback(){
						@Override
						public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
							//指纹验证成功
							setResult(RESULT_CODE_VERIFY_GESTURE_PSW);
							GestureVerifyActivity.this.finish();
						}
					});
//		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			int requestCode = getIntent().getIntExtra("requestCode", 0);
			if(requestCode == REQUEST_CODE_VERIFY_GESTURE_PSW2){
				//进入APP时验证手势不能按返回键退出
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
