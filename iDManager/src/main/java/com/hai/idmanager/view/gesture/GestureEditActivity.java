package com.hai.idmanager.view.gesture;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hai.idmanager.R;
import com.hai.idmanager.utils.PreferenceUtil;
import com.hai.idmanager.utils.ToastUtil;
import com.hai.idmanager.widget.gesture.GestureContentView;
import com.hai.idmanager.widget.gesture.LockIndicator;
import com.hai.idmanager.widget.gesture.GestureDrawline.GestureCallBack;
import com.hai.idmanager.widget.DialogListener;
import com.hai.idmanager.widget.DialogManager;

/**
 * 
 * 手势密码设置界面
 *
 */
public class GestureEditActivity extends Activity implements OnClickListener {

	public static final int REQUEST_CODE_SET_GESTURE_PSW = 1001;  //设置手势密码
	public static final int RESULT_CODE_SET_GESTURE_PSW = 2001;

	private TextView mTextTitle;
	private TextView mTextCancel;
	private LockIndicator mLockIndicator;
	private TextView mTextTip;
	private FrameLayout mGestureContainer;
	private GestureContentView mGestureContentView;
	private TextView mTextReset;
	private boolean mIsFirstInput = true;
	private String mFirstPassword = null;

	private boolean isFirstSet; //是否第一次设置，否的话就需要先验证之前的密码
	private String gesturePsw;

	public static void startForResult(Context context, int requestCode){
		Intent intent = new Intent(context, GestureEditActivity.class);
		((Activity)context).startActivityForResult(intent, requestCode);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.hai.idmanager.R.layout.activity_gesture_edit);

		initData();
		setUpViews();
		setUpListeners();
	}

	private void initData() {
		gesturePsw = PreferenceUtil.getGesturePsw();
		isFirstSet = TextUtils.isEmpty(gesturePsw);
	}
	
	private void setUpViews() {
		mTextTitle = (TextView) findViewById(com.hai.idmanager.R.id.text_title);
		mTextCancel = (TextView) findViewById(com.hai.idmanager.R.id.text_cancel);
		mTextReset = (TextView) findViewById(com.hai.idmanager.R.id.text_reset);
		mTextReset.setClickable(false);
		mLockIndicator = (LockIndicator) findViewById(com.hai.idmanager.R.id.lock_indicator);
		mTextTip = (TextView) findViewById(com.hai.idmanager.R.id.text_tip);
		mGestureContainer = (FrameLayout) findViewById(com.hai.idmanager.R.id.gesture_container);
		// 初始化一个显示各个点的viewGroup
		mGestureContentView = new GestureContentView(this, false, "", new GestureCallBack() {
			@Override
			public void onGestureCodeInput(String inputCode) {

				if(!isFirstSet){
					//验证之前设置的密码
					if(!gesturePsw.equals(inputCode)){
						ToastUtil.show(GestureEditActivity.this, "手势密码错误，请重试");
						mGestureContentView.clearDrawlineState(500L);
					}else {
						isFirstSet = true;
						showClearPswDialog();
						mGestureContentView.clearDrawlineState(500L, true);
					}
					return;
				}

				if (!isInputPassValidate(inputCode)) {
					mTextTip.setText(Html.fromHtml("<font color='#c70c1e'>最少链接4个点, 请重新输入</font>"));
					mGestureContentView.clearDrawlineState(0L);
					return;
				}
				if (mIsFirstInput) {
					mFirstPassword = inputCode;
					updateCodeList(inputCode);
					mGestureContentView.clearDrawlineState(0L);
					mTextReset.setClickable(true);
					mTextReset.setText(getString(com.hai.idmanager.R.string.reset_gesture_code));
				} else {
					if (inputCode.equals(mFirstPassword)) {
							ToastUtil.show(GestureEditActivity.this, "设置成功");
						mGestureContentView.clearDrawlineState(0L);
						saveGesturePsw();
						setResult(RESULT_CODE_SET_GESTURE_PSW);
						GestureEditActivity.this.finish();
					} else {
						mTextTip.setText(Html.fromHtml("<font color='#c70c1e'>与上一次绘制不一致，请重新绘制</font>"));
						// 左右移动动画
						Animation shakeAnimation = AnimationUtils.loadAnimation(GestureEditActivity.this, com.hai.idmanager.R.anim.shake);
						mTextTip.startAnimation(shakeAnimation);
						// 保持绘制的线，1.5秒后清除
						mGestureContentView.clearDrawlineState(1300L);
					}
				}
				mIsFirstInput = false;
			}

			@Override
			public void checkedSuccess() {
				
			}

			@Override
			public void checkedFail() {
				
			}
		});
		// 设置手势解锁显示到哪个布局里面
		mGestureContentView.setParentView(mGestureContainer);
		updateCodeList("");
	}
	
	private void setUpListeners() {
		mTextCancel.setOnClickListener(this);
		mTextReset.setOnClickListener(this);
	}
	
	private void updateCodeList(String inputCode) {
		// 更新选择的图案
		mLockIndicator.setPath(inputCode);
	}

	private void saveGesturePsw(){
		PreferenceUtil.putGesturePsw(mFirstPassword);
	}

	private void showClearPswDialog() {
		DialogManager.getInstance().showMessageDialog(this, getString(com.hai.idmanager.R.string.tip),
				getString(com.hai.idmanager.R.string.clear_gesture_psw_tip),
				getString(com.hai.idmanager.R.string.cancel_unlock_psw), getString(com.hai.idmanager.R.string.set_new_psw),
				false, false, new DialogListener.OnMessageDialogListener() {
					@Override
					public void onLeft(Dialog dialog) {
						dialog.dismiss();
						clearGesturePsw();
						ToastUtil.show(dialog.getContext(), com.hai.idmanager.R.string.gesture_psw_have_cancel);
						finish();
					}

					@Override
					public void onRight(Dialog dialog) {
						//设置新密码
						dialog.dismiss();
						mTextTip.setText(getString(com.hai.idmanager.R.string.please_set_new_unlock_pic));
					}
				});
	}

	private void clearGesturePsw(){
		PreferenceUtil.removeGesturePsw();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.text_cancel) {
			this.finish();
		} else if (id == R.id.text_reset) {
			mIsFirstInput = true;
			updateCodeList("");
			mTextTip.setText(getString(com.hai.idmanager.R.string.set_gesture_pattern));
		}
	}
	
	private boolean isInputPassValidate(String inputPassword) {
		if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
			return false;
		}
		return true;
	}
	
}
