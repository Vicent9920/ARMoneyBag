package cn.idmakers.armoneybag;

import android.app.Application;

import cn.idmakers.armoneybag.scan.utils.DesUtils;
import cn.idmakers.armoneybag.util.LocationUtil;

/**
 * Created by asus on 2017/9/28.
 */

public class App extends Application{

    public static LocationUtil location;
    @Override
    public void onCreate() {
        super.onCreate();
        location = new LocationUtil(this);
    }
    public static String getLocation(){
        return location.getLocation();
    }
    public static String getLocationValue(){
        String value = location.getLonAndLat();
        try {
            value = DesUtils.strToUnicode(value);
        } catch (Exception e) {
            return null;
        }
        return value;
    }
}
