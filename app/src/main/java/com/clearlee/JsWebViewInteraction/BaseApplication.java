package com.clearlee.JsWebViewInteraction;

import android.app.Application;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by cy on 2018/1/26 0026.
 */

public class BaseApplication extends Application {

    private static BaseApplication baseApplication;
    public static BaseApplication getInstance() {
        return baseApplication;
    }

    public HashMap<String, WebView> url_tempWebView = new HashMap<>();//临时缓存的webView

    public HashMap<String, WebView> url_persistentWebView = new HashMap<>();//持久缓存的webView

    public List<String> cachePersistentUrlList = new ArrayList<>();//持久性缓存的URL集合

    public String currShowWebUrl = "";//当前显示的webView的URL
    public WebView currShowWebView;//当前显示的WEBVIEW

    public List<String> loadingUrlList = new ArrayList<>();//加载中的URL集合



    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;

        //进app需要缓存的持久性URL
        cachePersistentUrlList = new ArrayList<>();
        cachePersistentUrlList.add("file:///android_asset/test.html");
        cachePersistentUrlList.add("http://app.cleair31.com/keyier/find.html");
        cachePersistentUrlList.add("http://218.6.173.17:8085/xinxun_html/apptest1.html");
        cachePersistentUrlList.add("http://218.6.173.17:8085/xinxun_html/counselorDetail.html?id=1");
        cachePersistentUrlList.add("http://app.cleair31.com/keyier/article.html?id=1000");
        cachePersistentUrlList.add("http://218.6.173.17:8085/xinxun_html/counselorDetail.html?id=2");
        cachePersistentUrlList.add("https://m.meizu.com/video/?click=mmz_index_tg_1");

        cacheWebView(cachePersistentUrlList, new ArrayList<String>(), true);
    }

    /**
     * 缓存指定URL的webview
     *
     * @param urlList              准备被缓存的URL
     * @param currNeedCacheUrlList 当前真正需要被缓存的URL
     * @param persistentCache      是否持久性缓存
     */
    public void cacheWebView(List<String> urlList, List<String> currNeedCacheUrlList, boolean persistentCache) {
        try {
            HashMap<String, WebView> url_webView = persistentCache ? url_persistentWebView : url_tempWebView;

            synchronized (url_webView) {
                //获得当前真正需要被缓存的URL
                currNeedCacheUrlList.clear();
                for (int i = 0; i < urlList.size(); i++) {
                    String url = urlList.get(i);
                    if (!BaseApplication.getInstance().url_tempWebView.containsKey(url) && !BaseApplication.getInstance().url_persistentWebView.containsKey(url)) {
                        currNeedCacheUrlList.add(url);
                    }
                }
                //开始缓存
                for (int i = 0; i < currNeedCacheUrlList.size(); i++) {
                    final String url = currNeedCacheUrlList.get(i);
                    WebView webView = buildWebView();

                    //把URL加入加载队列
                    loadingUrlList.add(url);
                    //如果加载队列只有这一个，就直接加载
                    if(loadingUrlList.size() == 1){
                        webView.loadUrl(url);
                    }

                    url_webView.put(url, webView);
                    LogUtils.v("==新缓存了webView的URL：" + url);
                }
            }
        } catch (Exception e) {
            LogUtils.ex(e);
        }
    }

    //构建WEBVIEW
    public WebView buildWebView(){
        try {
            final WebView webView = new WebView(BaseApplication.getInstance());
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setLoadsImagesAutomatically(true);
            webView.setWebViewClient(new MyWebViewClient());
            webView.setWebChromeClient(new MyWebChromeClient());
            webView.addJavascriptInterface(new JSInterface(), "TIO");
            return webView;
        }catch (Exception e){
            LogUtils.ex(e);
            return null;
        }
    }

    //返回指定的缓存的webView
    public WebView getSpecialCachedWebView(String url) {
        WebView webView = url_tempWebView.get(url);
        if (webView == null) {
            webView = url_persistentWebView.get(url);
        }
        return webView;
    }

    //清除缓存的临时webview
    public void clearCachedTempWebView(List<String> urlList) {
        try {
            synchronized (url_tempWebView) {
                try {
                    for (int i = 0; i < urlList.size(); i++) {
                        String url = urlList.get(i);
                        if (url_tempWebView.containsKey(url) && !url.equals(currShowWebUrl)) {
                            if(loadingUrlList.contains(url)) loadingUrlList.remove(url);
                            WebView webView = url_tempWebView.get(url);
                            url_tempWebView.remove(url);
                            if (webView.getParent() != null) {
                                ((ViewGroup) webView.getParent()).removeView(webView);
                            }
                            webView.removeAllViews();
                            webView.destroy();
                            webView = null;
                        }
                    }
                    System.gc();
                } catch (Exception e) {
                    LogUtils.ex(e);
                }
            }
        } catch (Exception e) {
            LogUtils.v(e);
        }
    }

    //渲染缓存的webView
    public void renderCachedWebView(WebView webView, ViewGroup renderCacheWebViewViewGroup) {
        try {
            if (webView.getParent() == null && renderCacheWebViewViewGroup!=null) renderCacheWebViewViewGroup.addView(webView);
        } catch (Exception e) {
            LogUtils.v(e);
        }
    }
}
