package com.hai.idmanager.view;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gyf.immersionbar.ImmersionBar;
import com.hai.idmanager.utils.ScreenUtil;
import com.hai.idmanager.view.gesture.GestureVerifyActivity;
import com.hai.idmanager.utils.PreferenceUtil;
import com.hai.idmanager.utils.ToastUtil;
import com.hai.idmanager.R;
import com.hai.idmanager.model.PageRequest;
import com.hai.idmanager.model.PageRequest.DoRequest;
import com.hai.idmanager.adapter.IdListAdapter;
import com.hai.idmanager.comm.FormResponse;
import com.hai.idmanager.comm.HomePageApi;
import com.hai.idmanager.comm.respentity.IdModel;
import com.hai.idmanager.comm.respentity.PageModel;
import com.hai.idmanager.custom.LoadMoreView;
import com.hai.idmanager.file.FileBackup;
import com.hai.idmanager.file.FileBackup.OnImportIdInfoListener;
import com.hai.idmanager.view.base.BaseActivity;
import com.hai.idmanager.view.setting.SettingActivity;
import com.hai.idmanager.widget.AddIdView;
import com.hai.idmanager.widget.AddIdView.OnAddIdListener;
import com.hai.idmanager.widget.DelIdView;
import com.hai.idmanager.widget.SearchIdInfoView;
import com.hai.idmanager.sqlite.DbHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.swipemenu.SwipeMenu;
import com.handmark.pulltorefresh.library.swipemenu.SwipeMenuCreator;
import com.handmark.pulltorefresh.library.swipemenu.SwipeMenuItem;
import com.handmark.pulltorefresh.library.swipemenu.SwipeMenuListView;
import com.handmark.pulltorefresh.library.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import com.handmark.pulltorefresh.library.PullToRefreshSwipeMenuListView;

public class MainActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "MainActivity";

	private Button btn_addId, btn_menu;
	private PullToRefreshSwipeMenuListView ptrlv_idInfo;
	private SwipeMenuListView smlv_idInfo;
	private LoadMoreView loadMoreView ;
	private List<IdModel> mIdModels;
	private IdListAdapter mIdListAdapter;

	private PageRequest<IdModel> mIdItemPage;

	private AddIdView addIdView;
	private DelIdView delIdView;
	private SearchIdInfoView searchIdInfoView;
	private DbHelper dbHelper;

	private static long firstBackTimeMills = 0;

	private ProgressDialog dialog;

	private static int itemEdited = -1;	//记录被编辑的账号item行数

	private LinearLayout linear_top;
	private static int searchBarHeight = -1;

	private LinearLayout linear_search;

	private static final int IMPORT_ID_CODE = 112;
	private static final int UPLOAD_FILE = 113;	//上传文件
	private static final int CREATE_FILE = 114; //创建要导出的文件

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showVerifyLayout();
		setContentView(R.layout.activity_main);

		initData();
		initView();
		initSystemBar();

