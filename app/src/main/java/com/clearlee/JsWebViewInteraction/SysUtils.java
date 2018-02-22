package com.clearlee.JsWebViewInteraction;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by cy on 2017/5/13 0013.
 * 系统相关（系统的信息，包括调用系统功能）
 */

public class SysUtils {

    //华为参数
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String key_product_brand = "ro.product.brand";
    private static final String key_hw_emui_api_level = "ro.build.hw_emui_api_level";
    //小米参数
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    /**
     * 华为rom
     * @return
     */
    public static boolean isEMUI() {
        try {
            return BuildProperties.getInstance().getProperty(KEY_EMUI_VERSION_CODE, null) != null
                    || "huawei".equalsIgnoreCase(BuildProperties.getInstance().getProperty(key_product_brand))
                    || BuildProperties.getInstance().getProperty(key_hw_emui_api_level, null) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 小米rom
     * @return
     */
    public static boolean isMIUI() {
        try {
            return BuildProperties.getInstance().getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || BuildProperties.getInstance().getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || BuildProperties.getInstance().getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (final Exception e) {
            return false;
        }
    }

    //是否minu6
    public static boolean isMIUIV6() {
        try {
            if (!"Xiaomi".equalsIgnoreCase(Build.MANUFACTURER))
                return false;
            return "V6".equals(BuildProperties.getInstance().getProperty(KEY_MIUI_VERSION_NAME, ""));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 魅族rom
     * @return
     */
    public static boolean isFlyme() {
        return getSystemProperty("ro.build.display.id", "").toLowerCase().contains("flyme");
        //以下这个方式在我的meilan note1上行不通，可能是我关了SmartBar的原因
//        try {
//            final Method method = Build.class.getMethod("hasSmartBar");
//            return method != null;
//        } catch (final Exception e) {
//            return false;
//        }
    }

    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public static class BuildProperties {

        private static BuildProperties buildProperties;

        public static BuildProperties getInstance() throws IOException {
            if (buildProperties == null) {
                synchronized (BuildProperties.class) {
                    if (buildProperties == null) {
                        buildProperties = new BuildProperties();
                    }
                }
            }
            return buildProperties;
        }

        private final Properties properties;

        private BuildProperties() throws IOException {
            properties = new Properties();
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        }

        public boolean containsKey(final Object key) {
            return properties.containsKey(key);
        }

        public boolean containsValue(final Object value) {
            return properties.containsValue(value);
        }

        public Set<Map.Entry<Object, Object>> entrySet() {
            return properties.entrySet();
        }

        public String getProperty(final String name) {
            return properties.getProperty(name);
        }

        public String getProperty(final String name, final String defaultValue) {
            return properties.getProperty(name, defaultValue);
        }

        public boolean isEmpty() {
            return properties.isEmpty();
        }

        public Enumeration<Object> keys() {
            return properties.keys();
        }

        public Set<Object> keySet() {
            return properties.keySet();
        }

        public int size() {
            return properties.size();
        }

        public Collection<Object> values() {
            return properties.values();
        }

    }

    //检查系统中是否已经安装了adobe flash player
    public static boolean checkIfInstallFlash() {
        PackageManager pm = BaseApplication.getInstance().getPackageManager();
        List<PackageInfo> infoList = pm.getInstalledPackages(PackageManager.GET_SERVICES);
        for (PackageInfo info : infoList) {
            if ("com.adobe.flashplayer".equals(info.packageName)) {
                return true;
            }
        }
        return false;
    }

    //获取可以启动的应用
    public  static  List<ResolveInfo> getResolveInfo(){
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos= BaseApplication.getInstance().getPackageManager().queryIntentActivities(mainIntent,0);
        return resolveInfos;
    }

    //启动指定包名的应用
    public static void launchSpeicalApp(String packageName){
        try {
            if (packageName != null) {
                Intent intent = BaseApplication.getInstance().getPackageManager().getLaunchIntentForPackage(packageName);
                BaseActivity.currAct.startActivity(intent);
            }
        } catch (Exception e) {
            LogUtils.ex(e);
        }
    }

    //安装apk
    public static void installApk(File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            BaseActivity.currAct.startActivity(intent);

        } catch (Exception e) {
            LogUtils.ex(e);
        }
    }

    //添加进剪贴板
    public static void set2Clipboard(String str) {
        if (Build.VERSION.SDK_INT > 11) {
            android.content.ClipboardManager c = (android.content.ClipboardManager)BaseApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
            c.setPrimaryClip(ClipData.newPlainText(str, str));
        } else {
            android.text.ClipboardManager c = (android.text.ClipboardManager) BaseApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
            c.setText(str);
        }
    }

    /**判断一个service是否启动**/
    public static boolean checkService(String serviceName) {
        ActivityManager activityManager = (ActivityManager) BaseApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = (ArrayList) activityManager.getRunningServices(100);
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceName.equals(serviceList.get(i).service.getClassName().toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * 根据PID取得appname
     * @param pid
     * @return
     */
    public static String getAppNameByPid(int pid) {
        String processName = null;
        try {
            ActivityManager activityManager = (ActivityManager)BaseApplication.getInstance().getSystemService(ACTIVITY_SERVICE);
            List l = activityManager.getRunningAppProcesses();
            Iterator i = l.iterator();
            while (i.hasNext()) {
                ActivityManager.RunningAppProcessInfo runningAppProcessInfo = (ActivityManager.RunningAppProcessInfo) (i.next());
                try {
                    if (runningAppProcessInfo.pid == pid) {
                        processName = runningAppProcessInfo.processName;
                        return processName;
                    }
                } catch (Exception e) {
                }
            }
        }catch (Exception e){
            LogUtils.ex(e);
        }
        return processName;
    }

    //获得正在运行的应用进程信息
    public static List getRunningAppProcessInfo() {
        ActivityManager activityManager = (ActivityManager) BaseApplication.getInstance().getSystemService(ACTIVITY_SERVICE);

        List appProcessList = activityManager.getRunningAppProcesses();

        for (int i = 0; i < appProcessList.size(); i++) {
            ActivityManager.RunningAppProcessInfo appProcessInfo = (ActivityManager.RunningAppProcessInfo) appProcessList.get(i);
            //进程ID
            int pid = appProcessInfo.pid;
            //用户ID，类似于Linux的权限不同，ID也就不同， 比如root
            int uid = appProcessInfo.uid;
            //进程名，默认是包名或者由属性android:process=""指定
            String processName = appProcessInfo.processName;
            //获得该进程占用的内存
            int[] memPid = new int[]{pid};
            //此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
            Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(memPid);
            //获取进程占内存用信息kb单位
            int memSize = memoryInfo[0].dalvikPrivateDirty;

            LogUtils.v("process name: " + processName + " pid: " + pid + " uid: " + uid + " memory size is -->" + memSize + "kb");

            //获得每个进程里运行的应用程序(包)，即每个应用程序的包名
            String[] packageList = appProcessInfo.pkgList;
            for (String pkg : packageList) {
                LogUtils.v("package name " + pkg + " in process id is -->" + pid);
            }
        }

        return appProcessList;
    }

    //获得正在运行的service的信息
    public static List getRunningServiceInfo() {
        ActivityManager activityManager = (ActivityManager) BaseApplication.getInstance().getSystemService(ACTIVITY_SERVICE);

        List serviceList = activityManager.getRunningServices(200);

        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = (ActivityManager.RunningServiceInfo) serviceList.get(i);
            //进程ID
            int pid = serviceInfo.pid;
            //用户ID，类似于Linux的权限不同，ID也就不同， 比如root
            int uid = serviceInfo.uid;
            //进程名，默认是包名或者由属性android:process=""指定
            String processName = serviceInfo.process;

            String serviceStr = serviceInfo.toString();
            LogUtils.v("serviceInfo: " + serviceStr);

            //获得该进程占用的内存
            int[] memPid = new int[]{pid};
            //此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
            Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(memPid);
            //获取进程占内存用信息kb单位
            int memSize = memoryInfo[0].dalvikPrivateDirty;

            LogUtils.v("The name of the process this service runs in: " + processName + " pid: " + pid + " uid: " + uid + " memory size is -->" + memSize + "kb");
        }

        return serviceList;
    }

    //获得系统可用的内存大小
    public static long getSystemAvaialbeMemorySize() {
        ActivityManager activityManager = (ActivityManager) BaseApplication.getInstance().getSystemService(ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.availMem;

        LogUtils.v("getSystemAvaialbeMemorySize()...memory size: " + memSize);
        return memSize;

        //调用系统函数，字符串转换long -String KB/MB
//        return Formatter.formatFileSize(context, memSize);
    }

    //通过进程名杀死进程
    public static int killProcessByName(String killProcessName) {
        ActivityManager activityManager = (ActivityManager) BaseApplication.getInstance().getSystemService(ACTIVITY_SERVICE);

        int killNum = 0;
        List appProcessList = activityManager.getRunningAppProcesses();

        for (int i = 0; i < appProcessList.size(); i++) {
            ActivityManager.RunningAppProcessInfo appProcessInfo = (ActivityManager.RunningAppProcessInfo) appProcessList.get(i);
            //进程ID
            int pid = appProcessInfo.pid;
            //用户ID，类似于Linux的权限不同，ID也就不同， 比如root
            int uid = appProcessInfo.uid;
            //进程名，默认是包名或者由属性android:process=""指定
            String processName = appProcessInfo.processName;
//            //获得该进程占用的内存
//            int[] memPid = new int[]{ pid };
//            //此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
//            Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(memPid);
//            //获取进程占内存用信息kb单位
//            int memSize = memoryInfo[0].dalvikPrivateDirty;
//
//            System.out.println("process name: " + processName + " pid: " + pid + " uid: " + uid + " memory size is -->" + memSize + "kb");
//
//            //获得每个进程里运行的应用程序(包)，即每个应用程序的包名
//            String[] packageList = appProcessInfo.pkgList;
//            for(String pkg : packageList){
//                System.out.println("package name " + pkg + " in process id is -->" + pid);
//            }

            if (killProcessName.equals(processName)) {
                LogUtils.v("===============killProcess pid-->" + pid);
                android.os.Process.killProcess(pid);
                killNum++;
            }
        }
        return killNum;
    }

}
