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

package cn.idmakers.armoneybag.scan.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.idmakers.armoneybag.R;
import cn.idmakers.armoneybag.scan.utils.BitmapCompare;
import cn.idmakers.armoneybag.ui.CaptureActivity;

public class DecodeHandler extends Handler {

	private static final String TAG = "DecodeHandler";
	private final CaptureActivity activity;
	private final MultiFormatReader multiFormatReader;
	private List<Bitmap> bmpData = new ArrayList<>();
	private boolean running = true;
	private int bmpCount = 0;

	public DecodeHandler(CaptureActivity activity, Map<DecodeHintType, Object> hints) {
		multiFormatReader = new MultiFormatReader();
		multiFormatReader.setHints(hints);
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message message) {
		if (!running) {
			return;
		}
		switch (message.what) {
		case R.id.decode:
			decode((byte[]) message.obj, message.arg1, message.arg2);
			break;
		case R.id.quit:
			running = false;
			Looper.myLooper().quit();
			break;
		}
	}

	String lastHashValue,currentHashValue = null;
	/**
	 * Decode the data within the viewfinder rectangle, and time how long it
	 * took. For efficiency, reuse the same reader objects from one decode to
	 * the next.
	 * 
	 * @param data
	 *            The YUV preview frame.
	 * @param width
	 *            The width of the preview frame.
	 * @param height
	 *            The height of the preview frame.
	 */
	private void decode(byte[] data, int width, int height) {
		Size size = activity.getCameraManager().getPreviewSize();
		Log.e(TAG,"decode");
		// 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
		byte[] rotatedData = new byte[data.length];
		for (int y = 0; y < size.height; y++) {
			for (int x = 0; x < size.width; x++)
				rotatedData[x * size.height + size.height - y - 1] = data[x + y * size.width];
		}
		for (int i = 0; i < rotatedData.length; i++) {

		}
		// 宽高也要调整
		int tmp = size.width;
		size.width = size.height;
		size.height = tmp;
		YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
		Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
		bmpData.add(bmp);

//		decodeData(bmp);
		if(bmpData.size()%2 == 1){
			lastHashValue = BitmapCompare.bitmapCompare(bmp);
		}else{
			currentHashValue = BitmapCompare.bitmapCompare(bmp);
			int diff = BitmapCompare.diff(lastHashValue,currentHashValue);
			Log.e(TAG,"相识度："+diff);
			bmpData.clear();
			currentHashValue = lastHashValue = null;
			if(diff == 0){
				bmpCount++;
			}
		}
		PlanarYUVLuminanceSource source = null;
		if(bmpCount==3){
			source = buildLuminanceSource(rotatedData, size.width, size.height);
		}


		Handler handler = activity.getHandler();
		if (source != null) {
			// Don't log the barcode contents for security.
			if (handler != null) {
				Message message = Message.obtain(handler, R.id.decode_succeeded);
				Bundle bundle = new Bundle();
				bundleThumbnail(source, bundle);
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

	private static void bundleThumbnail(PlanarYUVLuminanceSource source, Bundle bundle) {
		int[] pixels = source.renderThumbnail();
		int width = source.getThumbnailWidth();
		int height = source.getThumbnailHeight();
		Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
		bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
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
		Rect rect = activity.getCropRect();
		if (rect == null) {
			return null;
		}
		// Go ahead and assume it's YUV rather than die.
		return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
	}



}
