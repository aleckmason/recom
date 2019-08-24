package com.szc.recommend;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewConfiguration;
import android.widget.Toast;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2018/8/30 0030.
 */

public class SystemUtils {


    public static final String DESTINATIONPATH = getSDCardPath() + "/moningcall/";



    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static float getScale(Context context) {
        return getScreenWidth(context) / 720.0f;
    }

    public static int getScaleSize(Context context, int value) {
        return (int)(getScreenWidth(context) / 720.0f * value);
    }
    public static float getDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();

        float density  = dm.density;		// 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        int densityDPI = dm.densityDpi;
        return density;
    }


    //判断是否是手机号码
    public static boolean isMobileNO(String mobiles) {
        if(TextUtils.isEmpty(mobiles)) {
            return false;
        }
        Pattern p = Pattern.compile("^^1[3|4|5|7|8|9][0-9]\\d{4,8}$");
       // Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
    //判断是否是手机号码
    public static boolean isYZm(String yzm) {
        if(TextUtils.isEmpty(yzm)) {
            return false;
        }

        Pattern p = Pattern.compile("^(6[0-9])");
        Matcher m = p.matcher(yzm);
        return m.matches();
    }
    public static boolean isPackageAvilible(Context context, String packageName ){

        final PackageManager packageManager = context.getPackageManager();

        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for ( int i = 0; i < pinfo.size(); i++ )
        {

            // 循环判断是否存在指定包名
            if(pinfo.get(i).packageName.equalsIgnoreCase(packageName)){
                return true;
            }

        }
        return false;
    }


    //是否只包含字母和数字
    public static boolean isLetterDigitOrChinese(String str) {
        if(TextUtils.isEmpty(str)) {
            return false;
        }

        String regex = "^[a-z0-9A-Z\u4e00-\u9fa5]+$";
        return str.matches(regex);
    }

    public static int getVersionCode(Context context) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            localVersion = packageInfo.versionCode;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    public static String getVersionName(Context context) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            localVersion = packageInfo.versionName;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return localVersion;
    }



    public static String getFatory() {
        String res = "";
        try {
            res = Build.MANUFACTURER;
        } catch (Exception e) {

        }
        return res;

    }

    public static int getAndroidVer() {
        int res = 0;
        try {
            res = Build.VERSION.SDK_INT;

        } catch (Exception e) {

        }
        return res;
    }

    public static String getAndroidVerStr() {
        return Build.VERSION.RELEASE;
    }

    public static String getPhoneType() {
        String res = "";
        try {
            res = Build.MODEL;
        } catch (Exception e) {

        }
        return res;

    }

//打开APK程序代码

    private static void openFile(Context context, File file) {
        // TODO Auto-generated method stub
        Log.i("", "openFile");
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void openBrowser(Context context, String url) {

        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            // 打印Log   ComponentName到底是什么

            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(context.getApplicationContext(), "请下载浏览器", Toast.LENGTH_SHORT).show();
        }
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap, int maxkb) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        int options = 100;
        while (output.toByteArray().length > maxkb && options != 10) {
            output.reset(); //清空output
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
            options -= 10;
        }
        return output.toByteArray();
    }

    public static String formatMoey(float f) {
        if (f <= 0.00001f) {
            return "0";
        }
        String money = String.format("%.2f", f);
        return money;
    }

    public static String formatMoey(String s) {
        Float f = Float.valueOf(s);
        if (f <= 0.00001f) {
            return "0";
        }
        String money = String.format("%.2f", f);
        return money;
    }

    public static Bitmap getBitmapFromAssets(Context context, String filename) {
        AssetManager asm = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = asm.open(filename);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Drawable d = Drawable.createFromStream(inputStream, null);
        Bitmap b = ((BitmapDrawable) d).getBitmap();
        return b;
    }




    public static Bitmap getBitMBitmapFromUrl(String urlpath) {
        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return map;
    }



    public static int dip2px(Context context, float dpValue){
        final float scale = context.getResources ().getDisplayMetrics ().density;
        return (int) (dpValue * scale + 0.5f);
    }





    public static String getMetaData(Context context, String keyName) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            String value = appInfo.metaData.getString(keyName);
            if(value == null) {
                return "";
            }
            return value;
        } catch (Exception e) {

        }
        return "";
    }

    public static int getStatusBarHeight(Context context) {


        int result = 0;
        try {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }

        } catch (Exception e) {

        }
        if(result == 0) {
            return (int)(SystemUtils.getScale(context) * 20);
        }
        return result;
    }

    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

    public static void goToNotification(Activity context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }





    public static void shareByEmailText(Context context, String userMail) {
        try {
            Uri uri = Uri.parse("mailto:");
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, "");
            intent.putExtra(Intent.EXTRA_TEXT, "");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {userMail});
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getAndroidId (Context context) {
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        return ANDROID_ID;
    }


    public static boolean doProperty(float num) {
        if(Math.random() * 100 < num) {
            return true;
        }
        return false;
    }


}


