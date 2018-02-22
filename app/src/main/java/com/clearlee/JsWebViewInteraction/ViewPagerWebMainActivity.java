package com.clearlee.JsWebViewInteraction;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.webkit.WebView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cy on 2018/1/26 0026.
 */
public class ViewPagerWebMainActivity extends BaseWebActivity {

    ViewPager viewPager;
    RadioGroup radioGroup;
    RadioButton rb0, rb1, rb2, rb3;

    WebFragment webFragment0;
    WebFragment webFragment1;
    WebFragment webFragment2;
    WebFragment webFragment3;

    FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_web);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        rb0 = (RadioButton) findViewById(R.id.rb0);
        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);

        webFragment0 = buildWebFragment(BaseApplication.getInstance().cachePersistentUrlList.get(0));
        webFragment1 = buildWebFragment(BaseApplication.getInstance().cachePersistentUrlList.get(1));
        webFragment2 = buildWebFragment(BaseApplication.getInstance().cachePersistentUrlList.get(2));
        webFragment3 = buildWebFragment(BaseApplication.getInstance().cachePersistentUrlList.get(3));

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(webFragment0);
        fragmentList.add(webFragment1);
        fragmentList.add(webFragment2);
        fragmentList.add(webFragment3);

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        rb0.setChecked(true);
                        break;
                    case 1:
                        rb1.setChecked(true);
                        break;
                    case 2:
                        rb2.setChecked(true);
                        break;
                    case 3:
                        rb3.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb0:
                        BaseApplication.getInstance().currShowWebUrl = webFragment0.url;
                        BaseApplication.getInstance().currShowWebView = webFragment0.webView;
                        viewPager.setCurrentItem(0, false);
                        break;
                    case R.id.rb1:
                        BaseApplication.getInstance().currShowWebUrl = webFragment1.url;
                        BaseApplication.getInstance().currShowWebView = webFragment1.webView;
                        viewPager.setCurrentItem(1, false);
                        break;
                    case R.id.rb2:
                        BaseApplication.getInstance().currShowWebUrl = webFragment2.url;
                        BaseApplication.getInstance().currShowWebView = webFragment2.webView;
                        viewPager.setCurrentItem(2, false);
                        break;
                    case R.id.rb3:
                        BaseApplication.getInstance().currShowWebUrl = webFragment3.url;
                        BaseApplication.getInstance().currShowWebView = webFragment3.webView;
                        viewPager.setCurrentItem(3, false);
                        break;
                }
            }
        });

        rb0.setChecked(true);
    }

    //构建webFragment
    public WebFragment buildWebFragment(String url) {

        WebFragment webFragment = new WebFragment();
        webFragment.url = url;

        //获得相应的webView
        WebView webView = BaseApplication.getInstance().getSpecialCachedWebView(url);
        if(BaseApplication.getInstance().loadingUrlList.size()>0 && !BaseApplication.getInstance().loadingUrlList.get(0).equals(url) && BaseApplication.getInstance().loadingUrlList.contains(url)){
            BaseApplication.getInstance().loadingUrlList.remove(url);
            webView.loadUrl(url);
        }
        if (webView == null){
            webView = BaseApplication.getInstance().buildWebView();
            webView.loadUrl(url);
        }

        webFragment.webView = webView;
        return webFragment;
    }
}
