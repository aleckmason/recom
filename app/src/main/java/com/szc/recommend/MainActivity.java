package com.szc.recommend;

import android.app.Activity;
import android.os.Bundle;

import recommend.aleck.com.recommend.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecommendView rv = findViewById(R.id.rv);
        rv.hideTitle();;

    }
}
