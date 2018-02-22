package com.clearlee.JsWebViewInteraction;

import android.app.Activity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static com.clearlee.JsWebViewInteraction.JSInterface.executeJSFunction;
import static com.clearlee.JsWebViewInteraction.JSInterface.locateJsCallback;

public class LocationUtil {

    static LocationUtil locationUtil;

    boolean isOnceLoc = true;//是否为一次性定位

    public static LocationUtil getInstance() {
        if(locationUtil == null){
            synchronized (LocationUtil.class){
                if(locationUtil == null){
                    locationUtil = new LocationUtil();
                }
            }
        }
        return locationUtil;
    }

    BDLocation currentLocation;//定位信息

    public BDLocation getCurrentLocaiton() {
        if (currentLocation == null) {
            currentLocation = new BDLocation();
            currentLocation.setLatitude(0);
            currentLocation.setLongitude(0);
        }
        return currentLocation;
    }

    public void setCurrentLocation(BDLocation location) {
        currentLocation = location;
    }

    public MyLocationListenner myLocationListener = new MyLocationListenner();

    LocationClient mLocClient;

    public void locate(Activity activity, boolean onceLoc, boolean openGps, String coortype, int scanspan,
                       boolean needAddr) {
        isOnceLoc = onceLoc;

        //如果已经在定位，先关闭定位
        if (mLocClient != null && mLocClient.isStarted()) mLocClient.stop();

        mLocClient = new LocationClient(activity);
        mLocClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(openGps); // 打开gps
        option.setCoorType(coortype); // 设置坐标类型
        option.setScanSpan(scanspan);
        option.setIsNeedAddress(needAddr);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    public void closeLoc() {
        try {
            if (mLocClient != null) mLocClient.stop();
        } catch (Exception e) {
            LogUtils.ex(e);
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
        MapView mMapView;
        boolean isFirstLoc = true;//是否为第一次定位

        MyLocationListenner(MapView mMapView) {
            this.mMapView = mMapView;
            isFirstLoc = true;
        }

        MyLocationListenner() {

        }

        @Override
        public void onReceiveLocation(BDLocation location) {

            if (location == null ||
                    location.getLatitude() == 0 ||
                    location.getLongitude() == 0 ||
                    location.getLongitude() == 4.9E-324D ||
                    location.getLatitude() == 4.9E-324D) {
                return;
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            LogUtils.v(getLocation(location));

//            final String msg = isOnceLoc + JSON.toJSONString(locData) + "\n" + getLocation(location);
            final String msg = "定位地址："+location.getAddrStr();

            executeJSFunction(BaseApplication.getInstance().currShowWebView, locateJsCallback, new ArrayList<Object>() {{
                add(0);
                add(msg);
            }});

            if (isOnceLoc) {
                closeLoc();
            }

            if(mMapView != null){
                mMapView.getMap().setMyLocationData(locData);
                if (isFirstLoc) {
                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(18.0f);
                    mMapView.getMap().animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
            }

            isFirstLoc = false;
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    private String getLocation(BDLocation location) {
        //Receive Location
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
            sb.append("\ndirection : ");
            sb.append(location.getDirection());// 单位度
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        List<Poi> list = location.getPoiList();// POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }
        return sb.toString();
    }
}
