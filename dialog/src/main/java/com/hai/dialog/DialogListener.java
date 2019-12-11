package com.hai.dialog;

import android.app.Dialog;

import java.util.List;

public class DialogListener {

    public interface OnMessageSimpleDialogListener {
        void onOk(Dialog dialog);
    }

    public interface OnMessageDialogListener {
        void onLeft(Dialog dialog);
        void onRight(Dialog dialog);
    }

    public interface OnCheckListListener{
        void onOk(List<String> data);
    }

}
