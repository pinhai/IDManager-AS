package com.hai.idmanager.ui;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hai.idmanager.BaseActivity;
import com.hai.idmanager.R;
import com.hai.idmanager.constant.PrefConstants;
import com.hai.idmanager.util.SharedPrefUtil;

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private ToggleButton tb_fingerScanner;
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
        tv_version = findView(R.id.tv_version);
        tb_fingerScanner = findView(R.id.tb_fingerScanner);

        tb_fingerScanner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefUtil.getInstance().putBoolean(PrefConstants.KEY_FINGER_SCANNER, isChecked);
            }
        });

        setVersionInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }
    }
}
