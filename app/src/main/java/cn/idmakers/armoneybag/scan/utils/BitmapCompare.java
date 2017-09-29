package cn.idmakers.armoneybag.scan.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 图片相似度计算
 * Created by 魏兴 on 2017/9/25.
 */

public class BitmapCompare {

    /**
     * 获取图片哈希值
     * @param bitmap
     * @return
     */
    public static String bitmapCompare(Bitmap bitmap){
        String result = null;
        bitmap = convertGreyImg(bitmap);
        int avg = getAvg(bitmap);
        String binary = getBinary(bitmap,avg);
        result = binaryString2hexString(binary);
        return result;
    }

    /**
     * 简化色彩
     * @param img
     * @return
     */
    public static Bitmap convertGreyImg(Bitmap img) {
        int width = img.getWidth();         //获取位图的宽
        int height = img.getHeight();       //获取位图的高

        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int original = pixels[width * i + j];
                int red = ((original & 0x00FF0000) >> 16);
                int green = ((original & 0x0000FF00) >> 8);
                int blue = (original & 0x000000FF);

                int grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }

    /**
     * 计算像素平均值
     * @param img
     * @return
     */
    public static int getAvg(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = new int[width * height];
        img.getPixels(pixels, 0, width, 0, 0, width, height);

        int avgPixel = 0;
        for (int pixel : pixels) {
            avgPixel += pixel;
        }
        return avgPixel / pixels.length;
    }

    /**
     * 计算像素灰度
     * @param img
     * @param average
     * @return
     */
    public static String getBinary(Bitmap img, int average) {
        StringBuilder sb = new StringBuilder();

        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = new int[width * height];

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int original = pixels[width * i + j];
                if (original >= average) {
                    pixels[width * i + j] = 1;
                } else {
                    pixels[width * i + j] = 0;
                }
                sb.append(pixels[width * i + j]);
            }
        }
        return sb.toString();
    }

    /**
     * 计算哈希值
     * @param bString
     * @return
     */
    public static String binaryString2hexString(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuilder sb = new StringBuilder();
        int iTmp;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            sb.append(Integer.toHexString(iTmp));
        }
        return sb.toString();
    }

    /**
     * 比较哈希值
     * @param s1
     * @param s2
     */
    public static int diff(String s1, String s2) {
        char[] s1s = s1.toCharArray();
        char[] s2s = s2.toCharArray();
        int diffNum = 0;
        for (int i = 0; i<s1s.length; i++) {
            if (s1s[i] != s2s[i]) {
                diffNum++;
            }
        }
        return diffNum;
    }

    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
