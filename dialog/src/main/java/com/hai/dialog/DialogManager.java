package com.hai.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hai.dialog.adapter.ListCheckAdapter;
import com.hai.dialog.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DialogManager {

//    private final Context context;
//
//    public DialogManager(Context context){
//        this.context = context;
//    }

    private static DialogManager instance;
    private DialogManager(){}
    public static DialogManager getInstance(){
        if(instance == null){
            instance = new DialogManager();
        }
        return instance;
    }

    /**
     * 中间对话框
     */
    public Dialog getCenterDialog(Context context, View view){
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        if (dialog != null && window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ScreenUtils.getScreenWidth(context)*3/4;
                attr.gravity = Gravity.CENTER;//设置dialog 在布局中的位置
                window.setAttributes(attr);
            }
        }

        return dialog;
    }

    /**
     * 底部对话框
     */
    private Dialog getBottomDialog(Context context, View view){
        return getBottomDialog(context, view, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private Dialog getBottomDialog(Context context, View view, int height){
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        if (dialog != null && window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = height;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.gravity = Gravity.BOTTOM;//设置dialog 在布局中的位置
                window.setAttributes(attr);
            }
        }

        return dialog;
    }

    public void showMultiCheckListDialog(Context context, List<String> data, String chooseStr, final DialogListener.OnCheckListListener listListener, String title){
        showMultiCheckListDialog(context, data, chooseStr, listListener, ScreenUtils.getScreenHeight(context)/2, title);
    }

    /**
     * 多选对话框
     *
     * @param choosedStr 已经被选中的选项
     */
    public void showMultiCheckListDialog(Context context, List<String> data, String choosedStr, final DialogListener.OnCheckListListener listListener, int height, String title){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_list_check, null);
        final Dialog dialog = getBottomDialog(context, view, height);
        dialog.show();

        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(title);

        final List<String> result = new ArrayList<>();
        List<Boolean> checkItems = null;
        if(choosedStr != null){
            checkItems = new ArrayList<>();
            for(int i=0; i<data.size(); i++){
                if(choosedStr.contains(data.get(i))){
                    checkItems.add(true);
                    result.add(data.get(i));
                }else {
                    checkItems.add(false);
                }
            }
        }

        ListView listView = view.findViewById(R.id.list_view);
        ListCheckAdapter adapter = new ListCheckAdapter(context, data, checkItems, new ListCheckAdapter.OnItemCheckListener<String>() {
            @Override
            public String getText(String item) {
                return item;
            }

            @Override
            public boolean onItemCheck(String item, int position, boolean isChecked) {
                if(isChecked){
                    result.add(item);
                    return true;
                }else{
                    result.remove(item);
                    return false;
                }
            }
        });
        listView.setAdapter(adapter);

        TextView tv_ok = view.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listListener.onOk(result);
            }
        });
    }

    /**
     * 列表对话框
     */
    public void showListDialog(Context context, List<String> data, final AdapterView.OnItemClickListener listener, String title, int height){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null);
        final Dialog dialog = getBottomDialog(context, view, height);
        dialog.show();

        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(title);
        ListView listView = view.findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.dialog_list_item_txt, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onItemClick(parent, view, position, id);
                dialog.dismiss();
            }
        });
    }

    /**
     * 简单样式对话框，单按钮
     */
    public Dialog showMessageSimpleDialog(Context context, String title, String msg, final DialogListener.OnMessageSimpleDialogListener listener){
        return showMessageSimpleDialog(context, title, msg, context.getString(R.string.ok), false, true, listener);
    }

    public Dialog showMessageSimpleDialog(Context context, String title, String msg, final String actionMsg, boolean cancelOnTouchOutside,
                                          boolean cancelable, final DialogListener.OnMessageSimpleDialogListener listener){
        return showMessageSimpleDialog(context, title, msg, actionMsg, true, cancelOnTouchOutside, cancelable, listener);
    }

    public Dialog showMessageSimpleDialog(Context context, String title, String msg, final String actionMsg, final boolean dismissDialog, boolean cancelOnTouchOutside,
                                          boolean cancelable, final DialogListener.OnMessageSimpleDialogListener listener){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_message_simple, null);
        final Dialog dialog = getCenterDialog(context, view);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
        dialog.setCancelable(cancelable);
        dialog.show();

        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_msg = view.findViewById(R.id.tv_message);
        tv_title.setText(title);
        tv_msg.setText(msg);
        final TextView tv_ok = view.findViewById(R.id.tv_ok);
        if(!TextUtils.isEmpty(actionMsg)){
            tv_ok.setText(actionMsg);
        }
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onOk(dialog);
                }
                if(dismissDialog){
                    dialog.dismiss();
                }
            }
        });

        return dialog;
    }

    /**
     * 简单样式对话框，双按钮
     */
    public Dialog showMessageDialog(Context context, @Nullable String title, String msg, @Nullable String left, @Nullable String right,
                                    final DialogListener.OnMessageDialogListener listener){
        return showMessageDialog(context, title, msg, left, right, false, true, listener);
    }

    public Dialog showMessageDialog(Context context, @Nullable String title, String msg, @Nullable String left, @Nullable String right,
                                    boolean cancelOnTouchOutside, boolean cancelable,
                                    final DialogListener.OnMessageDialogListener listener){
        return showMessageDialog(context, title, msg, left, right, cancelOnTouchOutside, cancelable, Gravity.CENTER, listener);
    }

    public Dialog showMessageDialog(Context context, @Nullable String title, String msg, @Nullable String left, @Nullable String right,
                                    boolean cancelOnTouchOutside, boolean cancelable, int gravity,
                                    final DialogListener.OnMessageDialogListener listener){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
        final Dialog dialog;
        if(gravity == Gravity.CENTER){
            dialog = getCenterDialog(context, view);
        }else {
            dialog = getBottomDialog(context, view);
        }
        dialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
        dialog.setCancelable(cancelable);
        dialog.show();

        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_msg = view.findViewById(R.id.tv_message);
        if(TextUtils.isEmpty(title)){
            tv_title.setVisibility(View.GONE);
        }else {
            tv_title.setText(title);
        }
        tv_msg.setText(msg);
        TextView tv_left = view.findViewById(R.id.tv_left);
        if(!TextUtils.isEmpty(left)){
            tv_left.setText(left);
        }
        tv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLeft(dialog);
            }
        });
        TextView tv_right = view.findViewById(R.id.tv_right);
        if(!TextUtils.isEmpty(right)){
            tv_right.setText(right);
        }
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRight(dialog);
            }
        });

        return dialog;
    }

    private HashMap<Context, Dialog> indeterminateDialogs;
    private Dialog createIndeterminateDialog(Context context){
        Dialog indeterminateDialog = new Dialog(context, R.style.new_circle_progress);

        if(indeterminateDialogs == null){
            indeterminateDialogs = new HashMap<>();
        }
        indeterminateDialogs.put(context, indeterminateDialog);

        return indeterminateDialog;
    }

    /**
     * 显示单个旋转图片对话框
     */
    public void showIndeterminateDialog(Context context) {
       showIndeterminateDialog(context, true);
    }

    public void showIndeterminateDialog(Context context, boolean cancelable) {
        showIndeterminateDialog(context, cancelable, null);
    }

    public void showIndeterminateDialog(final Context context, boolean cancelable, final String content) {
        Dialog indeterminateDialog = createIndeterminateDialog(context);
        indeterminateDialog.setCancelable(cancelable);
        indeterminateDialog.setCanceledOnTouchOutside(false);
        View v = LayoutInflater.from(context).inflate(R.layout.view_indeterminate_progress, null);
        TextView tv_content = v.findViewById(R.id.tv_content);
        if (!TextUtils.isEmpty(content)) {
            tv_content.setText(content);
            tv_content.setVisibility(View.VISIBLE);
        }else {
            tv_content.setVisibility(View.GONE);
        }
        indeterminateDialog.setContentView(v);
        indeterminateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(context != null) indeterminateDialogs.remove(context);
            }
        });
        indeterminateDialog.show();
    }

    public void dismissIndeterminateDialog(Context context) {
        Dialog indeterminateDialog = indeterminateDialogs.get(context);
        if (indeterminateDialog != null && indeterminateDialog.isShowing()) {
            indeterminateDialog.dismiss();
        }
    }

}
