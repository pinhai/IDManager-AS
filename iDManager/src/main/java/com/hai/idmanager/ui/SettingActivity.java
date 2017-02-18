package com.hai.idmanager.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hai.idmanager.BaseActivity;
import com.hai.idmanager.R;
import com.hai.idmanager.constant.PrefConstants;
import com.hai.idmanager.util.SharedPrefUtil;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_gesture;
    private ToggleButton tb_gesturePsd, tb_fingerScanner;
    private TextView tv_version, tv_gestureSet;

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
        tb_gesturePsd.setOnCheckedChangeListener(gesturePasswordCheckListener);
        tb_fingerScanner = findView(R.id.tb_fingerScanner);
        tb_fingerScanner.setOnCheckedChangeListener(fingerprintCheckListener);
        tv_gestureSet = findView(R.id.tv_gestureSet);
        rl_gesture = findView(R.id.rl_gesture);

        tv_gestureSet.setText(SharedPrefUtil.getBoolean(PrefConstants.KEY_GESTURE_SET, false) ?
                R.string.set : R.string.unset);
        tb_gesturePsd.setChecked(SharedPrefUtil.getBoolean(PrefConstants.KEY_GESTURE_OPEN, false));
        tb_fingerScanner.setChecked(SharedPrefUtil.getBoolean(PrefConstants.KEY_FINGER_SCANNER,
                false));
        setVersionInfo();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
        }
    }

    private CompoundButton.OnCheckedChangeListener gesturePasswordCheckListener = new
            CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
            if(isChecked){
                if(!SharedPrefUtil.getBoolean(PrefConstants.KEY_GESTURE_SET, false)){
                    //未设置手势密码
                    buttonView.setChecked(false);
                    new AlertDialog.Builder(SettingActivity.this, R.style.BaseDialogTheme)
                            .setTitle(R.string.prompt)
                            .setMessage(R.string.please_setup_gesture_password_first)
                            .setPositiveButton(R.string.settings, new DialogInterface
                                    .OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.dismiss();
                                    Intent i = new Intent(SettingActivity.this,
                                            GestureSetupActivity.class);
                                    startActivity(i);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface
                                    .OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                }
            }else{

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
                    SharedPrefUtil.putBoolean(PrefConstants.KEY_FINGER_SCANNER, isChecked);
                }
            }else{
                buttonView.setChecked(true);
                //关闭指纹解锁
                showFingerScannerDialog(true, new OnAuthenticationStateListner() {
                    @Override
                    public void onAuthenticationState(boolean succeeded){
                        if(succeeded){
                            //指纹验证成功，关闭指纹解锁功能
                            SharedPrefUtil.putBoolean(PrefConstants.KEY_FINGER_SCANNER, false);
                            buttonView.setChecked(false);
                        }
                    }
                });
            }

        }
    };
}
