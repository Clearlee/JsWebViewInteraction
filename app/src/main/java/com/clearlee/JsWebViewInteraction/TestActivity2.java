package com.clearlee.JsWebViewInteraction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestActivity2 extends BaseActivity {
    Button bt_go3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        bt_go3 = (Button)findViewById(R.id.bt_go3);
        bt_go3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageUtils.startActivity(TestActivity3.class, null);
            }
        });
    }
}
