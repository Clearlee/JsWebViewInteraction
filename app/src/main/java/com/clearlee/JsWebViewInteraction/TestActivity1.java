package com.clearlee.JsWebViewInteraction;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class TestActivity1 extends BaseActivity {

    Button bt_goweb;
    Button bt_go2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setTheme(R.style.TestTheme);
        setContentView(R.layout.activity_test1);

        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(option);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.transparent));

        bt_goweb = (Button)findViewById(R.id.bt_goweb);
        bt_goweb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.go("http://218.6.173.17:8085/xinxun_html/apptest1.html", false);
            }
        });

        bt_go2 = (Button)findViewById(R.id.bt_go2);
        bt_go2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageUtils.startActivity(TestActivity2.class, null);
            }
        });
    }
}
