package com.szc.recommend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private NetWorkChangReceiver mNetWorkLisntener = new NetWorkChangReceiver();
    private boolean bLoaded = false;
    private int mItemColor ;
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
        LayoutInflater inflater = LayoutInflater.from(mContext);
        inflater.inflate(R.layout.recommend_view,this);

        mTitle = findViewById(R.id.title);
        mContainer = findViewById(R.id.container);
        mItemColor = mContext.getResources().getColor(R.color.black);

        loadAppItems(new Callback() {
            @Override
            public void onResult(List<AppItem> data) {
                initUI(data);
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mNetWorkLisntener, filter);
    }

    private void initUI(List<AppItem> data) {
        if(data == null) {
            return;
        }
        bLoaded = true;
        mContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        for(int i = 0;i < data.size();i++) {
            View itemView = inflater.inflate(R.layout.appitem_view,null);
            final AppItem item = data.get(i);
            if(item.getPackageName().equals(SystemUtils.getPackageName(mContext))) {
                continue;
            }
            int a = 0;
            item.setIconUrl(Constants.ROOT_URL + item.getIconUrl());
            item.setDownloadUrl(Constants.ROOT_URL + item.getDownloadUrl());



            if( item.bannerList != null) {
                for(int j = 0;j < item.bannerList.size();j++) {
                    item.getBannerList().set(j,Constants.ROOT_URL + item.getBannerList().get(j));
                }
            }



            NetRoundImageView icon = itemView.findViewById(R.id.icon);
            TextView name = itemView.findViewById(R.id.name);
            String iconUrl = item.getIconUrl();
            icon.setImageURL(item.getIconUrl());
            name.setText(item.getAppName());
            name.setTextColor(mItemColor);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchAppDetail(mContext,item.packageName);

                }
            });
            mContainer.addView(itemView);
        }
    }

    public void setItemColor(int res) {
        mItemColor = mContext.getResources().getColor(res);

    }

    public void hideTitle()  {
        mTitle.setVisibility(View.GONE);
    }
    public void setTitle(String text) {
        mTitle.setText(text);
    }

    public void setTitleColor(int colorResId) {
        mTitle.setTextColor(mContext.getResources().getColor(colorResId));
    }

    public static void launchAppDetail(Context context , String appPkg) {
        try {
            if (TextUtils.isEmpty(appPkg))
                return;
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAppItems(final Callback callback) {


            new Thread(new Runnable() {
                @Override
                public void run() {

                    HttpRequester hr = new HttpRequester();
                    HashMap<String,String> params = new HashMap<>();
                    params.put("channel",SystemUtils.getMetaData(mContext,"channel"));
                    try {
                         HttpRespons respon = hr.sendPost(Constants.GET_APP_LIST, params, null);
                         System.out.println("loadAppItems result = " + respon.content);
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


    class NetWorkChangReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                //获取联网状态的NetworkInfo对象
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    //如果当前的网络连接成功并且网络连接可用
                    if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                        if (info.getType() == ConnectivityManager.TYPE_WIFI || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                            //  Log.i("TAG", getConnectionType(info.getType()) + "连上");
                            if(!bLoaded) {
                                refresh();
                            }

                        }
                    } else {


                    }
                }
            }
        }
    };


    void refresh() {
        loadAppItems(new Callback() {
            @Override
            public void onResult(List<AppItem> data) {
                initUI(data);
            }
        });
    }
}
