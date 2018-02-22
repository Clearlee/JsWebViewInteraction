package com.clearlee.JsWebViewInteraction.payment.ali;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.clearlee.JsWebViewInteraction.BaseActivity;
import com.clearlee.JsWebViewInteraction.BaseApplication;
import com.clearlee.JsWebViewInteraction.DataBean.PayBackBean;
import com.clearlee.JsWebViewInteraction.FastJsonUtil;
import com.clearlee.JsWebViewInteraction.HttpStringUtil;
import com.clearlee.JsWebViewInteraction.LogUtils;
import com.clearlee.JsWebViewInteraction.PopTipUtils;
import com.clearlee.JsWebViewInteraction.WebActivity;

import java.util.ArrayList;

import static com.clearlee.JsWebViewInteraction.JSInterface.executeJSFunction;
import static com.clearlee.JsWebViewInteraction.JSInterface.paymentJsCallback;

/**
 * Created by zerdoor_pc on 2015/11/3.
 */
public class AlipayUser extends FragmentActivity {
    private static final int ADD_GOODS = 3;

    String callback = "";
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        PopTipUtils.showToast("支付成功");
                        returnBack(0, "支付成功");
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            PopTipUtils.showToast("支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            PopTipUtils.showToast("支付失败");
                            returnBack(-1,"支付失败");
                        }
                    }
                    break;
                }


                case SDK_CHECK_FLAG:
                    String sign = (String) msg.obj;
                    if (sign.length() != 0) {
                        pay(sign);
                    } else {
                        PopTipUtils.showToast("交易出错，请重新开始！");
                        returnBack(-1, "交易出错，请重新开始！");
                    }
                    break;
                case ADD_GOODS:
                    PayBackBean bean = FastJsonUtil.getPayBackBean((String) msg.obj);
                    if (bean.getCode() != null && bean.getCode().equals("SUCCESS")) {
                        mHandler.sendMessage(mHandler.obtainMessage(SDK_CHECK_FLAG, bean.getData()));
                        //getSDKVersion();
                    } else {
                        PopTipUtils.showToast(bean.getCode());
                        returnBack(-1, bean.getCode());
                    }


                    //  RootCodeTwo root = JsonParser.CheckCodeTwo((String) msg.obj);
                    // if ((root.type != null) ? (root.type.equals(MyString.PAYMENT)): (root.type != null)) {
                    //      sign = root.msg;
                    //      check();
                    //  } else {
                    //   if ("SUCCESS".equals(root.code)) {
                    //        AddNewWebView.Refresh(ShardPreferencesTool.getshare(AlipayUser.this, MyString.CALLBACK, MyString.NULL), MyString.SUCCESS);
                    //       ShardPreferencesTool.saveshare(AlipayUser.this, MyString.CALLBACK, MyString.NULL);
                    //  } else {
                    //      AddNewWebView.Refresh(ShardPreferencesTool.getshare(AlipayUser.this, MyString.CALLBACK, MyString.NULL), MyString.FILED);
                    //       ShardPreferencesTool.saveshare(AlipayUser.this, MyString.CALLBACK, MyString.NULL);
                    //  }
                    //    returnBack();
                    // }
                    break;
                case HttpStringUtil.ERROR:
                    PopTipUtils.showToast((String) msg.obj);
                    finish();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    //private String sign = "";

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

    String good_id = "", good_num = "", good_type = "", API = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bun = getIntent().getExtras();
        API = bun.getString("API");
//        good_id = bun.getString("type");
//        good_num = bun.getString("money");
//        good_type = bun.getString("orderid");
//        MyLog.I(MyApp.getLog()  + "API="+API+"\n= payid=" + payid + "&good_id=" + good_id + "&good_num=" + good_num + "&good_type=" + good_type);
        sendHttpGetGoods();

    }

    /**
     * 获取支付码*
     */
    private void sendHttpGetGoods() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = API;
                mHandler.sendMessage(msg);

//                RequestParams params = new RequestParams();
//                params.addBodyParameter("payid", payid);
//                params.addBodyParameter("good_id",good_id);
//                params.addBodyParameter("good_num",good_num);
//                params.addBodyParameter("good_type",good_type);
//                MyApp.getApp().HTTP.Post(HttpStringUtil.getUrl(API), params, mHandler,ADD_GOODS);
                // MyApp.getApp().HTTP.payment(mHandler, ADD_GOODS, payid, buy_menu_id, people_num, type);
            }
        }).start();
    }

    private void pay(final String payInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(AlipayUser.this);
                String result = alipay.pay(payInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);


//                官方api调用
//                PayTask alipay = new PayTask(AlipayUser.this);
//                String result = String.valueOf(alipay.payV2(payInfo,true));
//                 Message msg = new Message();
//                 msg.what = SDK_PAY_FLAG;
//                 msg.obj = result;
//                 mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        PopTipUtils.showToast(version);
    }

}
