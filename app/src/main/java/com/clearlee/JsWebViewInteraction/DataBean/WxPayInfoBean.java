package com.clearlee.JsWebViewInteraction.DataBean;


import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by zerdoor_pc on 2015/11/10.
 */
public class WxPayInfoBean {
    public String sign;
    public String timestamp;
    public String noncestr;
    public String partnerid;
    public String prepayid;
    @JSONField(name="package")
    public String package2;
    public String appid;
}
