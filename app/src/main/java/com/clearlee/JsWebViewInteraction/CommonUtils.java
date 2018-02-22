package com.clearlee.JsWebViewInteraction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by cy on 2018/2/5 0005.
 */

public class CommonUtils {

    //打电话
    public static void callPhone(final String phone) {
        if (TextUtils.isEmpty(phone)) {
            PopTipUtils.showToast("手机号码为空");
            return;
        }
        try {

            if(!RuntimePermissionUtils.grantPermission(Manifest.permission.CALL_PHONE)){
                HashMap<String, String> permissionMap = new HashMap<String, String>();
                permissionMap.put(Manifest.permission.CALL_PHONE, "打电话");
                BaseActivity.waitCallPhoneNum = phone;
                RuntimePermissionUtils.verifyRuntimePermissions(BaseActivity.currAct, permissionMap);
                return;
            }

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(BaseActivity.currAct);
            alertDialog.setTitle("提示");
            alertDialog.setMessage("是否拨打 " + phone);
            alertDialog.setPositiveButton("拨打", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                    BaseActivity.currAct.startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("取消", null).create();
            alertDialog.show();
        } catch (Exception e) {
            LogUtils.ex(e);
        }
    }

    //字符串转为urlencode
    public static String toURLEncode(String str) {
        try {
            if(!TextUtils.isEmpty(str)){
                str = new String(str.getBytes(), "UTF-8");
                str = URLEncoder.encode(str, "utf-8");
                return str;
            }
        }catch (Exception e){
            LogUtils.ex(e);
        }
        return "";
    }

}
