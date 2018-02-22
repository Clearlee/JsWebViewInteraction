package com.clearlee.JsWebViewInteraction;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by cy on 2017/10/31 0031.
 */

public class BaseWebActivity extends BaseActivity {

    public static ViewGroup renderCacheWebViewViewGroup;//用于渲染预缓存webview的容器


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //统一向所有activity根布局里添加一个预缓存webview的容器
        ViewGroup rootView = (ViewGroup) ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
        LinearLayout linearLayout_webViewCache = new LinearLayout(this);
        linearLayout_webViewCache.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.addView(linearLayout_webViewCache, 0);
        linearLayout_webViewCache.setVisibility(View.INVISIBLE);
        renderCacheWebViewViewGroup = linearLayout_webViewCache;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
