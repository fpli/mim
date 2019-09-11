package com.sap.mim.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import com.sap.mim.bean.Account;


public class MimApplication extends Application {

    private static MimApplication mInstance;
    public  static Context        mContext;

    private Account               mAccount;
    private Handler               messageHandler;
    private Handler               chatMessageHandler;
    private Handler               friendListHandler;
    /**
     * 屏幕宽度
     */
    public static int screenWidth;
    /**
     * 屏幕高度
     */
    public static int screenHeight;
    /**
     * 屏幕密度
     */
    public static float screenDensity;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext  = getApplicationContext();
        initScreenSize();
    }

    public static MimApplication getInstance() {
        return mInstance;
    }

    /**
     * 初始化当前设备屏幕宽高
     */
    private void initScreenSize() {
        DisplayMetrics curMetrics = getApplicationContext().getResources().getDisplayMetrics();
        screenWidth   = curMetrics.widthPixels;
        screenHeight  = curMetrics.heightPixels;
        screenDensity = curMetrics.density;
    }

    public Account getmAccount() {
        return mAccount;
    }

    public void setmAccount(Account mAccount) {
        this.mAccount = mAccount;
    }
}
