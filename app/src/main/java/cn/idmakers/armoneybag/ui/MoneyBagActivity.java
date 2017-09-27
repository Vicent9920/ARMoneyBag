package cn.idmakers.armoneybag.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.idmakers.armoneybag.R;

public class MoneyBagActivity extends AppCompatActivity {

    @BindView(R.id.cttlayout_etext)EditText etMoney;
    @BindView(R.id.cttlayout_btn)Button btnSend;
    @BindView(R.id.cttlayout_tv_img)TextView tvImg;
private StringBuilder sumValue;
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

        }
    }

    @OnTextChanged(R.id.cttlayout_etext)void onTextChanged(CharSequence text){
        sumValue.append(text);
        if(sumValue.substring(0,1).equals(".")){
            sumValue.insert(0,"0");
        }
        if(sumValue.indexOf(".")>1){
//            sumValue.delete()
        }
    }

}
