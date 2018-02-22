package com.clearlee.JsWebViewInteraction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.clearlee.JsWebViewInteraction.DataBean.PayBackBean;
import com.clearlee.JsWebViewInteraction.DataBean.WxPayInfoBean;

//xinxun_customer
//xinxun_customer


/**
 * Created by zerdoor_pc on 2016/5/9.
 * 数据解析类
 */
public class FastJsonUtil {
    /**
     * 解析支付信息*
     */
    public static PayBackBean getPayBackBean(String json) {

        return JSON.parseObject(json, new TypeReference<PayBackBean>() {
        });
    }

    /**
     * 解析支付信息*
     */
    public static WxPayInfoBean getWxPayInfoBean(String json) {

        return JSON.parseObject(json, new TypeReference<WxPayInfoBean>() {
        });
    }
}
