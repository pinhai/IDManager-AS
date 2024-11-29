package com.hai.idmanager.view;

import com.gyf.immersionbar.ImmersionBar;
import com.hai.idmanager.utils.ToastUtil;
import com.hai.idmanager.R;
import com.hai.idmanager.comm.respentity.IdModel;
import com.hai.idmanager.view.base.BaseActivity;
import com.hai.idmanager.sqlite.DbHelper;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class EditIdActivity extends BaseActivity implements OnClickListener{
	private ImageButton ib_back;
	private TextView tv_title;
	
	private EditText et_idName;
	private EditText et_idInfo;
	private Button btn_edit;
	private Button btn_submit;
	
	private IdModel idModel;
	
	private DbHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editid);
		
		initData();
		initView();
		initSystemBar();
	}

	private void initSystemBar() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
			ImmersionBar.with(this)
					.titleBar(R.id.title_bar)
					.statusBarDarkFont(true)
					.init();
			setBottomPadding(findView(R.id.ll_top));
		}
	}

	private void initData() {
		Bundle bundle = getIntent().getBundleExtra("data");
		idModel = (IdModel) bundle.getSerializable("itemModel");
		
		dbHelper = new DbHelper(this);
	}
	
	private void initView() {
		ib_back = findView(R.id.ib_back);
		tv_title = findView(R.id.tv_title);
		et_idName = findView(R.id.et_idName);
		et_idInfo = findView(R.id.et_idInfo);
		btn_edit = findView(R.id.btn_edit);
		btn_submit = findView(R.id.btn_submit);
		btn_edit.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
		ib_back.setOnClickListener(this);
		
		tv_title.setText("查看账号信息");
		et_idName.setText(idModel.getIdName());
		et_idInfo.setText(idModel.getIdInfo());
		et_idName.setEnabled(false);
		et_idInfo.setEnabled(false);
	}

	@Override
	public void onClick(View v) {
		if(v == ib_back){
			this.finish();
		}else if(v == btn_edit){
			et_idName.setEnabled(true);
			et_idInfo.setEnabled(true);
			tv_title.setText("编辑");
		}else if(v == btn_submit){
			if(!et_idName.isEnabled() || !et_idInfo.isEnabled()){
				ToastUtil.show(this, "请点击编辑按钮");
				return;
			}
			String idName = et_idName.getText().toString().trim();
			String idInfo = et_idInfo.getText().toString().trim();
			if(idName.equals("") || idName == null || idInfo.equals("") || idInfo == null){
				ToastUtil.show(this, "输入不能为空");
				return;
			}
			IdModel idModelModify = new IdModel();
			idModelModify.setId(idModel.getId());
			idModelModify.setIdName(idName);
			idModelModify.setIdInfo(idInfo);
			if(dbHelper.modifyIdInfo(idModelModify)){
				Intent intent = getIntent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("itemModel", idModelModify);
				intent.putExtra("data", bundle);
				setResult(110, intent);
				this.finish();
			}
		}
	}
}
