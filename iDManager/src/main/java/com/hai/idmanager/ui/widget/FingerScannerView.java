package com.hai.idmanager.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hai.idmanager.R;

/**
 * 指纹解锁弹窗
 * Created by Administrator on 2016/10/27 0027.
 */

public class FingerScannerView extends PopupWindow{

    private Context mContext;
    private View contentView;
    private TextView tv_prompt;

    public FingerScannerView(Context context){
        this(context, null);
    }

    public FingerScannerView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public FingerScannerView(Context context, AttributeSet attrs, int defStyleAttr){
        this(context, attrs, defStyleAttr, 0);
    }

    public FingerScannerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        contentView = LayoutInflater.from(mContext).inflate(R.layout.view_finger_scanner, null);
        initView();
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setContentView(contentView);
        setOutsideTouchable(false);
    }

    private void initView() {
        tv_prompt = (TextView)contentView.findViewById(R.id.tv_prompt);
        tv_prompt.setText(R.string.please_validate_finger_scanner);
    }

    public enum FingerScannerState{
        DEFAULT, SCANNING, SCAN_SUCCESS, SCAN_FAILURE;
    }

    /**
     * 设置当前指纹扫描的状态
     * @param state
     */
    public void setFingerScannerState(FingerScannerState state){
        switch (state){
            case DEFAULT:
                tv_prompt.setText(R.string.please_validate_finger_scanner);
                break;
            case SCANNING:
                tv_prompt.setText(R.string.validating_finger_scanner);
                break;
            case SCAN_SUCCESS:
                tv_prompt.setText(R.string.validate_success);
                break;
            case SCAN_FAILURE:
                tv_prompt.setText(R.string.validate_failure);
                break;
        }
    }
}
