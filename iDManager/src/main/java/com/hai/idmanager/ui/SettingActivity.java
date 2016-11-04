package com.hai.idmanager.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hai.idmanager.BaseActivity;
import com.hai.idmanager.R;
import com.hai.idmanager.constant.PrefConstants;
import com.hai.idmanager.util.SharedPrefUtil;

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private ToggleButton tb_gesturePsd, tb_fingerScanner;
    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initData();
        initView();
    }

    private void initData() {
    }

    private void setVersionInfo() {
        PackageManager pm = getPackageManager();
        String vnStr = "";
        try{
            vnStr = pm.getPackageInfo(getPackageName(), 0).versionName;
            tv_version.setText(vnStr);
        }catch (PackageManager.NameNotFoundException e){
        }

    }

    private void initView() {
        initTitleBar(true, getString(R.string.settings));
        tv_version = findView(R.id.tv_version);
        tb_gesturePsd = findView(R.id.tb_gesturePsd);
        tb_gesturePsd.setOnCheckedChangeListener(gesturePasswordCheckListener);
        tb_fingerScanner = findView(R.id.tb_fingerScanner);
        tb_fingerScanner.setOnCheckedChangeListener(fingerprintCheckListener);

        setVersionInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }
    }

    private CompoundButton.OnCheckedChangeListener gesturePasswordCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){

            }else{

            }
        }
    };

    private CompoundButton.OnCheckedChangeListener fingerprintCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                if(!tb_gesturePsd.isChecked()){
                    //打开指纹验证要先开启手势密码
                    Toast.makeText(SettingActivity.this, R.string.please_setup_gesture_password_first, Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(false);
                }else{
                    SharedPrefUtil.getInstance().putBoolean(PrefConstants.KEY_FINGER_SCANNER, isChecked);
                }
            }else{
                buttonView.setChecked(true);
                //关闭指纹解锁
                showFingerScannerDialog(true, new OnAuthenticationStateListner() {
                    @Override
                    public void onAuthenticationState(boolean succeeded) {
                        if(succeeded){
                            //指纹验证成功，关闭指纹解锁功能
                            SharedPrefUtil.getInstance().putBoolean(PrefConstants.KEY_FINGER_SCANNER, false);
                            buttonView.setChecked(false);
                        }
                    }
                });
            }

        }
    };
}
