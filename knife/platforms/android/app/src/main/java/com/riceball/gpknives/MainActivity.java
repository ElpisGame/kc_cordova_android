/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.riceball.gpknives;

import android.os.Bundle;

import org.apache.cordova.*;

import org.apache.cordova.CallbackContext;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.ComponentName;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import android.os.Build;

import android.app.Service;
import android.net.Uri;
import android.os.Bundle;
 
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.riceball.gpknives.BuildConfig;
import com.riceball.gpknives.R;
import com.vungle.warren.utility.platform.WebViewUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.cordova.PluginResult;

import com.riceball.gpknives.activity.IABManager;
import com.riceball.gpknives.bitverse.MyApplication;


public class MainActivity extends CordovaActivity
{
    private final String TAG 	= "MainActivity";
    public static String BRAND;
    private  static MainActivity instance = null;
	 /**
     * 手机品牌
     */
    // 小米
    public static final String PHONE_XIAOMI = "Xiaomi";
    // 华为
    public static final String PHONE_HUAWEI1 = "Huawei";
    // 华为
    public static final String PHONE_HUAWEI2 = "HUAWEI";
    // 华为
    public static final String PHONE_HUAWEI3 = "HONOR";
    // 魅族
    public static final String PHONE_MEIZU = "Meizu";
    // 索尼
    public static final String PHONE_SONY = "sony";
    // 三星
    public static final String PHONE_SAMSUNG = "samsung";
    // LG
    public static final String PHONE_LG = "lg";
    // HTC
    public static final String PHONE_HTC = "htc";
    // NOVA
    public static final String PHONE_NOVA = "nova";
    // OPPO
    public static final String PHONE_OPPO = "OPPO";
    // 乐视
    public static final String PHONE_LeMobile = "LeMobile";
    // 联想
    public static final String PHONE_LENOVO = "lenovo";
    // private ConnectivityManager mConnectivityManager = null;

	private CallbackContext mlistenerContext;

    private CallbackContext mCallbackContext;

    private Map<String,CallbackContext> mlistenerContexts = new HashMap<>();

    //---------COCOS 项目------------------//
    private boolean mBackKeyPressed = false;//记录是否有首次按键
    private static final String   appKey = "95586faa6eac";
    private int screenWidth;
    private static MainActivity app = null;

    private static Vibrator s_vibrator = null;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        MainActivity.instance = this;
		BRAND = android.os.Build.BRAND;
		Log.e(TAG, "---PHONE_BRAND---"+ BRAND);
		// String carrier= android.os.Build.MANUFACTURER;
        // Log.e(TAG, "---PHONE_BRAND---"+ carrier);

        //-----MIUI隐藏状态栏和虚拟按键-------
		if(BRAND == PHONE_XIAOMI || BRAND=="xiaomi")
		{
			View decorView = getWindow().getDecorView();
			int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN //隐藏状态栏
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏虚拟键
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(visibility);

			//在大于等于android P的版本上，需要设置flag：LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES，保证应用可以显示都notch区域。
			//如果设置了该flag，在notch屏手机上，要注意应用内部控件的位置，应该和屏幕边缘保持一定的距离，防止遮挡。

			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
					getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
			} finally {
				Log.e(TAG, "Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
			}
		}
        //-------------------


