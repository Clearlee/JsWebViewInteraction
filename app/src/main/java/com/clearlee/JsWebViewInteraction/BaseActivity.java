package com.clearlee.JsWebViewInteraction;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import static com.clearlee.JsWebViewInteraction.JSInterface.executeJSFunction;


/**
 * Created by cy on 2017/10/31 0031.
 */

public class BaseActivity extends AppCompatActivity {

    public static BaseActivity currAct;
    public static Stack<Activity> activityStack = new Stack<Activity>();

    public static String waitCallPhoneNum = "";


    public HashMap<String, String> permissionMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(currAct==null){
            permissionMap.put(Manifest.permission.ACCESS_NETWORK_STATE, "网络状态");
            permissionMap.put(Manifest.permission.ACCESS_WIFI_STATE, "WIFI状态");
            permissionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写文件");
            permissionMap.put(Manifest.permission.BLUETOOTH, "蓝牙");
            permissionMap.put(Manifest.permission.INTERNET, "网络");
            permissionMap.put(Manifest.permission.READ_PHONE_STATE, "读电话状态");
            permissionMap.put(Manifest.permission.PROCESS_OUTGOING_CALLS, "拨打电话状态");
            permissionMap.put(Manifest.permission.READ_CALL_LOG, "读通话记录");
            permissionMap.put(Manifest.permission.SEND_SMS, "发短信");
            permissionMap.put(Manifest.permission.READ_SMS, "读短信");
            permissionMap.put(Manifest.permission.CALL_PHONE, "打电话");
            permissionMap.put(Manifest.permission.ACCESS_FINE_LOCATION, "GPS");

            if (permissionMap.size() > 0)
                RuntimePermissionUtils.verifyRuntimePermissions(this, permissionMap);
        }

        activityStack.add(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        activityStack.remove(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RuntimePermissionUtils.REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.CALL_PHONE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {//授权了打电话权限
                    CommonUtils.callPhone(waitCallPhoneNum);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentAlbumUtils.getInstence().onActivityResult(requestCode, resultCode, data, new IntentAlbumUtils.PhoteSuccess() {
            @Override
            public void setPhote(String path) {

                final String uploadedUrl = "具体项目里上传后把服务端图片URL返回给JS";

                executeJSFunction(BaseApplication.getInstance().currShowWebView, JSInterface.uploadSelectedImgJsCallback, new ArrayList<Object>(){{add(0); add(uploadedUrl);}});

            }

            @Override
            public void setFile(File file) {

            }
        });


        super.onActivityResult(requestCode, resultCode, data);

    }



    @Override
    protected void onResume() {
        super.onResume();
        currAct =  this;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        switch (event.getKeyCode()){
//            case KeyEvent.KEYCODE_BACK:
//                LogUtils.v("=KEYCODE_BACK=");
//                return true;
//            case KeyEvent.KEYCODE_MENU:
//                LogUtils.v("=KEYCODE_MENU=");
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_UP:
//                LogUtils.v("=KEYCODE_VOLUME_UP=");
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                LogUtils.v("=KEYCODE_VOLUME_DOWN=");
//                return true;
//        }
        return super.dispatchKeyEvent(event);
    }

}
