package com.hai.idmanager.view.setting;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hai.dialog.DialogListener;
import com.hai.dialog.DialogManager;
import com.hai.gesturelock.GestureEditActivity;
import com.hai.idmanager.R;
import com.hai.idmanager.constant.PrefConstants;
import com.hai.idmanager.utils.PreferenceUtil;
import com.hai.idmanager.view.base.BaseActivity;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_gesture;
    private ToggleButton tb_gesturePsd, tb_fingerScanner;
    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initData();
        initView();
    }

    private void initData(){
    }

    private void setVersionInfo(){
        PackageManager pm = getPackageManager();
        String vnStr = "";
        try{
            vnStr = pm.getPackageInfo(getPackageName(), 0).versionName;
            tv_version.setText(vnStr);
        }catch(PackageManager.NameNotFoundException e){
        }

    }

    private void initView(){
        initTitleBar(true, getString(R.string.settings));
        tv_version = findView(R.id.tv_version);
        tb_gesturePsd = findView(R.id.tb_gesturePsd);
        tb_fingerScanner = findView(R.id.tb_fingerScanner);

//        tv_gestureSet = findView(R.id.tv_gestureSet);
        rl_gesture = findView(R.id.rl_gesture);

//        tv_gestureSet.setText(PreferenceUtil.getBoolean(PrefConstants.KEY_GESTURE, false) ?
//                R.string.set : R.string.unset);
        tb_gesturePsd.setChecked(com.hai.gesturelock.utils.PreferenceUtil.hasGesturePsw());
        tb_fingerScanner.setChecked(PreferenceUtil.getBoolean(PrefConstants.KEY_FINGERPRINT,
                false));
//        tb_gesturePsd.setOnCheckedChangeListener(gesturePasswordCheckListener);
        tb_gesturePsd.setOnClickListener(gesturePswClickListener);
        tb_fingerScanner.setOnCheckedChangeListener(fingerprintCheckListener);

        setVersionInfo();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GestureEditActivity.REQUEST_CODE_SET_GESTURE_PSW
                && resultCode == GestureEditActivity.RESULT_CODE_SET_GESTURE_PSW){
            tb_gesturePsd.setChecked(com.hai.gesturelock.utils.PreferenceUtil.hasGesturePsw());
        }
    }

    private View.OnClickListener gesturePswClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(tb_gesturePsd.isChecked()){
                //要开启手势密码
                GestureEditActivity.startForResult(SettingActivity.this, GestureEditActivity.REQUEST_CODE_SET_GESTURE_PSW);
                tb_gesturePsd.setChecked(false);
            }else {
                //要关闭手势密码
                DialogManager.getInstance().showMessageDialog(SettingActivity.this,
                        getString(R.string.tip), getString(R.string.close_gesture_psw_tip),
                        getString(R.string.cancel), getString(R.string.ok), new DialogListener.OnMessageDialogListener() {
                            @Override
                            public void onLeft(Dialog dialog) {
                                dialog.dismiss();
                            }

                            @Override
                            public void onRight(Dialog dialog) {
                                dialog.dismiss();
                                com.hai.gesturelock.utils.PreferenceUtil.removeGesturePsw();
                                tb_gesturePsd.setChecked(false);
                                //todo 关闭指纹
                            }
                        });
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener fingerprintCheckListener = new CompoundButton
            .OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked){
            if(isChecked){
                if(!tb_gesturePsd.isChecked()){
                    //打开指纹验证要先开启手势密码
                    Toast.makeText(SettingActivity.this, R.string
                            .please_setup_and_open_gesture_password_first,
                            Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(false);
                }else{
                    PreferenceUtil.putBoolean(PrefConstants.KEY_FINGERPRINT, isChecked);
                }
            }else{
                buttonView.setChecked(true);
                //关闭指纹解锁
                showFingerScannerDialog(new FingerprintManagerCompat.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                        //指纹验证成功，关闭指纹解锁功能
                        PreferenceUtil.putBoolean(PrefConstants.KEY_FINGERPRINT, false);
                        buttonView.setChecked(false);
                    }

                });
            }

        }
    };
}
