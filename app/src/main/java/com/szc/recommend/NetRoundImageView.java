package com.szc.recommend;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetRoundImageView extends RoundConerImageView {

    public interface Callback {
        public void onFinish();
    }
    public static final int GET_DATA_SUCCESS = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int SERVER_ERROR = 3;
    private Bitmap mBitmap;
    private Callback mCallback;
    //子线程不能操作UI，通过Handler设置图片
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_DATA_SUCCESS:

                    setImageBitmap(mBitmap);
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if(mCallback != null) {
                                mCallback.onFinish();
                            }
                        }
                    });
                    break;
            }
        }
    };

    public NetRoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NetRoundImageView(Context context) {
        super(context);
    }

    public NetRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setImageURL(final String path, Callback callback) {
        mCallback = callback;
        setImageURL(path);
    }

    private int mImgType = 0;
    public final static int TYPE_FIT_X = 1;
    public final static int TYPE_FIT_Y = 2;
    public void setImageType(int type) {
        mImgType = type;
    }


    private String mUrl;

    public String getImageUrl() {
        return mUrl;
    }
    //设置网络图片
    public void setImageURL(final String path) {
        //开启一个线程用于联网
        mUrl = path;
        new Thread() {
            @Override
            public void run() {
                try {
                    //把传过来的路径转成URL
                    URL url = new URL(path);
                    //获取连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //使用GET方法访问网络
                    connection.setRequestMethod("GET");
                    //超时时间为10秒
                    connection.setConnectTimeout(10000);
                    //获取返回码
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = connection.getInputStream();
                        //使用工厂把网络的输入流生产Bitmap
                        mBitmap = BitmapFactory.decodeStream(inputStream);
                        //利用Message把图片发给Handler
                        Message msg = Message.obtain();

                        msg.what = GET_DATA_SUCCESS;
                        handler.sendMessage(msg);
                        inputStream.close();
                    }else {
                        //服务启发生错误
                        handler.sendEmptyMessage(SERVER_ERROR);
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                    //网络连接错误
                    handler.sendEmptyMessage(NETWORK_ERROR);
                }
            }
        }.start();
    }

}

