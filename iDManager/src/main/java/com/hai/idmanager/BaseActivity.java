package com.hai.idmanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hai.idmanager.util.FingerprintDialogManager;

public class BaseActivity extends Activity {
    private String TAG = "BaseActivity";
	@SuppressWarnings("unchecked")
	protected <T> T findView(int id){
        return (T) findViewById(id);
    }

    //标题栏
    protected ImageButton ib_back;
    protected TextView tv_title;
    protected Button btn_action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化标题栏，必须在布局文件中添加@layout/view_title_bar.xml
     * @param showBackButton
     * @param title
     */
    protected void initTitleBar(boolean showBackButton, String title){
        if(ib_back == null || tv_title == null || btn_action == null){
            ib_back = findView(R.id.ib_back);
            tv_title = findView(R.id.tv_title);
            btn_action = findView(R.id.btn_action);
        }
        ib_back.setVisibility(showBackButton ? View.VISIBLE : View.GONE);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_title.setText(title);
    }

    /**
     * 显示指纹验证对话框
     */
    protected void showFingerScannerDialog(){
        showFingerScannerDialog(null);
    }

    protected void showFingerScannerDialog(FingerprintManagerCompat.AuthenticationCallback callback){
        FingerprintDialogManager.getInstance().showFingerScannerDialog(this, callback);
    }

}
