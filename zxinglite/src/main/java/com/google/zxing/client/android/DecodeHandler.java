/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.R;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class DecodeHandler extends Handler {

  private static final String TAG = DecodeHandler.class.getSimpleName();

  private final BaseCaptureActivity activity;
  private final MultiFormatReader multiFormatReader;
  private boolean running = true;

  private int bmpCount = 0;
  private String lastHashValue,currentHashValue = null;
  public String moneyValue = null;
  private List<Bitmap> bmpData = new ArrayList<>();

  DecodeHandler(BaseCaptureActivity activity, Map<DecodeHintType,Object> hints) {
    multiFormatReader = new MultiFormatReader();
    multiFormatReader.setHints(hints);
    this.activity = activity;
    if(this.activity.isFind){
      Bitmap bmp = null;
      bmp = getValue(activity.getBaseContext(),"bitmap",bmp,false);
      moneyValue = BitmapCompare.bitmapCompare(bmp);

    }
  }

  @Override
  public void handleMessage(Message message) {
    if (!running) {
      return;
    }
    switch (message.what) {
      case Ids.decode:
        decode((byte[]) message.obj, message.arg1, message.arg2);
        break;
      case Ids.quit:
        running = false;
        Looper.myLooper().quit();
        break;
    }
  }

  /**
   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
   * reuse the same reader objects from one decode to the next.
   *
   * @param data   The YUV preview frame.
   * @param width  The width of the preview frame.
   * @param height The height of the preview frame.
   */
  private void decode(byte[] data, int width, int height) {
    long start = System.currentTimeMillis();
    Result rawResult = null;
    PlanarYUVLuminanceSource source = activity.getCameraManager().buildLuminanceSource(data, width, height);
    if (source != null) {
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
      YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      image.compressToJpeg(new Rect(0, 0, width, height), 80, stream);
      Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

      if(moneyValue==null){
        bmpData.add(bmp);
        if(bmpData.size()%2 == 1){
          lastHashValue = BitmapCompare.bitmapCompare(bmp);
        }else{
          currentHashValue = BitmapCompare.bitmapCompare(bmp);
          int diff = BitmapCompare.diff(lastHashValue,currentHashValue);
          Log.e(TAG,"相识度："+diff+"lastHashValue："+lastHashValue+"currentHashValue："+currentHashValue);
          bmpData.clear();
          currentHashValue = lastHashValue = null;
          if(diff == 0){
            bmpCount++;
          }
        }
      }else {
        currentHashValue = BitmapCompare.bitmapCompare(bmp);
        int diff = BitmapCompare.diff(moneyValue,currentHashValue);
        if(diff <= 1){
          bmpCount = 2;
        }
      }
//		decodeData(bmp);

      if(bmpCount!=2){
        source = null;
      }


      Handler handler = activity.getHandler();
      if (source != null) {
        // Don't log the barcode contents for security.
        if (handler != null) {
          Message message = Message.obtain(handler, R.id.decode_succeeded);
          Bundle bundle = new Bundle();
          bundle.putString("bmp",BitmapCompare.bitmapToBase64(bmp));
//				bundleThumbnail(source, bundle);
          message.setData(bundle);
          message.sendToTarget();
        }
      } else {
        if (handler != null) {
          Message message = Message.obtain(handler, R.id.decode_failed);
          message.sendToTarget();
        }
      }
    }


  }

  /**
   * A factory method to build the appropriate LuminanceSource object based on
   * the format of the preview buffers, as described by Camera.Parameters.
   *
   * @param data
   *            A preview frame.
   * @param width
   *            The width of the image.
   * @param height
   *            The height of the image.
   * @return A PlanarYUVLuminanceSource instance.
   */
  public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
    Rect rect = activity.cameraManager.getFramingRectInPreview();
    if (rect == null) {
      return null;
    }
    // Go ahead and assume it's YUV rather than die.
    return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
  }

  private static void bundleThumbnail(PlanarYUVLuminanceSource source, Bundle bundle) {
    int[] pixels = source.renderThumbnail();
    int width = source.getThumbnailWidth();
    int height = source.getThumbnailHeight();
    Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
    bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
    bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, (float) width / source.getWidth());
  }

  private Bitmap getValue(Context context, String key, Bitmap defValue, boolean empty){
    SharedPreferences sp = context.getApplicationContext().getSharedPreferences("ARMoneyBag", Context.MODE_PRIVATE);
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

}
