package cn.idmakers.armoneybag.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * SharedPreferences 工具类
 * Created by 魏兴 on 2017/7/19.
 */

public class SharedPrefsUtil {

    private static final String SPTAG = "ARMoneyBag";
    /**
     * 向SharedPreferences中写入int类型数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param value 值
     */
    public static void putValue(Context context, String key,
                                int value) {
        SharedPreferences.Editor sp = getEditor(context);
        sp.putInt(key, value);
        sp.commit();
    }

    /**
     * 向SharedPreferences中写入boolean类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param value 值
     */
    public static void putValue(Context context, String key,
                                boolean value) {
        SharedPreferences.Editor sp = getEditor(context);
        sp.putBoolean(key, value);
        sp.commit();
    }

    /**
     * 向SharedPreferences中写入String类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param value 值
     */
    public static void putValue(Context context, String key,
                                String value) {
        SharedPreferences.Editor sp = getEditor(context);
        sp.putString(key, value);
        sp.commit();
    }

    /**
     * 向SharedPreferences中写入float类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param value 值
     */
    public static void putValue(Context context, String key,
                                float value) {
        SharedPreferences.Editor sp = getEditor(context);
        sp.putFloat(key, value);
        sp.commit();
    }

    /**
     * 向SharedPreferences中写入long类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param value 值
     */
    public static void putValue(Context context, String key,
                                long value) {

        SharedPreferences.Editor sp = getEditor(context);
        sp.putLong(key, value);
        sp.commit();

    }

    /**
     * 向SharedPreferences中写入Double类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param value 值
     */
    public static void putValue(Context context, String key,
                                Double value) {

        SharedPreferences.Editor sp = getEditor(context);
        sp.remove(key);
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(value);
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, strJson);
        editor.commit();
    }

    /**
     *
     * 向SharedPreferences中写入Bitmap类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param bitmap 值
     */
    public static void putValue(Context context, String key, Bitmap bitmap){
        SharedPreferences.Editor sp = getEditor(context);
        sp.remove(key);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, Base64.encodeToString(appicon, Base64.DEFAULT));
        editor.commit();

    }

    /**
     *  向SharedPreferences中写入List类型的数据
     *
     * @param context 上下文环境
     * @param key 值
     * @param datalist 值
     * @param <T> 数据类型
     */
    public static <T> void setDataList(Context context, String key, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, strJson);
        editor.commit();

    }


    /**
     * 从SharedPreferences中读取int类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param defValue 如果读取不成功则使用默认值
     * @param empty 是否置空
     * @return  返回读取的值
     */
    public static int getValue(Context context, String key,
                               int defValue, boolean empty) {
        SharedPreferences sp = getSharedPreferences(context);
        int value = sp.getInt(key, defValue);
        if(empty){
            sp.edit().remove(key);
        }
        return value;
    }

    /**
     * 从SharedPreferences中读取boolean类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param defValue 如果读取不成功则使用默认值
     * @param empty 是否置空
     * @return  返回读取的值
     */
    public static boolean getValue(Context context, String key,
                                   boolean defValue, boolean empty) {
        SharedPreferences sp = getSharedPreferences(context);
        boolean value = sp.getBoolean(key, defValue);
        if(empty){
            sp.edit().remove(key);
        }
        return value;
    }

    /**
     * 从SharedPreferences中读取String类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param defValue 如果读取不成功则使用默认值
     * @param empty 是否置空
     * @return  返回读取的值
     */
    public static String getValue(Context context, String key,
                                  String defValue, boolean empty) {
        SharedPreferences sp = getSharedPreferences(context);
        String value = sp.getString(key, defValue);
        if(empty)
            sp.edit().remove(key);
        return value;
    }

    /**
     * 从SharedPreferences中读取float类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param defValue 如果读取不成功则使用默认值
     * @param empty 是否置空
     * @return  返回读取的值
     */
    public static float getValue(Context context, String key,
                                 float defValue, boolean empty) {
        SharedPreferences sp = getSharedPreferences(context);
        float value = sp.getFloat(key, defValue);
        if(empty)
            sp.edit().remove(key);
        return value;
    }

    /**
     * 从SharedPreferences中读取long类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param defValue 如果读取不成功则使用默认值
     * @return  返回读取的值
     */
    public static long getValue(Context context, String key,
                                long defValue, boolean empty) {

        SharedPreferences sp = getSharedPreferences(context);
        long value = sp.getLong(key, defValue);
        if(empty)
            sp.edit().remove(key);

        return value;
    }
    /**
     * 从SharedPreferences中读取long类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param defValue 如果读取不成功则使用默认值
     * @return  返回读取的值
     */
    public static Double getValue(Context context, String key,
                                  double defValue, boolean empty) {

        SharedPreferences sp = getSharedPreferences(context);
        String value = sp.getString(key,null);
        if(null==value){
            return defValue;
        }
        Gson gson = new Gson();
        Double result = gson.fromJson(value,Double.class);
        if(empty)
            sp.edit().remove(key);
        return result;
    }

    /**
     * 从SharedPreferences中读取 Bitmap 类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param defValue 如果读取不成功则使用默认值
     * @return  返回读取的值
     */
    public static Bitmap getValue(Context context, String key, Bitmap defValue, boolean empty){
        SharedPreferences sp = getSharedPreferences(context);
        String value = sp.getString(key,null);
        if(null==value){
            return defValue;
        }
        Bitmap bitmap = null;
        try
        {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(value, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            if(empty)
                sp.edit().remove(key);
            return bitmap;
        }
        catch (Exception e)
        {
            return defValue;
        }
    }
    /**
     * 从SharedPreferences中读取List类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param empty 是否置空
     * @param <T> 数据类型
     * @return 返回List数据
     */
    public static <T> ArrayList<T> getDataList(Context context, String key, Type type, boolean empty) {

        ArrayList<T> datalist=new ArrayList<T>();
        SharedPreferences sp = getSharedPreferences(context);
        String strJson = sp.getString(key, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson,type);
        if(empty)
            sp.edit().remove(key);
        return datalist;

    }

    /**
     * 从SharedPreferences中读取ArrayList<Long> 类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param empty 是否置空
     * @return 返回ArrayList<Long>
     */
    public static ArrayList<Long> getLongData(Context context, String key, boolean empty){
        return getDataList(context,key,new TypeToken<List<Long>>() {
        }.getType(),empty);
    }
    /**
     * 从SharedPreferences中读取ArrayList<String> 类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param empty 是否置空
     * @return 返回ArrayList<Long>
     */
    public static ArrayList<String> getStringData(Context context, String key, boolean empty){
        return getDataList(context,key,new TypeToken<List<String>>() {
        }.getType(),empty);
    }

    /**
     * 从SharedPreferences中读取ArrayList<Integer> 类型的数据
     *
     * @param context 上下文环境
     * @param key 键
     * @param empty 是否置空
     * @return 返回ArrayList<Long>
     */
    public static ArrayList<Integer> getIntegerData(Context context, String key, boolean empty){
        return getDataList(context,key,new TypeToken<List<Integer>>() {
        }.getType(),empty);
    }

    //获取Editor实例
    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    //获取SharedPreferences实例
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(SPTAG, Context.MODE_PRIVATE);
    }
}
