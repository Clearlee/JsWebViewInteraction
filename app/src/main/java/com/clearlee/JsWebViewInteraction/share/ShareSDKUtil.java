package com.clearlee.JsWebViewInteraction.share;

import android.os.Handler;

import com.clearlee.JsWebViewInteraction.BaseActivity;
import com.clearlee.JsWebViewInteraction.BaseApplication;
import com.clearlee.JsWebViewInteraction.LogUtils;
import com.clearlee.JsWebViewInteraction.PopTipUtils;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

import static com.clearlee.JsWebViewInteraction.JSInterface.executeJSFunction;
import static com.clearlee.JsWebViewInteraction.JSInterface.shareJsCallback;
import static com.clearlee.JsWebViewInteraction.JSInterface.thirdPartyLoginJsCallback;


/**
 * Created by zerdoor_pc .
 * author:dc
 * 2016/12/20.
 */

public class ShareSDKUtil {

    static ShareSDKUtil instence;

    public static ShareSDKUtil getInstence() {
        if (instence == null) {
            instence = new ShareSDKUtil();
        }
        return instence;
    }

    public void showShareUI(String title, String text, String outUrl, String... image) {
        try {
            ShareSDK.initSDK(BaseActivity.currAct);
            OnekeyShare oks = new OnekeyShare();
            //关闭sso授权
            oks.disableSSOWhenAuthorize();

            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
            oks.setTitle(title);
            // titleUrl是标题的网络链接，QQ和QQ空间等使用
            oks.setTitleUrl(outUrl);
            // text是分享文本，所有平台都需要这个字段
            oks.setText(text);
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
            if (image != null && image.length > 0) {
                oks.setImageUrl(image[0]);
            }
            //oks.setImagePath(image);//确保SDcard下面存在此张图片
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(outUrl);
            // comment是我对这条分享的评论，仅在人人网和QQ空间使用
            oks.setComment("评论文本");
            // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");
            // 启动分享GUI
            oks.setCallback(shareListener);
            oks.show(BaseActivity.currAct);
        }catch (Exception e){
            LogUtils.ex(e);
        }
    }

    private String getString(int id) {
        return BaseApplication.getInstance().getResources().getString(id);
    }


    public void showShare(String platform, String title, String text, String outUrl, String... image) {
        try {
            ShareSDK.initSDK(BaseActivity.currAct);
            OnekeyShare oks = new OnekeyShare();
            //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
            if (platform != null) {
                Platform plat = ShareSDK.getPlatform(platform);
                oks.setPlatform(plat.getName());
            }
            //关闭sso授权
            oks.disableSSOWhenAuthorize();
            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
            oks.setTitle(title);
            // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
            oks.setTitleUrl(outUrl);
            // text是分享文本，所有平台都需要这个字段
            oks.setText(text);
            //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
            if (image != null && image.length > 0) {
                oks.setImageUrl(image[0]);
            }
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
            //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(outUrl);
            // comment是我对这条分享的评论，仅在人人网和QQ空间使用
            // oks.setComment("我是测试评论文本");
            // site是分享此内容的网站名称，仅在QQ空间使用
            // oks.setSite("ShareSDK");
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            // oks.setSiteUrl("http://sharesdk.cn");
            oks.setCallback(shareListener);
            //启动分享
            oks.show(BaseActivity.currAct);
        }catch (Exception e){
            LogUtils.ex(e);
        }
    }

    public void showShare(String platform, String title, String text, String outUrl, PlatformActionListener listener, String... image) {
        try {
            LogUtils.v("showShare:" + "\nplatform=" + platform + "\ntitle=" + title + "\ntext=" + text + "\noutUrl=" + outUrl + "\nimage=" + (image != null ? image[0] : ""));
            ShareSDK.initSDK(BaseActivity.currAct);
            OnekeyShare oks = new OnekeyShare();
            //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
            if (platform != null) {
                Platform plat = ShareSDK.getPlatform(platform);
                oks.setPlatform(plat.getName());
            }
//        //关闭sso授权
            oks.disableSSOWhenAuthorize();
            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
            oks.setTitle(title);
            // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
            oks.setTitleUrl(outUrl);
            // text是分享文本，所有平台都需要这个字段
            oks.setText(text);
            //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
            if (image != null && image.length > 0) {
                oks.setImageUrl(image[0]);
            } else {
//            oks.setImagePath(UriUtil.getUriPath(com.techinone.ximitu.utils.MString.getInstence().LOCALIMAGEHEAD + R.mipmap.icon));
            }
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
            //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(outUrl);
            // comment是我对这条分享的评论，仅在人人网和QQ空间使用
            // oks.setComment("我是测试评论文本");
            // site是分享此内容的网站名称，仅在QQ空间使用
            // oks.setSite("ShareSDK");
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            // oks.setSiteUrl("http://sharesdk.cn");
            oks.setCallback(listener);
            //启动分享
            oks.show(BaseActivity.currAct);

        }catch (Exception e){
            LogUtils.ex(e);
        }
    }



