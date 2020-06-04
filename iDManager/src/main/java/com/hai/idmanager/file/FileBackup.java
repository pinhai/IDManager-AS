package com.hai.idmanager.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hai.securitylock.utils.ToastUtil;
import com.hai.idmanager.comm.respentity.IdModel;
import com.hai.idmanager.utils.DateUtil;

public class FileBackup {
	private static String sdCardPath;
	private final static String appDirectory = "/IdManager";
	private final static String preFileName = "backup.id";
	
	/**
	 * 以json格式导出账号信息到文本
	 * @param idModels
	 * @return
	 */
	public static boolean exportIdInfo(List<IdModel> idModels){
		if(!Environment.isExternalStorageRemovable()){
			sdCardPath = Environment.getExternalStorageDirectory().toString();
			String fileName = preFileName + "." + DateUtil.getDateByFormat(System.currentTimeMillis(), "yyyyMMddHHmm");
			File file = new File(sdCardPath+appDirectory+"/"+fileName);
			File fileDir = new File(sdCardPath+appDirectory);
			BufferedWriter bw = null;
			if(!file.exists()){
				if(!fileDir.exists()){
					fileDir.mkdirs();
				}
				try {
					file.createNewFile();
					Gson gson = new Gson();
					String idInfoStr = gson.toJson(idModels);
					bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
					bw.write(idInfoStr);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}finally{
					try {
						bw.flush();
						bw.close();
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			}else{
				try {
					file.delete();
					file.createNewFile();
					Gson gson = new Gson();
					String idInfoStr = gson.toJson(idModels);
					bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
					bw.write(idInfoStr);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}finally{
					try {
						bw.flush();
						bw.close();
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			}
		}
		return true;
		
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
	public static boolean importIdInfo(Context context, Uri uri, FileDescriptor fileDescriptor, OnImportIdInfoListener onImportIdInfoListener){
		if(!uri.getPath().contains(preFileName)){
			ToastUtil.show(context, "不支持的文件类型！");
			return false;
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileDescriptor)));
			Gson gson = new Gson();
			List<IdModel> mIdModels = gson.fromJson(br, new TypeToken<List<IdModel>>(){}.getType());
			if(mIdModels.size() > 0){
				if(!onImportIdInfoListener.onImport(mIdModels)){
					ToastUtil.show(context, "导入失败");
					return false;
				}else{
					ToastUtil.show(context, "导入成功");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(context, "文件解析错误，可能已损坏");
			return false;
		}finally{
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
				ToastUtil.show(context, "文件解析错误，可能已损坏");
				return false;
			}
		}
		
		return true;
	}
	
	public interface OnImportIdInfoListener{
		boolean onImport(List<IdModel> idModels);
	}
	
}
