package com.github.rrj;

import android.app.Application;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.github.rrj.receiver.NetworkConnectChangedReceiver;

/**
 * Created by boby on 2017/2/27.
 */

public class MyApplication extends Application {

    private static MyApplication app;

    //    表示是否是移动网络
    private boolean isMobile;
    //    表示是否是WiFi
    private boolean isWifi;
    //    表示WiFi开关是否打开
    private boolean isEnableWifi;
    //    表示移动网络数据是否打开
    private boolean isEnableMobile;

    public static String welcomeRes = "";

    public static int tintColor = R.color.white;

    private NetworkConnectChangedReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;

        startNetReceiver();

        initStrategy();
    }

    private void startNetReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        mReceiver = new NetworkConnectChangedReceiver();

        registerReceiver(mReceiver, filter);

    }

    private void initStrategy() {
        ApplicationInfo info = null;
        try {
            info = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager
                    .GET_META_DATA);

            int tintColor = info.metaData.getInt("tint");
            String welcomePath = info.metaData.getString("welcome");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static MyApplication getInstance() {
        return app;
    }

    public boolean isConnected() {
        return isWifi || isMobile;
    }

    public boolean isMobile() {
        return isMobile;
    }

    public void setMobile(boolean mobile) {
        isMobile = mobile;
    }

    public boolean isWifi() {
        return isWifi;
    }

    public void setWifi(boolean wifi) {
        isWifi = wifi;
    }

    public boolean isEnableWifi() {
        return isEnableWifi;
    }

    public void setEnableWifi(boolean enableWifi) {
        isEnableWifi = enableWifi;
    }

    public boolean isEnableMobile() {
        return isEnableMobile;
    }

    public void setEnableMobile(boolean enableMobile) {
        isEnableMobile = enableMobile;
    }
}
