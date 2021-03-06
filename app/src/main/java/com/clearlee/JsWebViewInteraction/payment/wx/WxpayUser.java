package com.clearlee.JsWebViewInteraction.payment.wx;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.clearlee.JsWebViewInteraction.BaseActivity;
import com.clearlee.JsWebViewInteraction.BaseApplication;
import com.clearlee.JsWebViewInteraction.DataBean.PayBackBean;
import com.clearlee.JsWebViewInteraction.DataBean.WxPayInfoBean;
import com.clearlee.JsWebViewInteraction.FastJsonUtil;
import com.clearlee.JsWebViewInteraction.HttpStringUtil;
import com.clearlee.JsWebViewInteraction.LogUtils;
import com.clearlee.JsWebViewInteraction.PopTipUtils;
import com.clearlee.JsWebViewInteraction.R;
import com.clearlee.JsWebViewInteraction.WebActivity;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.clearlee.JsWebViewInteraction.JSInterface.executeJSFunction;
import static com.clearlee.JsWebViewInteraction.JSInterface.paymentJsCallback;


/**
 * Created by zerdoor_pc on 2015/11/10.
 * 微信支付界面
 */

public class WxpayUser extends Activity {

    private static final String TAG = "MicroMsg.SDKSample.PayActivity";

    private IWXAPI api;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //Log.i("支付==", "微信接口回调：" + msg.obj);
                   PayBackBean root = FastJsonUtil.getPayBackBean((String) msg.obj);
                   if (root.code.equals("SUCCESS")){
                       handler.sendEmptyMessage(1);
                   }else{
                       //返回失败
                       PopTipUtils.showToast(root.getMsg());
                   }

                    break;
                case 1:
                    //正常支付
                    WxPayInfoBean root2 = FastJsonUtil.getWxPayInfoBean(API);
                    //Log.i("支付","正常支付");
                    if (api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT){
                        //Log.i("支付", "微信支付调用：" + root2.msg);
                        sendPayReq(root2.sign, root2.timestamp, root2.noncestr, root2.partnerid, root2.prepayid, root2.package2, root2.appid);
                    }else{
                        //Log.i("支付","失败1");
                        Toast.makeText(WxpayUser.this,"请检查是否安装微信或是否微信版本过低！", Toast.LENGTH_SHORT).show();

                        PopTipUtils.showToast("支付失败");
                        returnBack(-1, "支付失败");
                    }
                    break;
                case HttpStringUtil.ERROR:
                    PopTipUtils.showToast((String) msg.obj);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
    String API="", good_id = "", good_num = "", good_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bun = getIntent().getExtras();
        API = bun.getString("API");
//      good_id = bun.getString("good_id");
//      good_num = bun.getString("good_num");
//      good_type = bun.getString("good_type");


//      MyLog.I(MyApp.getLog()  + "API="+API+"\n= payid=" + payid + "&good_id=" + good_id + "&good_num=" + good_num + "&good_type=" + good_type);
        api = WXAPIFactory.createWXAPI(this, Constant.WXAPPID);
        sendHttp();
    }

    private void sendHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
