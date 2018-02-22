package com.clearlee.JsWebViewInteraction.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.clearlee.JsWebViewInteraction.BaseActivity;
import com.clearlee.JsWebViewInteraction.BaseApplication;
import com.clearlee.JsWebViewInteraction.LogUtils;
import com.clearlee.JsWebViewInteraction.WebActivity;
import com.clearlee.JsWebViewInteraction.payment.wx.Constant;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;

import static com.clearlee.JsWebViewInteraction.JSInterface.executeJSFunction;
import static com.clearlee.JsWebViewInteraction.JSInterface.paymentJsCallback;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);
		Log.i("支付", "微信回调2");
    	api = WXAPIFactory.createWXAPI(this, null);
		api.registerApp(Constant.WXAPPID);
        api.handleIntent(getIntent(), this);

    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i("支付", "微信回调21");
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		Log.i("支付", "微信回调22");
	}

	@Override
	public void onResp(BaseResp resp) {

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (resp.errCode==0){
				Log.i("支付","微信回调23  成功");
//				AddNewWebView.Refresh(ShardPreferencesTool.getshare(this, MyString.CALLBACK, MyString.NULL), MyString.SUCCESS);
//				ShardPreferencesTool.saveshare(this, MyString.CALLBACK, MyString.NULL);
				returnBack(0, "微信回调23  成功");
//				ToastShow.showShort(this,"微信支付成功"+ JSON.toJSONString(resp));
			}else{
				Log.i("支付","微信回调23  失败");
//				AddNewWebView.Refresh(ShardPreferencesTool.getshare(this, MyString.CALLBACK, MyString.NULL), MyString.FILED);
//				ShardPreferencesTool.saveshare(this, MyString.CALLBACK, MyString.NULL);
				returnBack(-1, "微信回调23  失败");
//				ToastShow.showShort(this,"微信支付失败"+ JSON.toJSONString(resp));
			}
			this.finish();
		}
	}

	private void returnBack(int code, final String msg) {//1为成功，0为失败
		try {
			if (BaseActivity.currAct instanceof WebActivity){
				executeJSFunction(BaseApplication.getInstance().currShowWebView, paymentJsCallback, new ArrayList<Object>(){{add(0); add(msg);}});
			}
		} catch (Exception e) {
			LogUtils.ex(e);
		}
		this.finish();
	}

}