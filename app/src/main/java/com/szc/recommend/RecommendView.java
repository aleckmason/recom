package com.szc.recommend;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import recommend.aleck.com.recommend.R;

/**
 * Created by Administrator on 2019/8/24.
 */

public class RecommendView extends LinearLayout {
    private Handler mHandler = new Handler();
    private Context mContext;
    private LinearLayout mContainer;
    private TextView mTitle;
    public RecommendView(Context context) {
        super(context);
        mContext = mContext;
        init();
    }

    public RecommendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }



    private void init() {
        loadAppItems(new Callback() {
            @Override
            public void onResult(List<AppItem> data) {
                initUI(data);
            }
        });
    }

    private void initUI(List<AppItem> data) {
        if(data == null) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(mContext);
        inflater.inflate(R.layout.recommend_view,this);

        mContainer = findViewById(R.id.container);
        for(int i = 0;i < data.size();i++) {
            AppItem item = data.get(i);
            item.setIconUrl(Constants.ROOT_URL + item.getIconUrl());
            item.setDownloadUrl(Constants.ROOT_URL + item.getDownloadUrl());
            if( item.bannerList != null) {
                for(int j = 0;j < item.bannerList.size();j++) {
                    item.getBannerList().set(j,Constants.ROOT_URL + item.getBannerList().get(j));
                }
            }



            View itemView = inflater.inflate(R.layout.appitem_view,null);
            NetRoundImageView icon = itemView.findViewById(R.id.icon);
            TextView name = itemView.findViewById(R.id.name);
            String iconUrl = item.getIconUrl();
            icon.setImageURL(item.getIconUrl());
            name.setText(item.getAppName());

            mContainer.addView(itemView);
        }
    }

    public void setTitle(String text) {
        mTitle.setText(text);
    }
    public void setTitleColor(int colorResId) {
        mTitle.setTextColor(mContext.getResources().getColor(colorResId));
    }

    private void loadAppItems(final Callback callback) {


            new Thread(new Runnable() {
                @Override
                public void run() {


                    HttpRequester hr = new HttpRequester();
                    HashMap<String,String> params = new HashMap<>();
                    try {


                         HttpRespons respon = hr.sendPost(Constants.GET_APP_LIST, params, null);

                        final JSONObject json = new JSONObject(respon.content);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Gson gson = new Gson();
                                    AppItem[] list = gson.fromJson(json.get("data").toString(), AppItem[].class);
                                    if(list != null) {
                                        callback.onResult(Arrays.asList(list));
                                    }

                                } catch (Exception e) {
                                    callback.onResult(null);
                                }

                            }

                        });

                    } catch (Exception e) {
                        callback.onResult(null);
                        System.out.println("hahaha");
                        e.printStackTrace();
                    } finally {

                    }
                }
            }).start();
        }


    interface Callback {
        public void onResult(List<AppItem> data);
    }
}
