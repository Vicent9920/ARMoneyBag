package cn.idmakers.armoneybag.util;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * 地理位置信息
 * Created by asus on 2017/9/28.
 */
public class LocationUtil implements AMapLocationListener{

    private  String locationInfo = "";
    private String laonlatValue = "";
    public LocationUtil(Context context){
        // 初始化定位
        AMapLocationClient mlocationClient = new AMapLocationClient(context);
        // 设置定位回调监听
        mlocationClient.setLocationListener(this);

        // 初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        // 设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        // 设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        // 设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(true);
        // 设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(5000);
        // 给定位客户端对象设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 启动定位
        mlocationClient.startLocation();
    }


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null && amapLocation.getErrorCode() == 0) {
            locationInfo = amapLocation.getCity()+amapLocation.getDistrict()+amapLocation.getStreet()+amapLocation.getStreetNum();
            laonlatValue = amapLocation.getLatitude()+","+amapLocation.getLongitude();
        }

    }

    public String getLocation(){
        return locationInfo;
    }

    public String getLonAndLat(){
        return laonlatValue;
    }
}