package cn.idmakers.armoneybag.util;

import android.util.Log;

/**
 * 日志管理类
 * Created by 魏兴 on 2017/6/26.
 */

public class LUtil {
    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static final String TAG = "ARMoneyBag";

    // 下面四个是默认tag的函数
    public static void i(String msg)
    {
        if (isDebug){
            if(null==msg)msg="";
            Log.i(TAG, msg);
        }

    }

    public static void d(String msg)
    {
        if (isDebug){
            if(null==msg)msg="";
            Log.d(TAG, msg);
        }

    }

    public static void e(String msg)
    {
        if (isDebug){
            if(null==msg)msg="";
            if(msg.length()>4000){
                int len = msg.length();
                int size = len/4000;
                int count =len%4000;
                for (int i = 0; i < size; i++) {
                    Log.e(TAG,msg.substring(i*4000,(i+1)*4000));
                }
                Log.e(TAG,msg.substring(size*4000,msg.length()));
            }else{
                Log.e(TAG, msg);
            }

        }

    }
    public static void e(String msg, Exception e)
    {
        if (isDebug){
            if(null==msg)msg="";
            if(null!=e)
                Log.e(TAG, msg,e);
            else  Log.e(TAG, msg);
        }

    }

    public static void v(String msg)
    {
        if (isDebug){
            if(null==msg)msg="";
            Log.v(TAG, msg);
        }

    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg)
    {
        if (isDebug){
            if(null==msg)msg="";
            Log.i(tag, msg);
        }

    }

    public static void d(String tag, String msg)
    {
        if (isDebug){
            if(null==msg)msg="";
            Log.d(tag, msg);
        }

    }

    public static void e(String tag, String msg)
    {
        if (isDebug){
            if(null==msg)msg="";
            int acprt = 3000;
            if(msg.length() > acprt){
                int size = msg.length()/acprt;
                for (int i = 0; i < size; i++) {
                    int index = msg.length() > (i+1) * acprt  ?  (i+1) * acprt : msg.length();
                    String info = msg.substring(i * acprt,index);
                    Log.e(tag,info);
                }
            }else{
                Log.e(tag, msg);
            }

        }

    }
    public static void e(String tag, String msg, Exception e)
    {
        if (isDebug){
            if(null==msg)msg="";
            Log.e(tag, msg);
        }

    }
    public static void v(String tag, String msg)
    {
        if (isDebug){
            if(null==msg)msg="";
            Log.v(tag, msg);
        }

    }
}
