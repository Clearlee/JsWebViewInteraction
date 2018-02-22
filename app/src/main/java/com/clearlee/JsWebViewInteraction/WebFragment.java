package com.clearlee.JsWebViewInteraction;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * Created by cy on 2018/1/30 0030.
 */

public class WebFragment extends Fragment {

    public WebView webView;

    FrameLayout framelayout;

    View view;

    public String url = "";//当前的URL

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            if(view == null){
                view = inflater.inflate(R.layout.fragment_web, container, false);
                framelayout = (FrameLayout) view.findViewById(R.id.framelayout);

                if (webView.getParent() != null) {
                    ((ViewGroup) webView.getParent()).removeAllViews();
                }
                framelayout.addView(webView);
            }
        } catch (Exception e) {
            LogUtils.ex(e);
        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {
            if(isVisibleToUser){
                if(framelayout!=null && webView!=null && !webView.getParent().equals(framelayout)){
                    ((ViewGroup) webView.getParent()).removeView(webView);
                    framelayout.addView(webView);
                }
            }
        }catch (Exception e){
            LogUtils.ex(e);
        }
    }
}
