package com.clearlee.JsWebViewInteraction;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by cy on 2017/10/31 0031.
 * 日志工具类
 */

public class LogUtils {

    static String tag = "mylog";

    //控制台打印一个VERBOSE
    public static void v(Object obj) {
        try {
            Log.v(tag, String.valueOf(obj));
        }catch (Exception e){}
    }

    //控制台打印一个异常
    public static void ex(Throwable e) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        try{
            Log.e(tag, writer.toString());
        }catch (Exception e2){}
    }

}