        //-----------------------------cocos项目BEGIN---------------------------------------------
        //屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AppsFlyerConversionListener conversionListener =  new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionDataMap) {
                for (String attrName : conversionDataMap.keySet())
                    Log.d("AppsFlyer", "Conversion attribute: " + attrName + " = " + conversionDataMap.get(attrName));
                String status = Objects.requireNonNull(conversionDataMap.get("af_status")).toString();
                if(status.equals("Non-organic")){
                    if( Objects.requireNonNull(conversionDataMap.get("is_first_launch")).toString().equals("true")){
                        Log.d("AppsFlyer","Conversion: First Launch");
                    } else {
                        Log.d("AppsFlyer","Conversion: Not First Launch");
                    }
                } else {
                    Log.d("AppsFlyer", "Conversion: This is an organic install.");
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Log.d("AppsFlyer", "error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                Log.d("AppsFlyer", "onAppOpenAttribution: This is fake call.");
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                Log.d("AppsFlyer", "error onAttributionFailure : " + errorMessage);
            }
        };
        AppsFlyerLib.getInstance().setDebugLog(true);
        AppsFlyerLib.getInstance().init("DdWbxT9VRELdEsZiAcnGea", conversionListener, this);
        //AppsFlyerLib.getInstance().start(this);
        AppsFlyerLib.getInstance().start(getApplicationContext(), "DdWbxT9VRELdEsZiAcnGea", new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
                Log.d("AppsFlyer", "Launch sent successfully, got 200 response code from server");
            }
            @Override
            public void onError(int i, @NonNull String s) {
                Log.d("AppsFlyer", "Launch failed to be sent:\n" +
                        "Error code: " + i + "\n"
                        + "Error description: " + s);
            }
        });


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MainActivity", "getDynamicLink:onFailure", e);
                    }
        });
        //-----------------------------cocos项目END---------------------------------------------
        //管理初始化
        initManager();

        // enable Cordova apps to be started in the background
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);
    }

    //-----------------------------cocos项目BEGIN---------------------------------------------
    public void initManager(){
        app = this;
        //Bitverse链接
        MyApplication.getInstance().init(this.getApplication());

        //初始化firebase
        FireBaseAnalyticsManager.getInstance().init(this);
        //初始化app跳转
        JumpToApp.getInstance().init(this);
        getSize();
        //初始化广告管理
        AdManage.getInstance().init(this,this.screenWidth);

        Utils.init(this);

        VibratorWrapper.getInstance().init(this);

        IABManager.getInstance().init(this);

        BitverseManager.getInstance().init(this);

        //获取内存信息
        String ramStr = Utils.getMaxMemoryInfo();

        AdManage.FAUserProperty("ram",ramStr);
        
        //获取网络状态
        String netstat = Utils.getNetstat();

        AdManage.FAUserProperty("network_type",netstat);

        // AdManage.FAUserProperty("ab_test", BuildConfig.ab_test);

        Log.d(AdManage.getInstance().TAG, "netstat =>>>>" + netstat);

        AdManage.FAEventWithParam("app_start","app_start_type","cold_start");

        // s_vibrator = (Vibrator) this.getSystemService("vibrator");
    }


    public void getSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
 
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;
        float density = outMetrics.density;
 
        int adWidth = (int) (widthPixels / density)/2;
 
        this.screenWidth = widthPixels;
 
        Log.d(AdManage.getInstance().TAG, "widthPixels: "+widthPixels);
        Log.d(AdManage.getInstance().TAG, "heightPixels: "+heightPixels);
        Log.d(AdManage.getInstance().TAG, "density: "+density);
        Log.d(AdManage.getInstance().TAG, "adWidth: "+adWidth);
 
    }

    //跳谷歌商城下的游戏页面=============================
