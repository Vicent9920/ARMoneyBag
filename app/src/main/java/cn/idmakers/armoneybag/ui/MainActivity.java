package cn.idmakers.armoneybag.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.idmakers.armoneybag.R;

public class MainActivity extends AppCompatActivity {

    private static List<Bitmap> bmpData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.rLayout_btn_bag) void moneyBag() {
        startActivity(new Intent(this,MoneyBagActivity.class));
    }

    public static Bitmap getBitmap(int id){
        return bmpData.get(id);
    }

    public static void addBitmap(Bitmap bmp){
        bmpData.add(bmp);
    }
}
