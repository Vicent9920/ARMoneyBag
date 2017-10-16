package cn.idmakers.armoneybag.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;

import com.google.zxing.client.android.BaseCaptureActivity;

import cn.idmakers.armoneybag.R;

public class AlipayCaptureActivity extends BaseCaptureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay_capture);
    }

    @Override
    public SurfaceView getSurfaceView() {
        return null;
    }



    @Override
    public void handleDecode(Bundle bundle) {
        Intent resultIntent = new Intent();
//        bundle.putInt("width", mCropRect.width());
//        bundle.putInt("height", mCropRect.height());
        resultIntent.putExtras(bundle);
        this.setResult(RESULT_OK,resultIntent);
        this.finish();
    }
}
