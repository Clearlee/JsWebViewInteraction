package com.clearlee.JsWebViewInteraction;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cy on 2017/10/31 0031.
 * 运行时权限相关
 */

public class RuntimePermissionUtils {

    public static final int REQUEST_PERMISSION_CODE = 124;

    /**
     * 请求应用需要的运行时权限
     *
     * @param permissionMap key 是系统权限名，value是我们自己提示的权限中文名
     * @return 返回false就表示已经不需要请求权限了
     */
    public static boolean verifyRuntimePermissions(final Activity activity, HashMap<String, String> permissionMap) {
        List<String> permissionChineseList = new ArrayList<String>();//需要我们来提示需要的权限（提示中文名）
        final List<String> permissionList = new ArrayList<String>();//需要的所有权限(系统权限名)
        //添加需要的权限
        for (Map.Entry entry : permissionMap.entrySet()) {
            if (!addPermission(activity, permissionList, String.valueOf(entry.getKey())))
                permissionChineseList.add(String.valueOf(entry.getValue()));
        }
        if (permissionList.size() > 0) {//如果有权限需要允许
            if (permissionChineseList.size() > 0) {//如果有权限需要我们提示
                String message = "您需要允许以下权限，否则应用功能可能异常:";
                for (int i = 0; i < permissionChineseList.size(); i++)
                    message = message + (i == 0 ? "" : ",") + permissionChineseList.get(i);
                showOurRationale(activity, message, permissionList);
            } else {
                ActivityCompat.requestPermissions(activity,
                        permissionList.toArray(new String[permissionList.size()]),
                        REQUEST_PERMISSION_CODE);
            }
            return true;
        } else {
            return false;
        }
    }

    //添加权限, 如果有的权限，系统已经忽略了(被用户不再提示了)，就返回false，由我们自己提示
    public static boolean addPermission(Activity activity, List<String> permissionsList, String permission) {

        boolean noAuth = false;//是否授权
        if (true) {
            noAuth = ContextCompat.checkSelfPermission(BaseApplication.getInstance(), permission) != PackageManager.PERMISSION_GRANTED;
        } else {
            noAuth = !grantPermission(permission);
        }
        if (noAuth) {
            permissionsList.add(permission);
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))//第一次全新进入时，shouldShowRequestPermissionRationale方法将返回false
                return false;
        }
        return true;
    }

    //显示我们对权限的描述框
    public static void showOurRationale(final Activity activity, String rationale, final List<String> permissionsList) {
        final boolean firstButNotGetUnAuthPermi = (permissionsList == null || permissionsList.size() == 0);//如果第一次开APP但没有检测到未授权的权限

        AlertDialog.Builder builder = new AlertDialog
                                        .Builder(activity)
                                        .setMessage(rationale)
                                        .setPositiveButton(firstButNotGetUnAuthPermi ? "去设置" : "确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (!firstButNotGetUnAuthPermi) {
                                                    ActivityCompat.requestPermissions(activity,
                                                            permissionsList.toArray(new String[permissionsList.size()]),
                                                            REQUEST_PERMISSION_CODE);
                                                } else {
                                                    PageUtils.toPermissionPage();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
        if (firstButNotGetUnAuthPermi) {
            builder.setNegativeButton("不用", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    //请求相关权限，并返回是否授权
    public static boolean grantPermission(String permission) {
        int state = PermissionChecker.checkPermission(BaseApplication.getInstance(), permission, Process.myPid(), Process.myUid(), BaseApplication.getInstance().getPackageName());
        return state == PermissionChecker.PERMISSION_GRANTED;
    }

    //判断小米手机打电话权限有没勾选
    public static boolean miuiCheckedCallPhonePermission(Context context) {
        try {
            if (ContextCompat.checkSelfPermission(BaseApplication.getInstance(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                    && AppOpsManagerMode(context, AppOpsManager.OPSTR_CALL_PHONE) == 0) {
                return true;
            }
        } catch (Exception e) {
            LogUtils.ex(e);
        }
        return false;
    }

    //判断小米手机发短信权限有没勾选
    public static boolean miuiCheckedSendSmsPermission(Context context) {
        try {
            if (ContextCompat.checkSelfPermission(BaseApplication.getInstance(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                    && AppOpsManagerMode(context, AppOpsManager.OPSTR_SEND_SMS) == 0) {
                return true;
            }
        } catch (Exception e) {
            LogUtils.ex(e);
        }
        return false;
    }

    public static int AppOpsManagerMode(Context context, String opstr) {
        try {
            if (Build.VERSION.SDK_INT >= 19) {
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                return appOpsManager.checkOp(opstr, Process.myUid(), context.getPackageName());
            }
        } catch (Exception e) {
            LogUtils.ex(e);
        }
        return 0;//19以下，直接返回允许状态
    }

}