    public void showShareUI(String title, String text, String outUrl, PlatformActionListener listener, String... image) {
        try {
            ShareSDK.initSDK(BaseActivity.currAct);
            OnekeyShare oks = new OnekeyShare();
            //关闭sso授权
            oks.disableSSOWhenAuthorize();

            // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
            oks.setTitle(title);
            // titleUrl是标题的网络链接，QQ和QQ空间等使用
            oks.setTitleUrl(outUrl);
            // text是分享文本，所有平台都需要这个字段
            oks.setText("哈哈");
            // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
            if (image != null && image.length > 0) {
                oks.setImageUrl(image[0]);
            } else {
//            oks.setImageUrl(UriUtil.getUriPath(com.techinone.ximitu.utils.MString.getInstence().LOCALIMAGEHEAD + R.mipmap.icon));
            }
            //oks.setImagePath(image);//确保SDcard下面存在此张图片
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(outUrl);
            // comment是我对这条分享的评论，仅在人人网和QQ空间使用
            oks.setComment("评论文本");
            // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");
            // 启动分享GUI
            oks.setCallback(listener);
            oks.show(BaseActivity.currAct);
        }catch (Exception e){
            LogUtils.ex(e);
        }
    }

    /**
     * 单独分享到微博
     **/
    private void shareQQ(String title, String text, String outUrl, String image) {
        ShareParams sp = new ShareParams();
        sp.setTitle(title);
        sp.setTitleUrl(outUrl); // 标题的超链接
        sp.setText(text);
        sp.setImageUrl(image);
//        sp.setSite("发布分享的网站名称");//来源
//        sp.setSiteUrl("发布分享网站的地址");

        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        // 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
        qq.setPlatformActionListener(shareListener);
        // 执行图文分享
        qq.share(sp);
    }

    /**
     * 单独分享到微博
     **/
    private void shareWEIBO(String text, String image) {
        ShareParams sp = new ShareParams();
        sp.setText(text);
        sp.setImagePath(image);

        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        weibo.setPlatformActionListener(shareListener); // 设置分享事件回调
        // 执行图文分享
        weibo.share(sp);
    }


    public void shartLoginWechat(String type, final Handler wechatLoginHandler) {
        LogUtils.v("点击登录");
        ShareSDK.initSDK(BaseActivity.currAct);
        Platform info = ShareSDK.getPlatform(type);
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        info.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, final Throwable arg2) {

                String msg = "登录失败";
                LogUtils.v(msg + arg2.toString());
                PopTipUtils.showToast(msg);

                if (wechatLoginHandler != null) {
                    wechatLoginHandler.sendMessage(wechatLoginHandler.obtainMessage(-1, arg2.toString()));
                }

                executeJSFunction(BaseApplication.getInstance().currShowWebView, thirdPartyLoginJsCallback, new ArrayList<Object>() {{
                    add(-1);
                    add(arg2.getMessage());
                }});

            }

            @Override
            public void onComplete(final Platform arg0, int arg1, HashMap<String, Object> arg2) {

                String msg = "登录成功";
                //输出所有授权信息
                LogUtils.v(msg + arg0.getDb().exportData());
                PopTipUtils.showToast(msg);

                if (wechatLoginHandler != null) {
                    wechatLoginHandler.sendMessage(wechatLoginHandler.obtainMessage(0, arg0.getDb().exportData()) );
                }

                executeJSFunction(BaseApplication.getInstance().currShowWebView, thirdPartyLoginJsCallback, new ArrayList<Object>() {{
                    add(0);
                    add(arg0.getDb().exportData());
                }});

            }

            @Override
            public void onCancel(final Platform arg0, int arg1) {
                String msg = "退出登录";
                LogUtils.v(msg + arg0.getDb().exportData());

                if (wechatLoginHandler != null) {
                    wechatLoginHandler.sendMessage(wechatLoginHandler.obtainMessage(-2, arg0.getDb().exportData()) );
                }

                executeJSFunction(BaseApplication.getInstance().currShowWebView, thirdPartyLoginJsCallback, new ArrayList<Object>() {{
                    add(-2);
                    add(arg0.getDb().exportData());
                }});

            }
        });

        //关闭sso授权
//        Platform platform = ShareSDK.getPlatform(MyApp.getApp().activity, SinaWeibo.NAME);
//        platform.SSOSetting(true);

        //authorize与showUser单独调用一个即可
        info.authorize();//单独授权,OnComplete返回的hashmap是空的
//        info.showUser(null);//授权并获取用户信息

        //移除授权
        //weibo.removeAccount(true);
    }

    PlatformActionListener shareListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            final String msg = "分享成功！";
            PopTipUtils.showToast(msg);
            executeJSFunction(BaseApplication.getInstance().currShowWebView, shareJsCallback, new ArrayList<Object>(){{add(0); add(msg);}});
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            final String msg = "分享失败！";
            PopTipUtils.showToast(msg);
            executeJSFunction(BaseApplication.getInstance().currShowWebView, shareJsCallback, new ArrayList<Object>(){{add(-1); add(msg);}});
        }

        @Override
        public void onCancel(Platform platform, int i) {
            final String msg = "取消分享!";
            PopTipUtils.showToast(msg);
            executeJSFunction(BaseApplication.getInstance().currShowWebView, shareJsCallback, new ArrayList<Object>(){{add(-2); add(msg);}});
        }
    };

    public interface CallBack {
        public void Back(boolean isSucess, String value);
    }
}