//    public static void getAppByGooglePlay(String appPkg){
//        JumpToApp.getInstance().getAppByGooglePlay(appPkg);
//    }
 
    // public  static  void vibrate(int time){
    //    // Log.d("vibrate", "shakeeeee: ");
    //     Vibrator vib=(Vibrator)app.getSystemService(Service.VIBRATOR_SERVICE);
    //     vib.vibrate(time);
    // }

    public static void vibrate(Integer time) {
        if (s_vibrator != null ) s_vibrator.vibrate(time);
//        Log.v("jswrapper", "vibrate time:" + time + " s_vibrator:" + (s_vibrator != null));
    }

    public static void vibrateLong() {
        vibrate(400);
    }

    public static void vibrateShort() {
        vibrate(30);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                if (!mBackKeyPressed) {
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    mBackKeyPressed = true;
                    new Timer().schedule(new TimerTask() {//延时两秒，如果超出则擦错第一次按键记录
                        @Override
                        public void run() {
                            mBackKeyPressed = false;
                        }
                    }, 2000);
                } else {//退出程序
                    this.finish();
                    System.exit(0);
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    //-----------------------------cocos项目END---------------------------------------------
    @Override
	public void onActivityResult (int requestCode, int resultCode, Intent data) 
    {
		// TODO Auto-generated method stub
		super.onActivityResult (requestCode, resultCode, data);
	}


	@Override
	public void onDestroy () 
	{
		// TODO Auto-generated method stub
		super.onDestroy ();
	}

	@Override
	public void onNewIntent (Intent intent) 
	{
		// TODO Auto-generated method stub
		super.onNewIntent (intent);
	}

	@Override
	public void onPause () 
	{
		// TODO Auto-generated method stub
		super.onPause();
	}


	@Override
	public void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}



	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		Log.e(TAG, "onBackPressed ==>"); 
	}

    public boolean coolMethod(JSONArray args, CallbackContext callbackContext) throws JSONException {
        String message = args.getString(0);
        Log.d(TAG, "MainActivity.coolMethod==>" + message);
        if (message != null && message.length() > 0) {
            Log.d(TAG, "判断方法---1111");
            if (message.equals("bindListener")) {
                mlistenerContext = callbackContext;
            } else if (message.equals("openMaxAdDebug")) {
                AdManage.openMaxAdDebug();
            } else if (message.equals("openUrl")) {
                String url = args.getString(1);
                BitverseManager.getInstance().openUrl(url);
            } else if (message.equals("FAUserProperty")) {
                String eventName = args.getString(1);
                String value = args.getString(2);
                AdManage.FAUserProperty(eventName,value);
            } else if (message.equals("FAEvent")) {
                String eventName = args.getString(1);
                AdManage.FAEvent(eventName);
            }
            else if (message.equals("FAEventWithParam")) {
                String eventName = args.getString(1);
                String key = args.getString(2);
                String value = args.getString(3);
                AdManage.FAEventWithParam(eventName,key,value);
            }
            else if (message.equals("FAEventWithParamsThr")) {
                String eventName = args.getString(1);
                String key1 = args.getString(2);
                String value1 = args.getString(3);
                String key2 = args.getString(4);
                String value2 = args.getString(5);
                AdManage.FAEventWithParamsThr(eventName,key1,value1,key2,value2);
            }
            else if (message.equals("FATotalRevenueSwitch")) {
                String value = args.getString(1);
                String dayTotal = args.getString(2);
                AdManage.FATotalRevenueSwitch(value,dayTotal);
            }
            else if (message.equals("showRewardVideoAd")) {
                String adUnitId = args.getString(1);
                AdManage.showRewardVideoAd(adUnitId);
            }
            else if (message.equals("showBannerAd")) {
                String pos = args.getString(1);
                AdManage.showBannerAd(pos);
            }
            else if (message.equals("hideBannerAd")) {
                AdManage.hideBannerAd();
            }
            else if (message.equals("showInterstitialAd")) {
                AdManage.showInterstitialAd();
            }
            else if (message.equals("showOpenAppAd")) {
                AdManage.showOpenAppAd();
            }
            else if (message.equals("hideOpenAppAd")) {
                AdManage.hideOpenAppAd();
            }
            else if (message.equals("loadOpenAppAd")) {
                AdManage.loadOpenAppAd();
            }
            else if (message.equals("requestProducts")) {
                IABManager.requestProducts();
            }
            else if (message.equals("requestSubscribe")) {
                IABManager.requestSubscribe();
            }
            else if (message.equals("buyProductByPayIndex")) {
                try {
                    int payIndex = args.getInt(1);
                    IABManager.buyProductByPayIndex(payIndex);
                } 
                catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (message.equals("getUUID")) {
                String uuid =  Utils.getUUID();
                Log.d(TAG, "getUUID--->"+ uuid);
		        // callbackContext.success(uuid);
                callbackContext.success(uuid); 
            }
            else if (message.equals("getCountry")) {
                String country = Utils.getCountry();
                Log.d(TAG, "getCountry--->"+ country);
		        // callbackContext.success(country);
                callbackContext.success(country); 
            }
            else if (message.equals("getLanguageCode")) {
                String code = Utils.getLanguageCode();
                Log.d(TAG, "getLanguageCode--->"+ code);
		        // callbackContext.success(code);
                callbackContext.success(code); 
            }
            else if (message.equals("openStoreComment")) {
                Utils.openStoreComment();
            }
            else if (message.equals("isInstalled")) {
                BitverseManager.isInstalled();
            }
            else if (message.equals("ConnectWallet")) {
                BitverseManager.Connect();
            }
            else if (message.equals("vibrateLong")) {
                VibratorWrapper.vibrateLong();
            }
            else if (message.equals("vibrateShort")) {
                VibratorWrapper.vibrateShort();
            }
            else if(message.equals("jumpToGPL")){
                Log.d(TAG, "jumpToGPL--->");
                // JumpToApp.getInstance().getAppByGooglePlay("com.riceball.gpknives");
                JumpToApp.getInstance().launchAppDetail(this,"com.riceball.gpknives","com.android.vending");
            }
            else if (message.equals("sdkSyncRoleInfo")) {
                // sync_cb = callbackContext;
            }
            else if (message.equals("bindListenerOne")) {
                String funcName = args.getString(1);
                mlistenerContexts.put(funcName,callbackContext);
            }
            // callbackContext.success("");
            return true;
        } else {
            callbackContext.error("Expected one non-empty string argument.");
            return false;
        }
    }

    public void handleJsCallabackMessage(final String jsCodeStr){
        Log.d(TAG, "---handleJsCallabackMessage---" + jsCodeStr);
		// if(mlistenerContext !=null){
        //     Log.d(TAG, "---mlistenerContext---" + jsCodeStr);
		//     mlistenerContext.success(jsCodeStr);
        // }
        // else{
        //     Log.d(TAG, "---mlistenerContext=null");
        // }
        if(mlistenerContexts!=null && mlistenerContexts.size() > 0){
            String funcName = "";
            String params = "";
            if(jsCodeStr.contains("_")){
                funcName = jsCodeStr.split("_")[0];
                params = jsCodeStr.split("_")[1];
            }
            else{
                funcName = jsCodeStr;
            }
            if(mlistenerContexts.containsKey(funcName)){
                CallbackContext callbackContext = mlistenerContexts.get(funcName);
		        // callbackContext.success(params); //只能回调一次
                PluginResult result = new PluginResult(PluginResult.Status.OK, params);
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
            }
        }
    }

    public static MainActivity getInstance()
    {
        return instance;
    }
}
