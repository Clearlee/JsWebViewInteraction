package com.clearlee.JsWebViewInteraction;

import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by cy on 2018/1/30 0030.
 */

public class MyWebViewClient extends WebViewClient{
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        try {
            Intent intent = new Intent();
            intent.setClass(BaseApplication.getInstance(), WebActivity.class);
            intent.putExtra(WebActivity.key_url, url);
            BaseActivity.currAct.startActivity(intent);
        }catch (Exception e){
            LogUtils.ex(e);
        }
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        try {
            super.onPageFinished(view, url);
            view.getSettings().setLoadsImagesAutomatically(true);

            //如果此URL是预加载队列里的
            if(BaseApplication.getInstance().loadingUrlList.contains(url)){
                BaseApplication.getInstance().renderCachedWebView(view, BaseWebActivity.renderCacheWebViewViewGroup);

                //把加载完成的URL从队列中删除
                BaseApplication.getInstance().loadingUrlList.remove(url);
                //如果加载队列还有URL待加载，取出第一个进行加载
                if(BaseApplication.getInstance().loadingUrlList.size()>0){
                    String loadUrl = BaseApplication.getInstance().loadingUrlList.get(0);
                    WebView webView = BaseApplication.getInstance().getSpecialCachedWebView(loadUrl);
                    webView.loadUrl(loadUrl);
                }
            }
        }catch (Exception e){
            LogUtils.ex(e);
        }
    }
}
