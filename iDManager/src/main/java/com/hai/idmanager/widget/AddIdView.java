package com.hai.idmanager.widget;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

import com.hai.idmanager.utils.ToastUtil;
import com.hai.idmanager.view.base.BasePopupWindow;
import com.hai.idmanager.R;

public class AddIdView extends BasePopupWindow implements OnClickListener{
	private Context mContext;
	private OnAddIdListener mOnAddIdListener;
	private View mView;
	
	private EditText et_idName;
	private EditText et_idInfo;
	private Button btn_confirm;
	private Button btn_cancle;
	
	public AddIdView(Context context, View view, OnAddIdListener onAddIdListener){
		super(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mView = view;
		mContext = context;
		mOnAddIdListener = onAddIdListener;
		
		initView();
		
	}
	
	private void initView() {
		et_idName = (EditText) mView.findViewById(R.id.et_idName);
		et_idInfo = (EditText) mView.findViewById(R.id.et_idInfo);
		btn_confirm = (Button) mView.findViewById(R.id.btn_confirm);
		btn_cancle = (Button) mView.findViewById(R.id.btn_cancel);
		
		btn_confirm.setOnClickListener(this);
		btn_cancle.setOnClickListener(this);
		
		setContentView(mView);
		int height = mView.findViewById(R.id.linear_container).getMeasuredHeight();
		setPopupAttribute(mContext, height);
	}

	public interface OnAddIdListener{
		void onAdding();
		void onAddId(String idName, String idInfo);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.btn_confirm){
			AddId();
		}else if(id == R.id.btn_cancel){
			this.dismiss();
		}
	}
	
	public void clearEditText(){
		et_idName.setText("");
		et_idInfo.setText("");
	}

	private void AddId() {
		String idName = String.valueOf(et_idName.getText());
		String idInfo = String.valueOf(et_idInfo.getText());
		if(idName == null || idName.equals("") || idInfo == null || idInfo.equals("")){
			ToastUtil.show(mContext, "输入不能为空");
			return;
		}
		mOnAddIdListener.onAdding();
		mOnAddIdListener.onAddId(idName, idInfo);
	}
}
