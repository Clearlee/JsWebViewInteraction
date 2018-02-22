package com.clearlee.JsWebViewInteraction;

import android.app.Activity;
import android.os.Handler;


/**
 * Created by zerdoor_pc .
 * author:dc
 * 2016/12/23.
 */

public class LoadingUtil {
    Activity activity;

    public LoadingUtil(Activity activity) {
        this.activity = activity;
    }

    LoadingDialog dialog;

    public void start() {
        if (dialog == null || !dialog.isShowing()) {
            dialog = new LoadingDialog(activity);
        }
        dialog.setText("处理中");
        dialog.setCancelable(true);
        dialog.show();
    }

    public void start(String text) {
        try {
            if (dialog == null || !dialog.isShowing()) {
                dialog = new LoadingDialog(activity);
            }
            dialog.setText(text);
            dialog.setCancelable(true);
            dialog.show();
        }catch (Exception e){}
    }

    public void setText(String text) {
        if (dialog == null || !dialog.isShowing()) {
            dialog = new LoadingDialog(activity);
        }
        dialog.setText(text);

    }


    public void success(String text) {
        if (dialog != null && dialog.isShowing()) {
            if (text == null || text.length() == 0) text = "操作成功！";
            closeDialog();
        }
    }
    public void success() {
        if (dialog != null && dialog.isShowing()) {
            closeDialog();
        }
    }
    public void error(String text) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setText(text);
            closeDialog();
        }
    }


    public void dismissDialog(){
        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
                dialog = null;
            } catch (Exception e) {
                LogUtils.ex(e);
            }
        }
    }

    public void closeDialog() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing()) {
                    try {
                        dialog.dismiss();
                        dialog = null;
                    } catch (Exception e) {
                    }
                }
            }
        }, 1000);
    }

    public void closeDialogNow() {

        if (dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
                dialog = null;
            } catch (Exception e) {
            }
        }else if (dialog!=null){
            dialog=null;
        }
    }


    public void successFinish(String text) {
        if (dialog != null && dialog.isShowing()) {
            if (text == null || text.length() == 0) text = "操作成功！";
            dialog.setText(text);
            closeDialogFinish();
        }
    }

    public void errorFinish(String text) {
        if (dialog != null && dialog.isShowing()) {

            dialog.setText(text);
            closeDialogFinish();
        }

    }

    public void closeDialogFinish() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing()) {
                    try {
                        dialog.dismiss();
                        dialog = null;
                        activity.finish();
                    } catch (Exception e) {
                    }
                }
            }
        }, 1500);
    }

    private Handler handler = new Handler();
}
