package com.clearlee.JsWebViewInteraction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.clearlee.JsWebViewInteraction.map.MapUtil;
import com.clearlee.JsWebViewInteraction.payment.ali.AlipayUser;
import com.clearlee.JsWebViewInteraction.payment.wx.WxpayUser;
import com.clearlee.JsWebViewInteraction.share.ShareSDKUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import static com.clearlee.JsWebViewInteraction.BaseActivity.activityStack;
import static com.clearlee.JsWebViewInteraction.CommonCallbackHandler.loginCallbackHandler;
import static com.clearlee.JsWebViewInteraction.IntentAlbumUtils.PHOTO_REQUEST_GALLERY;
import static com.clearlee.JsWebViewInteraction.IntentAlbumUtils.PHOTO_REQUEST_TAKEPHOTO;

/**
 * Created by cy on 2018/2/2 0002.
 */

public class JSInterface {

    public static String uploadSelectedImgJsCallback = "";
    public static String paymentJsCallback = "";
    public static String shareJsCallback = "";
    public static String thirdPartyLoginJsCallback = "";
    public static String locateJsCallback = "";

    //跳转到指定网页
    @JavascriptInterface
    public void goWebpage(String url, int goType, String jsCallback) {
        try {
            final String url_final;
            if (!url.startsWith("http")) {
                url_final = "file:///android_asset/" + url;
            } else {
                url_final = url;
            }
            switch (goType) {
                case 0://系统浏览器打开
                    PageUtils.goUrl(url);
                    break;
                case 1://当前webview页面打开
                    BaseApplication.getInstance().currShowWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            BaseApplication.getInstance().currShowWebView.loadUrl(url_final);
                        }
                    });
                    break;
                case 2://在新的webview页面打开
                    WebActivity.go(url, false);
                    break;
            }
            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(0);
                add("成功");
            }});
        } catch (final Exception e) {
            LogUtils.ex(e);
            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(-1);
                add("失败:" + e.getMessage());
            }});
        }
    }

    //跳转到指定的原生页面
    @JavascriptInterface
    public void goNativePage(String pageClassName, String paramStr, String jsCallback) {
        try {
            Intent intent = new Intent();
            if (!TextUtils.isEmpty(paramStr)) {
                Map<String, Object> map = new Gson().fromJson(paramStr, new TypeToken<HashMap<String, Object>>() {
                }.getType());
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getValue() instanceof Integer) {
                        intent.putExtra(entry.getKey(), ((Integer) entry.getValue()).intValue());
                    } else if (entry.getValue() instanceof String) {
                        intent.putExtra(entry.getKey(), ((String) entry.getValue()));
                    } else if (entry.getValue() instanceof Double) {
                        intent.putExtra(entry.getKey(), ((Double) entry.getValue()).doubleValue());
                    } else if (entry.getValue() instanceof Float) {
                        intent.putExtra(entry.getKey(), ((Float) entry.getValue()).floatValue());
                    } else if (entry.getValue() instanceof Long) {
                        intent.putExtra(entry.getKey(), ((Long) entry.getValue()).longValue());
                    } else if (entry.getValue() instanceof Boolean) {
                        intent.putExtra(entry.getKey(), ((Boolean) entry.getValue()).booleanValue());
                    }
                }
            }
            PageUtils.startActivityByName(pageClassName, intent);
            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(0);
                add("成功");
            }});
        } catch (final Exception e) {
            LogUtils.ex(e);
            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(-1);
                add("失败:" + e.getMessage());
            }});
        }
    }

    //原生返回页面
    @JavascriptInterface
    public void goBack(int index, String jsCallback) {
        try {
            if (index == 0) {
                index = activityStack.size() - 1;
            }
            for (int i = activityStack.size() - 1; i >= 0; i--) {
                if (index > 0) {
                    if (null != activityStack.get(i)) {
                        activityStack.get(i).finish();
                    }
                    index--;
                } else {
                    break;
                }
            }

            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(0);
                add("成功");
            }});
        } catch (final Exception e) {
            LogUtils.v(e);
            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(-1);
                add("失败:" + e.getMessage());
            }});
        }
    }

    //打电话
    @JavascriptInterface
    public void callPhone(String phoneNum, String jsCallback) {
        try {
            CommonUtils.callPhone(phoneNum);
            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(0);
                add("成功");
            }});
        } catch (final Exception e) {
            LogUtils.ex(e);
            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(-1);
                add("失败:" + e.getMessage());
            }});
        }
    }

    //选择图片上传
    @JavascriptInterface
    public void uploadSelectedImg(String jsCallback) {
        try {
            uploadSelectedImgJsCallback = jsCallback;
            IntentAlbumUtils.getInstence().openChoiceGallery(BaseActivity.currAct, PHOTO_REQUEST_TAKEPHOTO, PHOTO_REQUEST_GALLERY);
            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(0);
                add("成功");
            }});
        } catch (final Exception e) {
            LogUtils.ex(e);
            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(-1);
                add("失败:" + e.getMessage());
            }});
        }
    }

    //支付
    @JavascriptInterface
    public void payment(int payType, String payStr, String jsCallback) {
        try {
            paymentJsCallback = jsCallback;
        } catch (Exception e) {
            LogUtils.ex(e);
        }

        Bundle bundle = new Bundle();

        bundle.putString("API", payStr);

        switch (payType) {
            case 0://微信
                Intent intent0 = new Intent();
                intent0.putExtras(bundle);
                PageUtils.startActivity(WxpayUser.class, intent0);
                break;
            case 1://支付宝
                Intent intent1 = new Intent();
                intent1.putExtras(bundle);
                PageUtils.startActivity(AlipayUser.class, intent1);
                break;
        }
    }

    //去播放视频
    @JavascriptInterface
    public void ToPlayVideo(final String name, final String url, final String jsCallback) {
        try {
            BaseActivity.currAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JCVideoPlayerStandard.startFullscreen(BaseActivity.currAct, JCVideoPlayerStandard.class, url, name);
                        executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                            add(0);
                            add("成功");
                        }});
                    } catch (final Exception e) {
                        LogUtils.ex(e);
                        executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                            add(-1);
                            add("失败:" + e.getMessage());
                        }});
                    }
                }
            });
        } catch (final Exception e) {
            LogUtils.ex(e);
        }
    }

    //分享
    @JavascriptInterface
    public void share(int shareType, String title, String text, String url, String coverUrl, String jsCallback) {
        try {
            shareJsCallback = jsCallback;
            String type = "";
            switch (shareType) {
                case 0:
                    type = "0";
                    break;
                case 1:
                    type = QQ.NAME;
                    break;
                case 2:
                    type = Wechat.NAME;
                    break;
                case 3:
                    type = SinaWeibo.NAME;
                    break;
                case 4:
                    type = WechatMoments.NAME;
                    break;
            }

            if (shareType == 0) {
                ShareSDKUtil.getInstence().showShareUI(title, text, url, coverUrl);
            } else {
                ShareSDKUtil.getInstence().showShare(type, title, text, url, coverUrl);
            }
            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(0);
                add("成功");
            }});
        } catch (final Exception e) {
            LogUtils.ex(e);
            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(-1);
                add("失败:" + e.getMessage());
            }});
        }
    }

    //三方登录
    @JavascriptInterface
    public void thirdPartyLogin(int loginType, final String jsCallback) {
        try {
            thirdPartyLoginJsCallback = jsCallback;

            switch (loginType) {
                case 1:
                    PopTipUtils.showToast("QQ登录功能暂无");
                    break;
                case 2:
                    PopTipUtils.showWaitDialog(-1);
                    ShareSDKUtil.getInstence().shartLoginWechat(Wechat.NAME, loginCallbackHandler);
                    break;
            }

        } catch (final Exception e) {
            LogUtils.ex(e);
            PopTipUtils.showToast("调起第三方登录异常！");
            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(-1);
                add("失败:" + e.getMessage());
            }});
        }
    }

    //定位
    @JavascriptInterface
    public void locate(int type, boolean openGps, String coortype, int scanspan,
                       boolean needAddr, String jsCallback) {
        try {
            locateJsCallback = jsCallback;
            switch (type) {
                case 0://关闭定位
                    LocationUtil.getInstance().closeLoc();
                    break;
                case 1://一次定位
                    LocationUtil.getInstance().locate(BaseActivity.currAct, true, openGps, coortype, scanspan, needAddr);
                    break;
                case 2://一直定位
                    LocationUtil.getInstance().locate(BaseActivity.currAct, false, openGps, coortype, scanspan, needAddr);
                    break;
            }
        } catch (Exception e) {
            LogUtils.ex(e);
        }
    }

    //切换推动开关
    @JavascriptInterface
    public void togglePush(int togglePush, String jsCallback) {
        try {

//            switch (togglePush){
//                case 0:
//                    MyApp.getApp().closePush();
//                    break;
//                case 1:
//                    MyApp.getApp().openPush();
//                    break;
//            }

        } catch (final Exception e) {
            LogUtils.ex(e);
            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(-1);
                add("失败:" + e.getMessage());
            }});
        }
    }

    //打开3方地图导航
    @JavascriptInterface
    public void openMapNavi(int type, String lng, String lat, String dest, String jsCallback) {
        try {
            switch (type) {
                case 0://百度地图
                    MapUtil.openBaiduMap(lat, lng, dest);
                    break;
                case 1://高德地图
                    MapUtil.openGaodeMap(lat, lng, dest);
                    break;
                case 2://腾讯地图
                    MapUtil.openTencentMap(lat, lng, dest);
                    break;
            }
        } catch (final Exception e) {
            LogUtils.ex(e);
            executeJSFunction(BaseApplication.getInstance().currShowWebView, jsCallback, new ArrayList<Object>() {{
                add(-1);
                add("失败:" + e.getMessage());
            }});
        }

    }

    //执行JS方法
    public static void executeJSFunction(final WebView webView, String functionName, List<Object> paramList) {
        try {
            if (TextUtils.isEmpty(functionName)) return;
            String paramStr = "";
            if (paramList != null && paramList.size() > 0) {
                for (int i = 0; i < paramList.size(); i++) {
                    Object object = paramList.get(i);
                    String paramJoint = "";
                    if (object instanceof Integer) {
                        paramJoint = String.valueOf(object);
                    } else if (object instanceof String) {
                        paramJoint = "'" + String.valueOf(object) + "'";
                    }

                    if (!TextUtils.isEmpty(paramStr)) paramJoint = "," + paramJoint;
                    paramStr += paramJoint;
                }
            }

            final String jsCode = "try{" + functionName + "(" + paramStr + ")}catch(e){" + "alert(e)}";

            webView.post(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= 19)
                        webView.evaluateJavascript("javascript: " + jsCode, null);
                    else
                        webView.loadUrl("javascript: " + jsCode);
                }
            });
        } catch (Exception e) {
            LogUtils.ex(e);
        }
    }
}
