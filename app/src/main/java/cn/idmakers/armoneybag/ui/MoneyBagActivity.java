package cn.idmakers.armoneybag.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.BitmapCompare;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;
import cn.idmakers.armoneybag.App;
import cn.idmakers.armoneybag.R;
import cn.idmakers.armoneybag.util.FileUtil;
import cn.idmakers.armoneybag.util.LUtil;
import cn.idmakers.armoneybag.util.SharedPrefsUtil;

import static cn.idmakers.armoneybag.App.location;

public class MoneyBagActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.cttlayout_etext)EditText etMoney;
    @BindView(R.id.cttlayout_btn)Button btnSend;
    @BindView(R.id.cttlayout_tv_img)TextView tvImg;
    @BindView(R.id.cttlayout_tv_unit)TextView tvUnit;
    @BindView(R.id.tb_toolbar)Toolbar toolbar;
    /**
     * 请求打开相机
     */
    private static final int QUEST_AR_OPEN = 100;
    private static final int  QUEST_AR_FIND = 101;
    /**
     * 图片资源
     */
    private String imgpath = null;
    /**
     * 是否是外部打开
     */
    private boolean isFromOut = false;

    /**
     * 外部传过来的图片Id
     */
    private int bmpId = -1;
    /**
     * 外部传过来的红包坐标
     */
    private String bmpLocation;
    private String money = "";

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
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(this);
        String action = getIntent().getAction();
        if(!TextUtils.isEmpty(action)&&Intent.ACTION_VIEW.equals(action)){
            Uri uri = getIntent().getData();
            if(uri != null){
               LUtil.e("外部打开");
                isFromOut = true;
                String id = uri.getQueryParameter("id");
                bmpId = Integer.valueOf(id);
                bmpLocation = new String(Base64.decode(uri.getQueryParameter("location"), Base64.NO_WRAP));
                money = uri.getQueryParameter("money");
                LUtil.e("id:"+bmpId+"\nlocation:"+location);
            }
        }

    }

    @OnClick(R.id.cttlayout_btn)void doSend(){
        String sum = etMoney.getText().toString();
        if(sum.length()>0){
            startActivityForResult(new Intent(this,AlipayCaptureActivity.class),QUEST_AR_OPEN);
        }
    }

    /**
     * 分享红包   外部网页打开
     * @return
     */
    @OnLongClick(R.id.cttlayout_tv_img)boolean  sharMoney(){
        LUtil.e("加密的地址："+App.getLocationValue());
        if(bmpId == -1){
            FileUtil.shareMsg(this,"AR红包","领领红包","我在"+ App.getLocation()+"给你发了一个红包，赶快来领吧！"
                    +imgpath+"%\nhttp://192.168.199.237:8020/西藏/tabtlesetting.html\n"+App.getLocationValue(),null);
        }else{
            FileUtil.shareMsg(this,"AR红包","领领红包","我在"+ App.getLocation()+"得到一个红包，赶快来领吧！"
                    +imgpath+"%\nhttp://192.168.199.237:8020/西藏/tabtlesetting.html\n"+App.getLocationValue(),null);
        }

        finish();
        return true;
    }

    @OnClick(R.id.cttlayout_tv_img)void openMoneyBag(){
        Intent intent = new Intent(this,AlipayCaptureActivity.class);
        intent.putExtra("find",(bmpId != -1));
        startActivityForResult(intent,QUEST_AR_FIND);

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
        if(requestCode == QUEST_AR_OPEN){
            if(resultCode ==RESULT_OK){
                Bundle bundle = data.getExtras();
//                byte[] bmpData = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
                String value = bundle.getString("bmp");
//                int width = bundle.getInt("width");
//                int height = bundle.getInt("height");
//                Bitmap moneyBmp = BitmapFactory.decodeByteArray(bmpData,0,bmpData.length);
                Bitmap moneyBmp = BitmapCompare.base64ToBitmap(value);
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
                    SharedPrefsUtil.putValue(MoneyBagActivity.this,"bitmap",moneyBmp);
                }

            }else if(resultCode == RESULT_CANCELED){
                LUtil.e("自动取消");
            }else{
                Toast.makeText(this,"AR红包隐藏失败",Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == QUEST_AR_FIND){
            if(resultCode == RESULT_OK){
                Toast.makeText(this,"红包已经收入钱包中了！",Toast.LENGTH_SHORT).show();
            }else if(resultCode == RESULT_CANCELED){
                LUtil.e("自动取消");
            }else{
                Toast.makeText(this,"红包打开失败！",Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(this,MainActivity.class));
//            finish();
        }


    }

    @Override
    public void onClick(View view) {
       onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(bmpId != -1){
            tvImg.setText(money+"元");
            tvImg.setVisibility(View.VISIBLE);
            etMoney.setVisibility(View.GONE);
            btnSend.setVisibility(View.GONE);
            tvUnit.setVisibility(View.GONE);
            Bitmap bmp = SharedPrefsUtil.getValue(this,"bitmap",((BitmapDrawable)tvImg.getBackground()).getBitmap(),false);
            Drawable drawable = new BitmapDrawable(bmp);
            tvImg.setBackground(drawable);
        }
    }

    @Override
    public void onBackPressed() {
        if(isFromOut){
            startActivity(new Intent(this,MainActivity.class));
        }
        super.onBackPressed();
    }
}
