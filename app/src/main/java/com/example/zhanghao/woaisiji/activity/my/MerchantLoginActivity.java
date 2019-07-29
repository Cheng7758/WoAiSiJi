package com.example.zhanghao.woaisiji.activity.my;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.example.zhanghao.woaisiji.R;

public class MerchantLoginActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_login);
        initView();
    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.merchant_login_webview);
    }
}
