package com.hai.idmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hai.idmanager.ui.widget.FingerScannerView;

public class BaseActivity extends Activity {
	@SuppressWarnings("unchecked")
	protected <T> T findView(int id){
        return (T) findViewById(id);
    }

    private AlertDialog fingerScannerDialog;
    private TextView tv_prompt;
    private FingerScannerView fingerScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showFingerScannerDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.view_finger_scanner, null);
        tv_prompt = (TextView)view.findViewById(R.id.tv_prompt);
        tv_prompt.setText(R.string.please_validate_finger_scanner);
        if(fingerScannerDialog == null){
            fingerScannerDialog = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_DialogWhenLarge)
                    .setTitle(R.string.prompt)
//                    .setView(view)
                    .setCancelable(true)
                    .create();

        }
        fingerScannerDialog.show();
    }

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
}
