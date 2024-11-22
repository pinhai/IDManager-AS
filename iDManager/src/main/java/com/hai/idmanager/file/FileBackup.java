package com.hai.idmanager.file;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hai.idmanager.comm.respentity.IdModel;
import com.hai.idmanager.utils.ToastUtil;
import com.hai.idmanager.utils.DateUtil;

public class FileBackup {
	private static String sdCardPath;
	private final static String appDirectory = "/IdManager";
	private final static String preFileName = "backup.id";

	public static void createIdInfoFile(Activity activity, int requestCode){
		String fileName = preFileName + "." + DateUtil.getDateByFormat(System.currentTimeMillis(), "yyyyMMddHHmmss");

		Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TITLE, fileName);

		// Optionally, specify a URI for the directory that should be opened in
		// the system file picker when your app creates the document.
//		intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

		activity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 以json格式导出账号信息到文本
	 * @param idModels
	 * @return
	 */
	public static boolean exportIdInfo(Context context, Uri uri, List<IdModel> idModels){
		ContentResolver contentResolver = context.getContentResolver();
		try {
			OutputStream outputStream = contentResolver.openOutputStream(uri);
			Gson gson = new Gson();
			String idInfoStr = gson.toJson(idModels);
			// 写入数据到输出流
			outputStream.write(idInfoStr.getBytes());
			outputStream.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 导入信息
	 * @param context
	 * @param uri
	 * @param onImportIdInfoListener
	 * @return
	 */
	@SuppressLint("SdCardPath")
	@SuppressWarnings({ })
	public static boolean importIdInfo(Context context, Uri uri, OnImportIdInfoListener onImportIdInfoListener){
		if(!uri.getPath().contains(preFileName)){
			ToastUtil.show(context, "不支持的文件类型！");
			return false;
		}
		BufferedReader br = null;
		try {
			ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileDescriptor)));
			Gson gson = new Gson();
			List<IdModel> mIdModels = gson.fromJson(br, new TypeToken<List<IdModel>>(){}.getType());
			if(!mIdModels.isEmpty()){
				if(!onImportIdInfoListener.onImport(mIdModels)){
					ToastUtil.show(context, "导入失败");
					return false;
				}else{
					ToastUtil.show(context, "导入成功");
				}
			}
			br.close();
			parcelFileDescriptor.close();
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(context, "文件解析错误，可能已损坏");
			return false;
		}
//		finally{
//			try {
//				br.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//				ToastUtil.show(context, "文件解析错误，可能已损坏");
//				return false;
//			}
//		}
		
		return true;
	}
	
	public interface OnImportIdInfoListener{
		boolean onImport(List<IdModel> idModels);
	}
	
}
