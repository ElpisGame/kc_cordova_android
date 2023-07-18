package com.riceball.gpknives.ads;
 
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxAppOpenAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkUtils;
import com.riceball.gpknives.R;
// import com.riceball.gpknives.R;
 
import com.riceball.gpknives.AdManage;
import com.riceball.gpknives.MainActivity;


public class AdsOpenApp implements LifecycleObserver{
 
   public final String TAG = "AdsOpenApp";

   private MaxAppOpenAd appOpenAd = null;
   private Context context = null;
   private boolean _isLoaded = false; //冷启动时
   private boolean _isReported = false; //冷启动时,避免重复上报
   private long curTime = 0;
   private int  count = 0;
//    public AdsOpenApp(final Context context)
//    {
//        ProcessLifecycleOwner.get().getLifecycle().addObserver( this );

//        this.context = context;

//        appOpenAd = new MaxAppOpenAd( "YOUR_AD_UNIT_ID", context);
//        appOpenAd.setListener( this );
//        appOpenAd.loadAd();
//    }

    public void init(Context context){
        ProcessLifecycleOwner.get().getLifecycle().addObserver( this );
        this.context = context;

        appOpenAd = new MaxAppOpenAd( "710d5722c7ff0c9e", context);
        // appOpenAd.setListener( this );

        appOpenAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(final MaxAd ad) {
                    Log.d(TAG, "openad onAdLoaded  " + _isLoaded);
                    if(!_isLoaded){
                        //后加载其他广告类型
                        AdManage.loadBannerAd();
                        AdManage.initVideoAd();
                        AdManage.loadInterstitalAd();
                        _isLoaded = true;
                    }

            }
            @Override
            public void onAdLoadFailed(final String adUnitId, final MaxError error) {
                    Log.d(TAG, "openad onAdLoadFailed : "+adUnitId);
                    if(!_isLoaded){
                        //后加载其他广告类型
                        AdManage.loadBannerAd();
                        AdManage.initVideoAd();
                        AdManage.loadInterstitalAd();
                        _isLoaded = true;
                    }
                jsCallback("oepnAdCallBack_false");
            }
            @Override
            public void onAdDisplayed(final MaxAd ad) {
                    Log.d(TAG, "openad onAdDisplayed");
                    jsCallback("oepnAdCallBack_true");
                    appOpenAd.loadAd();
                    AdManage.onAdRevenuePaid(ad);
            }
            @Override
            public void onAdClicked(final MaxAd ad) {}

            @Override
            public void onAdHidden(final MaxAd ad)
            {
                Log.d(TAG, "openad onAdHidden : ");
                appOpenAd.loadAd();
                jsCallback("oepnAdCallBack_true");
            }

            @Override
            public void onAdDisplayFailed(final MaxAd ad, final MaxError error)
            {
                    Log.d(TAG, "openad onAdDisplayFailed : ");
                appOpenAd.loadAd();
                //    jsCallback("oepnAdCallBack(false);");
            }

        });

        appOpenAd.loadAd();
    }

    // public void load(){
    //     if(appOpenAd==null){
    //         Log.d(TAG, "appOpenAd load");
    //         appOpenAd = new MaxAppOpenAd( "710d5722c7ff0c9e", context);
    //         appOpenAd.setListener( this );
    //     }
    //     appOpenAd.loadAd();
    // }

    public void hide(){
        if(appOpenAd!=null && _isLoaded){
            Log.d(TAG, "appOpenAd hide");
            appOpenAd.destroy();
            appOpenAd = null;
        }
    }

   public void showAdIfReady()
   {
        if(curTime==0 && count==0){
            //打开app应该展示开屏
            if(!_isReported){
                _isReported = true;
                AdManage.FAEvent("open_start_show");
            }
        }
        else{
            //后台切换应当展示开屏相当于should show
            AdManage.FAEvent("open_back_show");
        }
       if ( appOpenAd == null ){
            Log.d(TAG, "showAdIfReady appOpenAd == null");
           return;
       }

       if(!AppLovinSdk.getInstance( context ).isInitialized())
       {
            Log.d(TAG, "showAdIfReady !AppLovinSdk.getInstance( context ).isInitialized() ");
           return;
       }

       if ( appOpenAd.isReady() )
       {
            Log.d(TAG, "showAdIfReady openad show success ");
            appOpenAd.showAd();

            if(curTime==0 && count==0){
                //打开app应该展示开屏
                AdManage.FAEvent("open_start_show_success");
            }
            else{
                //后台切换应当展示开屏相当于should show
                AdManage.FAEvent("open_back_show_success");
            }
       }
       else
       {
            Log.d(TAG, "showAdIfReady openad show failed  isReady() not ");
            jsCallback("oepnAdCallBack_false");
            appOpenAd.loadAd();
       }
   }

   @OnLifecycleEvent(Lifecycle.Event.ON_START)
   public void onStart()
   {    
       if(AdManage.getInstance().isVipNoOpenAd){
            Log.d(TAG, "onStart isVipNoOpenAd");
       }
       else{
            long deltaT = System.currentTimeMillis()/1000 - curTime;
            Log.d(TAG, "onStart " + deltaT + " " + count);
            if(curTime > 0){
                AdManage.FAEventWithParam("app_start","app_start_type","soft_launch");
                if(deltaT > 10){
                    //10s以上展示
                    count++;
                    if(count % 2 == 0){ //每2次可以展示
                        showAdIfReady();
                    }
                }
            }
       }
   }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onBack()
    {
        Log.d(TAG, "onBack");
        curTime = System.currentTimeMillis()/1000;
    }

//    @Override
//    public void onAdLoaded(final MaxAd ad) {
//         Log.d(TAG, "openad onAdLoaded  " + _isLoaded);
//         if(!_isLoaded){
//             //后加载其他广告类型
//             AdManage.loadBannerAd();
//             AdManage.initVideoAd();
//             AdManage.loadInterstitalAd();
//             _isLoaded = true;
//         }

//    }
//    @Override
//    public void onAdLoadFailed(final String adUnitId, final MaxError error) {
//         Log.d(TAG, "openad onAdLoadFailed : "+adUnitId);
//         if(!_isLoaded){
//             //后加载其他广告类型
//             AdManage.loadBannerAd();
//             AdManage.initVideoAd();
//             AdManage.loadInterstitalAd();
//             _isLoaded = true;
//         }
//        jsCallback("oepnAdCallBack(false);");
//    }
//    @Override
//    public void onAdDisplayed(final MaxAd ad) {
//         Log.d(TAG, "openad onAdDisplayed");
//         jsCallback("oepnAdCallBack(true);");
//         AdManage.onAdRevenuePaid(ad);
//    }
//    @Override
//    public void onAdClicked(final MaxAd ad) {}

//    @Override
//    public void onAdHidden(final MaxAd ad)
//    {
//         Log.d(TAG, "openad onAdHidden : ");
//        appOpenAd.loadAd();
//        jsCallback("oepnAdCallBack(true);");
//    }

//    @Override
//    public void onAdDisplayFailed(final MaxAd ad, final MaxError error)
//    {
//         Log.d(TAG, "openad onAdDisplayFailed : ");
//        appOpenAd.loadAd();
//     //    jsCallback("oepnAdCallBack(false);");
//    }

    public void jsCallback(final String jsCodeStr) {
        Log.d("adverManager", "jsCallback code:" + jsCodeStr);
        MainActivity.getInstance().handleJsCallabackMessage(jsCodeStr);
    }
}