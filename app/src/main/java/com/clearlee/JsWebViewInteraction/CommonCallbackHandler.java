package com.clearlee.JsWebViewInteraction;

import android.os.Handler;
import android.os.Message;

/**
 * Created by cy on 2018/2/6 0006.
 */

public class CommonCallbackHandler {
    public static Handler loginCallbackHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://第三方登录成功
                    // TODO: 2018/2/6 0006  
                    break;
                case 2://登录失败
                    // TODO: 2018/2/6 0006  
                    break;
                case 3://取消登录
                    // TODO: 2018/2/6 0006
                    break;
                case HttpStringUtil.ERROR:
                    // TODO: 2018/2/6 0006
                    break;
            }

        }
    };
}
