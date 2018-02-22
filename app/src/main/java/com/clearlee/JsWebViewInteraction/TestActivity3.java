package com.clearlee.JsWebViewInteraction;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class TestActivity3 extends BaseActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        webView = (WebView)findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.addJavascriptInterface(new JSInterface(), "TIO");

        webView.loadUrl("file:///android_asset/testback.html");
    }
}