//		boolean sdCardWritePermission =
//				getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;
//		if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission) {
//			requestPermission();
//		}
	}

	private void initSystemBar() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
			ImmersionBar.with(this)
					.titleBar(R.id.linear_top)
					.statusBarDarkFont(true)
					.init();
			setBottomPadding(findView(R.id.cl_bottom));
		}
	}

	private void initData() {
		dbHelper = new DbHelper(MainActivity.this);
		mIdItemPage = new PageRequest<IdModel>(idItemReq, idItemResp);
		loadMoreView = new LoadMoreView(this);

		mIdModels = new ArrayList<>();
		mIdListAdapter = new IdListAdapter(this, mIdModels);

	}

	private void initView() {
		btn_menu = findView(R.id.btn_menu);
		btn_menu.setOnClickListener(this);
		btn_addId = findView(R.id.btn_addId);
		btn_addId.setOnClickListener(this);
		ptrlv_idInfo = findView(R.id.ptrlv_idInfo);
		smlv_idInfo = ptrlv_idInfo.getRefreshableView();
		smlv_idInfo.addFooterView(loadMoreView.getView());
		smlv_idInfo.setAdapter(mIdListAdapter);
		smlv_idInfo.setOnItemClickListener(mIdInfoItemClick);
//		lv_idInfo.setOnItemLongClickListener(mIdItemLongClick);
		smlv_idInfo.setMenuCreator(creator);
		smlv_idInfo.setOnMenuItemClickListener(onMenuItemClickListener);
		ptrlv_idInfo.setOnLastItemVisibleListener(mLastItem);
		ptrlv_idInfo.setOnRefreshListener(onRefreshListener);

		dialog = new ProgressDialog(this);

		showWaitingDialog();
		mIdItemPage.init();

		linear_top = findView(R.id.linear_top);
		linear_top.post(new Runnable() {
			@Override
			public void run() {
				searchBarHeight = linear_top.getMeasuredHeight();
			}
		});

		linear_search = findView(R.id.linear_search);
		linear_search.setOnClickListener(this);

		addIdView = new AddIdView(this, LayoutInflater.from(this).inflate(R.layout.view_addid, null), onAddIdListener);

		initSearchIdWindow();
	}

	private void showVerifyLayout() {
		if(PreferenceUtil.hasGesturePsw()){
			GestureVerifyActivity.startForResult(MainActivity.this,
					GestureVerifyActivity.REQUEST_CODE_VERIFY_GESTURE_PSW2);
		}
	}

	private void requestPermission() {
		ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
				REQUEST_PERMISSION);
	}

	private static final int REQUEST_PERMISSION = 1002;
	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_PERMISSION) {
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			} else {
				ToastUtil.show(this, getString(R.string.permission_access_failed));
				System.exit(0);
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.item_export) {
			FileBackup.createIdInfoFile(MainActivity.this, CREATE_FILE);
			return true;
		}else if(id == R.id.item_import){
			//导入，启动文件管理器
			openFileBrowser(IMPORT_ID_CODE);
			return true;
		}else if(id == R.id.item_upload){
			//分享文件
			openFileBrowser(UPLOAD_FILE);
			return true;
		}/*else if(id == R.id.item_about){
			showAboutDialog();
		}*/else if(id == R.id.item_setup){
			Intent i = new Intent(MainActivity.this, SettingActivity.class);
			startActivity(i);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if(searchIdInfoView != null && searchIdInfoView.isShowing()){
				searchIdInfoView.dismiss();
			}else{
				long currentTimeMills = System.currentTimeMillis();
				if(currentTimeMills-firstBackTimeMills < 3000){
//					System.exit(0);
					MainActivity.this.finish();
				}else{
					ToastUtil.show(this, "再按一次退出程序");
					firstBackTimeMills = currentTimeMills;
					return true;
				}
			}
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.btn_addId){
			showAddIdWindow();
		}else if (id == R.id.linear_search) {
			showSearchIdWindow();
		}else if (id == R.id.btn_menu) {
			invalidateOptionsMenu();
			openOptionsMenu();
		}
	}

	private SwipeMenuCreator creator = new SwipeMenuCreator() {
		@Override
		public void create(SwipeMenu menu) {
			// create "delete" item
			SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
			// set item background
			deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
			// set item width
			deleteItem.setWidth(ScreenUtil.INSTANCE.dp2px(90));
			// set a icon
			deleteItem.setIcon(R.drawable.ic_delete);
			// add to menu
			menu.addMenuItem(deleteItem);
		}
	};

	private OnMenuItemClickListener onMenuItemClickListener = new OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
			switch (index) {
			case 0:
				deleteItem(position);
				break;

			default:
				break;
			}
			return false;
		}
	};

	/**
	 * 显示关于对话框
	 *
	 * @deprecated
	 */
	private void showAboutDialog(){
		PackageManager pm = getPackageManager();
		String vnStr = "";
		try{
			vnStr = "version：" + pm.getPackageInfo(getPackageName(), 0).versionName;
		}catch (PackageManager.NameNotFoundException e){
		}
		new AlertDialog.Builder(this, 0)
				.setTitle("关于")
				.setMessage(vnStr=="" ? "未获取到版本号" : vnStr)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create().show();
	}

	private void deleteItem(int position){
		if(dbHelper.delIdInfo(mIdModels.get(position).getId())){
			mIdModels.remove(position);
			mIdListAdapter.notifyDataSetChanged();
			ToastUtil.show(MainActivity.this, "删除账号成功");
		}else{
			ToastUtil.show(MainActivity.this, "删除账号失败");
		}
	}

	private void openFileBrowser(int requestCode){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		try{
			startActivityForResult(intent, requestCode);
		}catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(this, "未安装文件管理器");
		}
	}

	private void initSearchIdWindow(){
		searchIdInfoView = new SearchIdInfoView(this, dbHelper, new SearchIdInfoView.OnItemListener() {
			@Override
			public void onClick(int id) {
				searchIdInfoView.dismiss();
				IdModel idModel = dbHelper.queryIdInfoById(id);
				startEditIdActivityForResult(idModel);
			}

		});
	}

	private void showSearchIdWindow() {
		searchIdInfoView.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
		searchIdInfoView.showInputMethod();
	}

	private void showAddIdWindow() {
		addIdView.clearEditText();
		addIdView.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
	}

	private View getView() {
		return LayoutInflater.from(this).inflate(R.layout.activity_main, null);
	}

	private void showWaitingDialog(){
		dialog.setTitle("提示");
		dialog.setMessage("请稍等...");
		dialog.setCancelable(false);
		dialog.show();
	}

	private void startEditIdActivityForResult(IdModel itemModel) {
		Intent intent = new Intent(MainActivity.this, EditIdActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("itemModel", itemModel);
		intent.putExtra("data", bundle);
		startActivityForResult(intent, 110);
	}

	private OnAddIdListener onAddIdListener = new OnAddIdListener() {

		@Override
		public void onAddId(String idName, String idInfo) {
			dialog.dismiss();
			if(dbHelper.addIdInfo(new IdModel(idName, idInfo))){
				addIdView.dismiss();
				ToastUtil.show(MainActivity.this, "添加成功");
				mIdItemPage.init();
			}
		}

		@Override
		public void onAdding() {
			showWaitingDialog();
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == 110 && resultCode == 110 && itemEdited >= 0 && data != null){
			IdModel idModelEdited = (IdModel) data.getBundleExtra("data").getSerializable("itemModel");
			mIdModels.set(itemEdited-1, idModelEdited);
			mIdListAdapter.notifyDataSetChanged();
			ToastUtil.show(this, "账号修改成功");
		}else if(requestCode == IMPORT_ID_CODE && data != null){
			Uri uri = data.getData();
            FileBackup.importIdInfo(this, uri, onImportIdInfoListener);
        }else if(requestCode == UPLOAD_FILE && data != null){
			//上传
			Uri uri = data.getData();
			Log.v(TAG, "分享uri：" + uri.toString());
			shareFile(uri);
		}else if(requestCode == CREATE_FILE && resultCode == RESULT_OK && data != null){
			Uri uri = data.getData();
			if(FileBackup.exportIdInfo(MainActivity.this, uri, dbHelper.queryIdInfoByPage(0))){
				ToastUtil.show(this, "导出成功");
			}else{
				ToastUtil.show(this, "导出失败，请重试");
			}
		}
	}

	//分享文件
    private void shareFile(Uri uri) {
		try{
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.setType("*/*");
        	shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
			startActivity(shareIntent);
		}catch (Exception e){
			e.printStackTrace();
			ToastUtil.show(this, "必须选择IDManager目录下的文件");
		}
    }

	private DoRequest<IdModel> idItemReq = new DoRequest<IdModel>() {
		@Override
		public void doRequest(int page,
				FormResponse<PageModel<IdModel>> response) {
			HomePageApi homePageApi = new HomePageApi(MainActivity.this);
			homePageApi.getIdList(page, response);
		}
	};

	private FormResponse<PageModel<IdModel>> idItemResp = new FormResponse<PageModel<IdModel>>(){

		@Override
		public void onResponse(PageModel<IdModel> response) {
			if(mIdItemPage.isEnd()){
				loadMoreView.end();
			}else{
				loadMoreView.finish();
			}
			if(response.getIndex() == 1){
				mIdModels.clear();
				smlv_idInfo.setSelection(0);
			}
			mIdModels.addAll(response.getDatas());
			mIdListAdapter.notifyDataSetChanged();
			dialog.dismiss();
			ptrlv_idInfo.onRefreshComplete();
		}

		@Override
		public void onErrorResponse(int error) {
			ptrlv_idInfo.onRefreshComplete();
			ToastUtil.show(MainActivity.this, "数据获取失败");
		}

	};

	private OnItemClickListener mIdInfoItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Object tem = parent.getAdapter().getItem(position);
			if(tem instanceof IdModel){
				itemEdited = position;
				IdModel itemModel = (IdModel) tem;
				startEditIdActivityForResult(itemModel);
			}
		}

	};

	private OnLastItemVisibleListener mLastItem = new OnLastItemVisibleListener() {

		@Override
		public void onLastItemVisible() {
			if(!mIdItemPage.isEnd()){
				loadMoreView.loadData();
				mIdItemPage.next();
			}
		}
	};

	private OnRefreshListener<SwipeMenuListView> onRefreshListener = new OnRefreshListener<SwipeMenuListView>() {

		@Override
		public void onRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
			mIdItemPage.init();
		}
	};

	private OnImportIdInfoListener onImportIdInfoListener = new OnImportIdInfoListener() {

		@Override
		public boolean onImport(List<IdModel> idModels) {
			for(IdModel idModel : idModels){
				if(!dbHelper.addIdInfo(idModel)){
					return false;
				}
			}
			mIdModels.addAll(idModels);
			mIdListAdapter.notifyDataSetChanged();
			return true;
		}
	};

}
