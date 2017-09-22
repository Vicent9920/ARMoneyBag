package cn.idmakers.armoneybag.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import cn.idmakers.armoneybag.R;
import cn.idmakers.armoneybag.test.jsbrid.BridgeHandler;
import cn.idmakers.armoneybag.test.jsbrid.BridgeWebView;
import cn.idmakers.armoneybag.test.jsbrid.CallBackFunction;
import cn.idmakers.armoneybag.test.jsbrid.DefaultHandler;
import cn.idmakers.armoneybag.util.LUtil;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener,BridgeHandler {

    BridgeWebView webView;

    Button button;

    @Override
    public void onClick(View view) {
        if (button.equals(view)) {
            webView.callHandler("functionInJs", "data from Java", new CallBackFunction() {

                @Override
                public void onCallBack(String data) {
                    // TODO Auto-generated method stub
                    LUtil.e("reponse data from js " + data);
                }

            });
        }
    }

    @Override
    public void handler(String name, String data, CallBackFunction function) {
        LUtil.e(name);
        switch (name){
            case "submitFromWeb":
                LUtil.e("handler = submitFromWeb, data from web = " + data);
                function.onCallBack("submitFromWeb exe, response data 中文 from Java");
                break;
        }

    }

    static class Location {
        String address;
    }

    static class User {
        String name;
        Location location;
        String testStr;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        webView = (BridgeWebView) findViewById(R.id.web_view);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        webView.setDefaultHandler(new DefaultHandler());
        webView.loadUrl("file:///android_asset/demo.html");

        webView.setDefaultHandler(this);

        User user = new User();
        Location location = new Location();
        location.address = "SDU";
        user.location = location;
        user.name = "大头鬼";

        sendMessage("functionInJs", new Gson().toJson(user), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                LUtil.e("functionInJs:"+data);
            }
        });
        sendMessage("hello",null,null);

    }

    public void sendMessage(String name, String data, CallBackFunction callback) {
        webView.callHandler(name,data,callback);
    }
}
