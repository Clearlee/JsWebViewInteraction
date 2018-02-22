package com.clearlee.JsWebViewInteraction;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

/**
 * Created by cy on 2017/10/31 0031.
 * 页面相关
 */

public class PageUtils {

    public static void startActivity(Class<?> target, Intent intent) {
        try {
            if(intent == null){
                intent = new Intent();
            }
            intent.setClass(BaseApplication.getInstance(), target);
            BaseActivity.currAct.startActivity(intent);
        }catch (Exception e){
            LogUtils.ex(e);
        }
    }

    public static void startActivityForResult(Activity fromActivity, Class<?> target, Intent intent, int requestCode){
        if(intent == null){
            intent = new Intent();
        }
        intent.setClass(BaseApplication.getInstance(), target);
        fromActivity.startActivityForResult(intent, requestCode);
    }

    public static void startActivityByName(String className, Intent intent) {
        try {
            if(intent == null){
                intent = new Intent();
            }
            intent.setClass(BaseApplication.getInstance(), Class.forName(className));
            BaseActivity.currAct.startActivity(intent);
        }catch (Exception e){
            LogUtils.ex(e);
        }
    }

    public static void toPermissionPage() {
        //Build.MANUFACTURER：OnePlus：一加手机，
        if(SysUtils.isMIUI()){
            gotoMiuiPermission();
        }
        else if(SysUtils.isEMUI()){
            gotoHuaweiPermission();
        }
        else if(SysUtils.isFlyme()){
            gotoMeizuPermission();
        }
        else if("OnePlus".equalsIgnoreCase(Build.MANUFACTURER)){
            gotoOnePlusPermission();
        }
        else{
            goAppDetail();
        }
    }

    /**
     * 跳转到miui的权限管理页面
     */
    private static void gotoMiuiPermission() {
        Intent i = new Intent();
        ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        i.setComponent(componentName);
        i.putExtra("extra_pkgname", BaseApplication.getInstance().getPackageName());

        try {
            BaseActivity.currAct.startActivity(i);
        } catch (Exception e) {
            LogUtils.ex(e);
            goAppDetail();
        }
    }

    /**
     * 华为的权限管理页面
     */
    private static void gotoHuaweiPermission() {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.android.packageinstaller", "com.android.packageinstaller.permission.ui.ManagePermissionsActivity");//华为权限组管理
            intent.setComponent(comp);
            intent.putExtra("packageName", BaseApplication.getInstance().getPackageName());
            BaseActivity.currAct.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.SingleAppActivity");//华为权限管理
                intent.setComponent(comp);
                intent.putExtra("packageName", BaseApplication.getInstance().getPackageName());
                BaseActivity.currAct.startActivity(intent);
            }catch (Exception e2){
                LogUtils.ex(e2);
                goAppDetail();
            }
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    private static void gotoMeizuPermission() {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
            intent.putExtra("packageName", BaseApplication.getInstance().getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (isIntentAvailable(intent)) {
                BaseActivity.currAct.startActivity(intent);
            } else {
                goAppDetail();
            }
        }catch (Exception e){
            LogUtils.ex(e);
            goAppDetail();
        }
    }
    private static boolean isIntentAvailable(Intent intent) {
        return intent != null && BaseApplication.getInstance().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    /**
     * 跳转到一加的权限管理页面
     */
    private static void gotoOnePlusPermission() {
        Intent i = new Intent();
        ComponentName componentName = new ComponentName("com.oneplus.security", "com.oneplus.security.oppermission.OPPermissionAppPermListActivity");
        i.setComponent(componentName);
        i.putExtra("packageName", BaseApplication.getInstance().getPackageName());
        try {
            BaseActivity.currAct.startActivity(i);
        } catch (Exception e) {
            LogUtils.ex(e);
            goAppDetail();
        }
    }

    //跳到系统应用详情页
    private static void goAppDetail() {
        try {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", BaseApplication.getInstance().getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", BaseApplication.getInstance().getPackageName());
            }
            BaseActivity.currAct.startActivity(localIntent);
        }catch (Exception e){
            LogUtils.ex(e);
        }
    }

    //跳转到浏览器
    public static void goUrl(String url) {
        try{
            final Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            BaseActivity.currAct.startActivity(intent);
        }catch ( Exception e){
            LogUtils.ex(e );
        }
    }
}