//                RequestParams params = new RequestParams();
//                params.addBodyParameter("payid", payid);
//                params.addBodyParameter("good_id",good_id);
//                params.addBodyParameter("good_num",good_num);
//                params.addBodyParameter("good_type",good_type);
//                MyApp.getApp().HTTP.Post(HttpStringUtil.getUrl(API), params, handler, 0);
            }
        }).start();
    }

    private void returnBack(int code, final String msg) {
        try {
            if(BaseActivity.currAct instanceof WebActivity){
                executeJSFunction(BaseApplication.getInstance().currShowWebView, paymentJsCallback, new ArrayList<Object>(){{add(0); add(msg);}});
            }
        }catch (Exception e){
            LogUtils.ex(e);
        }
        this.finish();
    }


    /**
     * 微信公众平台商户模块和商户约定的密钥
     * <p/>
     * 注意：不能hardcode在客户端，建议genPackage这个过程由服务器端完成
     */
    private static final String PARTNER_KEY = "8934e7d15453e97507ef794cf7b0519d";

    private String genPackage(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        int num = params.size();
        for (int i = 0; i < num; i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(PARTNER_KEY); // 注意：不能hardcode在客户端，建议genPackage这个过程都由服务器端完成

        // 进行md5摘要前，params内容为原始内容，未经过url encode处理
        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();

        return URLEncodedUtils.format(params, "utf-8") + "&sign=" + packageSign;
    }

    /**
     * 微信开放平台和商户约定的密钥
     * <p/>
     * 注意：不能hardcode在客户端，建议genSign这个过程由服务器端完成
     */
    private static final String APP_SECRET = "db426a9829e4b49a0dcac7b4162da6b6"; // wxd930ea5d5a258f4f 对应的密钥

    /**
     * 微信开放平台和商户约定的支付密钥
     * <p/>
     * 注意：不能hardcode在客户端，建议genSign这个过程由服务器端完成
     */
    private static final String APP_KEY = "L8LrMqqeGRxST5reouB0K66CaYAWpqhAVsq7ggKkxHCOastWksvuX1uvmvQclxaHoYd3ElNBrNO2DHnnzgfVG9Qs473M3DTOZug5er46FhuGofumV8H2FVR9qkjSlC5K"; // wxd930ea5d5a258f4f 对应的支付密钥

    private class GetAccessTokenTask extends AsyncTask<Void, Void, GetAccessTokenResult> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(WxpayUser.this, getString(R.string.app_tip), getString(R.string.getting_access_token));
        }

        @Override
        protected void onPostExecute(GetAccessTokenResult result) {
            if (dialog != null) {
                dialog.dismiss();
            }

            if (result.localRetCode == LocalRetCode.ERR_OK) {
                Toast.makeText(WxpayUser.this, R.string.get_access_token_succ, Toast.LENGTH_LONG).show();


                GetPrepayIdTask getPrepayId = new GetPrepayIdTask(result.accessToken);
                getPrepayId.execute();
            } else {
                Toast.makeText(WxpayUser.this, getString(R.string.get_access_token_fail, result.localRetCode.name()), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected GetAccessTokenResult doInBackground(Void... params) {
            GetAccessTokenResult result = new GetAccessTokenResult();

            String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                    Constant.WXAPPID,Constant.WXAPPSERC);
            byte[] buf = Util.httpGet(url);
            if (buf == null || buf.length == 0) {
                result.localRetCode = LocalRetCode.ERR_HTTP;
                return result;
            }
            String content = new String(buf);
            result.parseFrom(content);
            return result;
        }
    }

    private class GetPrepayIdTask extends AsyncTask<Void, Void, GetPrepayIdResult> {

        private ProgressDialog dialog;
        private String accessToken;

        public GetPrepayIdTask(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(WxpayUser.this, getString(R.string.app_tip), getString(R.string.getting_prepayid));
        }

        @Override
        protected void onPostExecute(GetPrepayIdResult result) {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (result.localRetCode == LocalRetCode.ERR_OK) {
                Toast.makeText(WxpayUser.this, R.string.get_prepayid_succ, Toast.LENGTH_LONG).show();
//                sendPayReq(RESULT);
            } else {
                Toast.makeText(WxpayUser.this, getString(R.string.get_prepayid_fail, result.localRetCode.name()), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected GetPrepayIdResult doInBackground(Void... params) {

            String url = String.format("https://api.weixin.qq.com/pay/genprepay?access_token=%s", accessToken);
            String entity = genProductArgs();
            GetPrepayIdResult result = new GetPrepayIdResult();

            byte[] buf = Util.httpPost(url, entity);
            if (buf == null || buf.length == 0) {
                result.localRetCode = LocalRetCode.ERR_HTTP;
                return result;
            }
            String content = new String(buf);
            result.parseFrom(content);
            return result;
        }
    }

    private static enum LocalRetCode {
        ERR_OK, ERR_HTTP, ERR_JSON, ERR_OTHER
    }

    private static class GetAccessTokenResult {
        private static final String TAG = "MicroMsg.SDKSample.PayActivity.GetAccessTokenResult";
        public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
        public String accessToken;
        public int expiresIn;
        public int errCode;
        public String errMsg;

        public void parseFrom(String content) {
            if (content == null || content.length() <= 0) {
                localRetCode = LocalRetCode.ERR_JSON;
                return;
            }

            try {
                JSONObject json = new JSONObject(content);
                if (json.has("access_token")) { // success case
                    accessToken = json.getString("access_token");
                    expiresIn = json.getInt("expires_in");
                    localRetCode = LocalRetCode.ERR_OK;
                } else {
                    errCode = json.getInt("errcode");
                    errMsg = json.getString("errmsg");
                    localRetCode = LocalRetCode.ERR_JSON;
                }

            } catch (Exception e) {
                localRetCode = LocalRetCode.ERR_JSON;
            }
        }
    }

    private static class GetPrepayIdResult {

        private static final String TAG = "MicroMsg.SDKSample.PayActivity.GetPrepayIdResult";

        public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;
        public String prepayId;
        public int errCode;
        public String errMsg;

        public void parseFrom(String content) {

            if (content == null || content.length() <= 0) {
                localRetCode = LocalRetCode.ERR_JSON;
                return;
            }

            try {
                JSONObject json = new JSONObject(content);
                if (json.has("prepayid")) { // success case
                    prepayId = json.getString("prepayid");
                    localRetCode = LocalRetCode.ERR_OK;
                } else {
                    localRetCode = LocalRetCode.ERR_JSON;
                }

                errCode = json.getInt("errcode");
                errMsg = json.getString("errmsg");

            } catch (Exception e) {
                localRetCode = LocalRetCode.ERR_JSON;
            }
        }
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 建议 traceid 字段包含用户信息及订单信息，方便后续对订单状态的查询和跟踪
     */
    private String getTraceId() {
        return "crestxu_" + genTimeStamp();
    }

    /**
     * 注意：商户系统内部的订单号,32个字符内、可包含字母,确保在商户系统唯一
     */
    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long timeStamp;
    private String nonceStr, packageValue;

    private String genSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int num = params.size() - 1;
        for (; i < num; i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append(params.get(i).getName());
        sb.append('=');
        sb.append(params.get(i).getValue());

        String sha1 = Util.sha1(sb.toString());

        return sha1;
    }

    private String genProductArgs() {
        JSONObject json = new JSONObject();

        try {
            json.put("appid", Constant.WXAPPID);
            String traceId = getTraceId();  // traceId 由开发者自定义，可用于订单的查询与跟踪，建议根据支付用户信息生成此id
            json.put("traceid", traceId);
            nonceStr = genNonceStr();
            json.put("noncestr", nonceStr);
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("bank_type", "WX"));
            packageParams.add(new BasicNameValuePair("body", "千足金箍棒"));
            packageParams.add(new BasicNameValuePair("fee_type", "1"));
            packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));
            packageParams.add(new BasicNameValuePair("notify_url", "http://weixin.qq.com"));
            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
            packageParams.add(new BasicNameValuePair("partner", "1900000109"));
            packageParams.add(new BasicNameValuePair("spbill_create_ip", "196.168.1.1"));
            packageParams.add(new BasicNameValuePair("total_fee", "1"));
            packageValue = genPackage(packageParams);

            json.put("package", packageValue);
            timeStamp = genTimeStamp();
            json.put("timestamp", timeStamp);

            List<NameValuePair> signParams = new LinkedList<NameValuePair>();
            signParams.add(new BasicNameValuePair("appid", Constant.WXAPPID));
            signParams.add(new BasicNameValuePair("appkey", APP_KEY));
            signParams.add(new BasicNameValuePair("noncestr", nonceStr));
            signParams.add(new BasicNameValuePair("package", packageValue));
            signParams.add(new BasicNameValuePair("timestamp", String.valueOf(timeStamp)));
            signParams.add(new BasicNameValuePair("traceid", traceId));
            json.put("app_signature", genSign(signParams));

            json.put("sign_method", "sha1");
        } catch (Exception e) {

            return null;
        }

        return json.toString();
    }

    private void sendPayReq(String sign, String timestamp, String noncestr, String partnerid, String prepayid, String package2, String appid) {
        Log.i("支付", "req=sign=" + sign + "  timestamp=" + timestamp + "  noncestr=" + noncestr + "  partnerid=" + partnerid + " prepayid=" + prepayid + "  package2=" + package2 + " appid=" + appid);
        PayReq req = new PayReq();
        req.appId = appid;
        req.partnerId = partnerid;
        req.prepayId = prepayid;
        req.nonceStr = noncestr;
        req.timeStamp = timestamp;
        req.packageValue = package2;
        req.sign = sign;
        final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
        msgApi.registerApp(Constant.WXAPPID);
        //Log.i("支付","req="+req.toString());
        api.sendReq(req);
        this.onBackPressed();
//                  "code":"SUCCESS",
//                   "type":"payment",
//                   "msg":{
//                   "sign":"533445B7FF1B133A708647EC3EB46C00",
//                    "timestamp":"1447156725",
//                    "noncestr":"YV79ti6tB5ivwaXi",
//                    "partnerid":"1276850101",
//                    "prepayid":"wx201511101954095c8e14d67b0937287694",
//                    "package":"Sign\u003dWXPay",
//                    "appid":"wxf290810f0b3b638c"

        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//        IWXMsg.registerApp;

    }

}
