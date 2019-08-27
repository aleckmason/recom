package com.szc.recommend;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

import recommend.aleck.com.recommend.R;


/**
 * Created by Administrator on 2019/8/24.
 */

public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_help);

        WebView mWebView = findViewById(R.id.weibview);
        String url = getIntent().getStringExtra("url");
        mWebView.loadUrl(url);
    }


}
