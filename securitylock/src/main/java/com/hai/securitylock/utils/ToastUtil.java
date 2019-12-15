package com.hai.securitylock.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hai.securitylock.R;

public class ToastUtil {

    public static void show(Context context, int res){
        if(context == null) return;
        show(context, context.getString(res));
    }

    public static void show(Context context, String toast){
        show(context, toast, Gravity.NO_GRAVITY);
    }

    public static void show(Context context, String toast, int gravity){
        if(context == null || TextUtils.isEmpty(toast)) return;

        LayoutInflater in = LayoutInflater.from(context);
        View view = in.inflate(R.layout.view_toast, null);
        TextView tv = view.findViewById(R.id.toast);
        tv.setText(toast);
        Toast t = new Toast(context);
        t.setView(view);
        t.setGravity(gravity, 0, 0);
        t.show();
    }

}
