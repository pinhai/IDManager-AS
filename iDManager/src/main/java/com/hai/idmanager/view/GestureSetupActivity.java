package com.hai.idmanager.view;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hai.idmanager.R;
import com.hai.idmanager.view.base.BaseActivity;
import com.hai.idmanager.widget.gesture.GestureContentView;
import com.hai.idmanager.widget.gesture.GestureDrawline;
import com.hai.idmanager.widget.gesture.LockIndicator;

/**
 * 设置手势密码
 */
public class GestureSetupActivity extends BaseActivity implements View.OnClickListener{
    /** 手机号码*/
//    public static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";
//    /** 意图 */
//    public static final String PARAM_INTENT_CODE = "PARAM_INTENT_CODE";
//    /** 首次提示绘制手势密码，可以选择跳过 */
//    public static final String PARAM_IS_FIRST_ADVICE = "PARAM_IS_FIRST_ADVICE";
//    private TextView mTextTitle;
//    private TextView mTextCancel;
    private LockIndicator mLockIndicator;
    private TextView tv_tip;
    private FrameLayout fl_gestureContainer;
    private GestureContentView mGestureContentView;
    private TextView tv_reset;
//    private String mParamSetUpcode = null;
//    private String mParamPhoneNumber;
    private boolean mIsFirstInput = true;
    private String mFirstPassword = null;
//    private String mConfirmPassword = null;
//    private int mParamIntentCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_gesture);
        setUpViews();
        setUpListeners();
    }

    private void setUpViews() {
        tv_reset = (TextView) findViewById(R.id.tv_reset);
        tv_reset.setClickable(false);
        mLockIndicator = (LockIndicator) findViewById(R.id.lock_indicator);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        fl_gestureContainer = (FrameLayout) findViewById(R.id.fl_gestureContainer);
        // 初始化一个显示各个点的viewGroup
        mGestureContentView = new GestureContentView(this, false, "", new GestureDrawline.GestureCallBack() {
            @Override
            public void onGestureCodeInput(String inputCode) {
                if (!isInputPassValidate(inputCode)) {
                    tv_tip.setText(Html.fromHtml("<font color='#c70c1e'>最少链接4个点, 请重新输入</font>"));
                    mGestureContentView.clearDrawlineState(0L);
                    return;
                }
                if (mIsFirstInput) {
                    mFirstPassword = inputCode;
                    updateCodeList(inputCode);
                    mGestureContentView.clearDrawlineState(0L);
                    tv_reset.setClickable(true);
                    tv_reset.setText(getString(R.string.reset_gesture_code));
                } else {
                    if (inputCode.equals(mFirstPassword)) {
                        Toast.makeText(GestureSetupActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                        mGestureContentView.clearDrawlineState(0L);
                        GestureSetupActivity.this.finish();
                    } else {
                        tv_tip.setText(Html.fromHtml("<font color='#c70c1e'>与上一次绘制不一致，请重新绘制</font>"));
                        // 左右移动动画
                        Animation shakeAnimation = AnimationUtils.loadAnimation(GestureSetupActivity.this, R.anim.shake);
                        tv_tip.startAnimation(shakeAnimation);
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
        mGestureContentView.setParentView(fl_gestureContainer);
        updateCodeList("");
    }

    private void setUpListeners() {
        tv_reset.setOnClickListener(this);
    }

    private void updateCodeList(String inputCode) {
        // 更新选择的图案
        mLockIndicator.setPath(inputCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_reset:
                mIsFirstInput = true;
                updateCodeList("");
                tv_tip.setText(getString(R.string.set_gesture_pattern));
                break;
            default:
                break;
        }
    }

    private boolean isInputPassValidate(String inputPassword) {
        if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
            return false;
        }
        return true;
    }

}
