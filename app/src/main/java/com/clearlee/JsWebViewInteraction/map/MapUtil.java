package com.clearlee.JsWebViewInteraction.map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.clearlee.JsWebViewInteraction.BaseActivity;
import com.clearlee.JsWebViewInteraction.BaseApplication;
import com.clearlee.JsWebViewInteraction.LogUtils;

import java.util.List;

/**
 * Created by ZerdoorPHPDC on 2017/12/5 0005.
 */

public class MapUtil {

    public static void openTencentMap(String lat, String lng, String destination) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        Uri uri = Uri.parse("qqmap://map/routeplan?type=drive&to=" + destination + "&tocoord=" + lat + "," + lng);
        intent.setData(uri);
        if (intent.resolveActivity(BaseApplication.getInstance().getPackageManager()) != null) {
            BaseActivity.currAct.startActivity(intent);
        } else {
            Toast.makeText(BaseApplication.getInstance(), "您尚未安装腾讯地图", Toast.LENGTH_LONG).show();
        }
    }


    //http://lbsyun.baidu.com/index.php?title=uri/api/android
    public static void openBaiduMap(String lat, String lng, String destination) {
        if (isAvilible(BaseApplication.getInstance(), "com.baidu.BaiduMap")) {
            try {
                Intent intent = Intent.getIntent("intent://map/direction?" +
                        "destination=latlng:" + lat + "," + lng + "|name:" + destination +      //终点
                        "&mode=driving&" +          //导航路线方式
                        "&src=appname#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
               BaseActivity.currAct.startActivity(intent); //启动调用
            } catch (Exception e) {
                LogUtils.ex(e);
            }
        } else {
            Toast.makeText(BaseApplication.getInstance(), "您尚未安装百度地图", Toast.LENGTH_LONG).show();
            Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (intent.resolveActivity(BaseApplication.getInstance().getPackageManager()) != null) {
                BaseActivity.currAct.startActivity(intent);
            }
        }
    }

    //http://lbs.amap.com/api/amap-mobile/guide/android/route
    public static void openGaodeMap(String lat, String lng, String destination) {
        if (isAvilible(BaseApplication.getInstance(), "com.autonavi.minimap")) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);

            Uri uri = Uri.parse("amapuri://route/plan/?did=BGVIS2&dlat=" + lat + "&dlon=" + lng + "&dname=" + destination + "&dev=0&t=0");
            intent.setData(uri);

            BaseActivity.currAct.startActivity(intent);
        } else {
            Toast.makeText(BaseApplication.getInstance(), "您尚未安装高德地图", Toast.LENGTH_LONG).show();
            Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (intent.resolveActivity(BaseApplication.getInstance().getPackageManager()) != null) {
                BaseActivity.currAct.startActivity(intent);
            }
        }
    }

    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

}
