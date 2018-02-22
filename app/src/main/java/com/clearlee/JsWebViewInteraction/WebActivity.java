package com.clearlee.JsWebViewInteraction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by cy on 2018/1/26 0026.
 */
public class WebActivity extends BaseWebActivity {

    public WebView webView;

    public static final String key_url = "key_url";

    FrameLayout framelayout;

    public List<String> currPageCacheUrlList = new ArrayList<>();//当前页缓存的URL集合

    public String url = "";//当前的URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        framelayout = (FrameLayout) findViewById(R.id.framelayout);

        try {

            //获得需要显示的URL
            url = getIntent().getStringExtra(key_url);
            if (TextUtils.isEmpty(url)) url = BaseApplication.getInstance().cachePersistentUrlList.get(0);

            //获得相应的webView
            webView = BaseApplication.getInstance().getSpecialCachedWebView(url);
            if(BaseApplication.getInstance().loadingUrlList.size()>0 && !BaseApplication.getInstance().loadingUrlList.get(0).equals(url) && BaseApplication.getInstance().loadingUrlList.contains(url)){
                BaseApplication.getInstance().loadingUrlList.remove(url);
                webView.loadUrl(url);
            }
            if (webView == null){
                webView = BaseApplication.getInstance().buildWebView();
                webView.loadUrl(url);
            }
            //把webView加进布局
            if (webView.getParent() != null) ((ViewGroup) webView.getParent()).removeView(webView);
            framelayout.addView(webView);

        } catch (Exception e) {
            LogUtils.ex(e);
        }
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayerStandard.backPress()) {
            return;
        }

        if(webView.canGoBack())
            webView.goBack();
        else{
            super.onBackPressed();
            if(currPageCacheUrlList!=null && currPageCacheUrlList.size()>0) BaseApplication.getInstance().clearCachedTempWebView(currPageCacheUrlList);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseApplication.getInstance().currShowWebUrl = url;
        BaseApplication.getInstance().currShowWebView = webView;

//        new android.os.Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                List<String> list = new ArrayList<>();
//                for(int i=3;i<= 200;i++){
//                    String url = "http://218.6.173.17:8085/xinxun_html/counselorDetail.html?id="+i;
//                    list.add(url);
//                }
//                BaseApplication.getInstance().cacheWebView(list, currPageCacheUrlList, false);
//            }
//        }, 2000);

        //测试数据
//        List<String> list = new ArrayList<>();
//        list.add("http://app.cleair31.com/keyier/article.html?id=1000");
//        BaseApplication.getInstance().cacheWebView(list, currPageCacheUrlList, false);
    }

    public static void go(String url, boolean newTask) {
        Intent intent = new Intent();
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(WebActivity.key_url, url);
        PageUtils.startActivity(WebActivity.class, intent);
    }


}
