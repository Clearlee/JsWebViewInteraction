package com.clearlee.JsWebViewInteraction;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by cy on 2017/10/31 0031.
 * 弹出提示相关
 */

public class PopTipUtils {

    /**** 显示toast **/
    static Toast toast;
    public static void showToast(final String text, final int lengthShort, final int position) {
        try {
            if (!TextUtils.isEmpty(text) && BaseActivity.currAct != null) {
                BaseActivity.currAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (toast == null) {
                            toast = Toast.makeText(BaseActivity.currAct, text, lengthShort);
                        } else {
                            toast.setText(text);
                        }
                        toast.setGravity(position, 0 , 0);
                        toast.show();
                        LogUtils.v(" toast： " + text);
                    }
                });
            }
        }catch (Exception e){
            LogUtils.ex(e);
        }
    }
    public static void showToast(String text) {
        showToast(text, Toast.LENGTH_SHORT, Gravity.CENTER_HORIZONTAL);
    }

    //弹出日期选择
    public static void showDateSelectDialog(final TextView textView){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(BaseApplication.getInstance(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                textView.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        datePickerDialog.show();
    }

    //------------------------------等待框，下----------------------------------
    static Dialog waitDialog;
    public static String showWaitDialogId = "";//显示的等待框ID
    static boolean prepareShowWaitDialog = false;//准备显示等待框

    /**
     * 显示等待框
     * @param layoutId 要显示的 layout 文件, <1 则用默认的
     **/
    public static Dialog showWaitDialog(final int layoutId) {
        try {
            prepareShowWaitDialog = true;
            BaseActivity.currAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //可能准备显示时又开始隐藏了
                    if (!prepareShowWaitDialog) return;

                    int layout = layoutId < 1 ? R.layout.dialog_wait : layoutId;

                    String currId = BaseActivity.currAct.toString() + layout;

                    //如果已经显示等待框
                    if (waitDialog != null && waitDialog.isShowing() ) {
                        if(!showWaitDialogId.equals(currId) && !TextUtils.isEmpty(showWaitDialogId)){//显示框ID不等于当前要显示的ID,就先隐藏掉
                            hideWaitIngDialog();
                        }else{//因为显示框正好和当前要显示的ID一样，就不处理了
                            return;
                        }
                    }

                    try {
                        waitDialog = new Dialog(BaseActivity.currAct, R.style.theme_dialog_wait);
                        waitDialog.setCancelable(true);
                        waitDialog.setCanceledOnTouchOutside(false);
                        waitDialog.setContentView(layout);
                        waitDialog.show();
                        showWaitDialogId = currId;
                    } catch (Exception e) {
                        LogUtils.ex(e);
                    }
                }
            });
        } catch (Exception e) {
            LogUtils.ex(e);
        }
        return waitDialog;
    }

    //隐藏等待框
    public static void hideWaitIngDialog() {
        try {
            prepareShowWaitDialog = false;

            if (waitDialog != null && waitDialog.isShowing()) {
                waitDialog.dismiss();
            }
        } catch (Exception e) {
            LogUtils.ex(e);
        }finally {
            showWaitDialogId = "";
        }
    }
    //------------------------------等待框，上----------------------------------

}
