package com.clearlee.JsWebViewInteraction;

import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by cy on 2018/2/2 0002.
 */

public class MyWebChromeClient extends WebChromeClient{
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        PopTipUtils.showToast(message);
        return false;
    }
}
