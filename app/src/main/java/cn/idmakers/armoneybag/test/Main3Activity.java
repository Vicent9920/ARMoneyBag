package cn.idmakers.armoneybag.test;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.nio.ByteBuffer;

import cn.idmakers.armoneybag.R;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Main3Activity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private TextureView mPreviewView;
    private Handler mHandler;
    private HandlerThread mThreadHandler;
    private Size mPreviewSize;
    private CaptureRequest.Builder mPreviewBuilder;
    private ImageReader mImageReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        initView();
        initLooper();
    }

    //很多过程都变成了异步的了，所以这里需要一个子线程的looper  
    private void initLooper() {
        mThreadHandler = new HandlerThread("CAMERA2");
        mThreadHandler.start();
        mHandler = new Handler(mThreadHandler.getLooper());
    }

    //可以通过TextureView或者SurfaceView  
    private void initView() {
        mPreviewView = (TextureView) findViewById(R.id.textureview);
        mPreviewView.setSurfaceTextureListener(this);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            //获得所有摄像头的管理者CameraManager  
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            //获得某个摄像头的特征，支持的参数  
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics("0");
            //支持的STREAM CONFIGURATION  
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            //摄像头支持的预览Size数组  
            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];
            //打开相机  
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraManager.openCamera("0", mCameraDeviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    // 这个方法要注意一下，因为每有一帧画面，都会回调一次此方法  
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            try {
                startPreview(camera);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    };
    // 开始预览，主要是camera.createCaptureSession这段代码很重要，创建会话  
    private void startPreview(CameraDevice camera) throws CameraAccessException {
        SurfaceTexture texture = mPreviewView.getSurfaceTexture();

//      这里设置的就是预览大小  
        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                Surface surface = new Surface(texture);
        try {
            // 设置捕获请求为预览，这里还有拍照啊，录像等  
            mPreviewBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

//      就是在这里，通过这个set(key,value)方法，设置曝光啊，自动聚焦等参数！！ 如下举例：  
//      mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);  




    }

    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession session) {
            try {
                updatePreview(session);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {

        }
    };

    private void updatePreview(CameraCaptureSession session) throws CameraAccessException {
        session.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);
    }

    private ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        /**
         *  当有一张图片可用时会回调此方法，但有一点一定要注意： 
         *  一定要调用 reader.acquireNextImage()和close()方法，否则画面就会卡住！！！！！我被这个坑坑了好久！！！ 
         *    很多人可能写Demo就在这里打一个Log，结果卡住了，或者方法不能一直被回调。 
         **/
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image img = reader.acquireNextImage();
            /**
             *  因为Camera2并没有Camera1的Priview回调！！！所以该怎么能到预览图像的byte[]呢？就是在这里了！！！我找了好久的办法！！！
             **/
            ByteBuffer buffer = img.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            img.close();
        }
    };
}
