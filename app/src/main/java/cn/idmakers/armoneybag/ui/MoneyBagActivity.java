package cn.idmakers.armoneybag.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;
import cn.idmakers.armoneybag.App;
import cn.idmakers.armoneybag.R;
import cn.idmakers.armoneybag.scan.decode.DecodeThread;
import cn.idmakers.armoneybag.scan.utils.BitmapCompare;
import cn.idmakers.armoneybag.util.FileUtil;
import cn.idmakers.armoneybag.util.LUtil;

public class MoneyBagActivity extends AppCompatActivity {

    @BindView(R.id.cttlayout_etext)EditText etMoney;
    @BindView(R.id.cttlayout_btn)Button btnSend;
    @BindView(R.id.cttlayout_tv_img)TextView tvImg;
    @BindView(R.id.cttlayout_tv_unit)TextView tvUnit;

    private static final int QUEST_AR_CODE = 100;
    private String imgpath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_money_bag);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.cttlayout_btn)void doSend(){
        String sum = etMoney.getText().toString();
        if(sum.length()>0){
            startActivityForResult(new Intent(this,CaptureActivity.class),QUEST_AR_CODE);
        }
    }

    @OnLongClick(R.id.cttlayout_tv_img)boolean  sharMoney(){

        FileUtil.shareMsg(this,"AR红包","领领红包","我在"+ App.getLocation()+"给你发了一个红包，赶快来领吧！"+imgpath+"%\nhttp://192.168.0.133:8020/tibetcement/tabtlesetting.html\n"+App.getLocationValue(),null);
        finish();
        return true;
    }

    @OnTextChanged(R.id.cttlayout_etext)void onTextChanged(CharSequence s){
        if (s.toString().contains(".")) {
            if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                etMoney.setText(s.toString().subSequence(0,
                        s.toString().indexOf(".") + 3));
                etMoney.setSelection(s.length());
            }
        }
        if (s.toString().trim().substring(0).equals(".")) {
            s = "0" + s;
            etMoney.setText(s);
            etMoney.setSelection(2);
        }

        if (s.toString().startsWith("0")
                && s.toString().trim().length() > 1) {
            if (!s.toString().substring(1, 2).equals(".")) {
                etMoney.setText(s.subSequence(0, 1));
                etMoney.setSelection(1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == QUEST_AR_CODE){
            if(resultCode ==RESULT_OK){
                Bundle bundle = data.getExtras();
                byte[] bmpData = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
                int width = bundle.getInt("width");
                int height = bundle.getInt("height");
                Bitmap moneyBmp = BitmapFactory.decodeByteArray(bmpData,0,bmpData.length);
//                File dirFile = FileUtil.createFileDir(this,"ARMoney");
//                imgpath = FileUtil.saveBitmap(dirFile,"temp.png",moneyBmp);
                imgpath = BitmapCompare.bitmapToBase64(moneyBmp);
                if(imgpath==null){
                    Toast.makeText(this,"红包保存失败，请再次尝试",Toast.LENGTH_SHORT).show();
                }else{
                    tvImg.setText(etMoney.getText()+"元");
                    tvImg.setVisibility(View.VISIBLE);
                    etMoney.setVisibility(View.GONE);
                    btnSend.setVisibility(View.GONE);
                    tvUnit.setVisibility(View.GONE);
                }

            }else if(resultCode == RESULT_CANCELED){
                LUtil.e("自动取消");
            }else{
                Toast.makeText(this,"AR红包隐藏失败",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
