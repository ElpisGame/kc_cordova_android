package com.riceball.gpknives;
 
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
 
import androidx.annotation.NonNull;
 
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
 
import com.riceball.gpknives.ads.AdsBanner;
import com.riceball.gpknives.ads.AdsInterstitial;
import com.riceball.gpknives.ads.AdsRewarded;
import com.riceball.gpknives.ads.AdsOpenApp;


 
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_G;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE;

 
public class AdManage {
    private static final String AD_BANNER_UNIT_ID = "fd72732c23079a9a";//横幅广告ID
    private static final String AD_UNIT_ID_1 = "da46bfed443a1a0d"; //插页广告1       
    private static final String AD_UNIT_ID_2 = "3c96ef3647cf90a2"; //插页广告2      
    private static final String AD_VIDEO_ID = "5dfaca028aee81cf";//激励视频广告ID  
 
 
    public Context mainActive = null;
    private static AdManage mInstace = null;
 
    private MaxAdView adView;
    private LinearLayout bannerLayout;
    private String adPos;
 
    public final String TAG = "ADManage";
    private int screenWidth;

    private AdsBanner admobBanner = new AdsBanner();
    private AdsInterstitial admobInterstitial = new AdsInterstitial();
    private AdsRewarded admobRewarded = new AdsRewarded();
    private AdsOpenApp adsOpenApp = new AdsOpenApp();
    //-----------类型常量-------------
    private static String BANNER_TYPE = "0";
    private static String INTERSTITIAL_TYPE_1 = "10";
    private static String INTERSTITIAL_TYPE_2 = "11";
    private static String NATIVE_TYPE = "2";
    private static String SPLASH_TYPE = "5";
    private static String REWARDED_TYPE = "8";
    //----------------------
    private static boolean isVideoRewarded = false;
    private static boolean isVideoClose = false;
    
    //vip订阅无开屏广告
    public static boolean isVipNoOpenAd = false;

    public static AdManage getInstance() {
        if (null == mInstace) {
            mInstace = new AdManage();
        }
        return mInstace;
    }
 
    public void init(Context context, int wid) {
        this.mainActive = context;
        this.screenWidth = wid;
 
        //初始化广告 SDK.
        // Make sure to set the mediation provider value to "max" to ensure proper functionality
        AppLovinSdk.getInstance( context ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( context, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                // AppLovin SDK is initialized, start loading ads
                Log.d(AdManage.getInstance().TAG, "onInitializationComplete: ");

                //开屏广告初始化并预加载
                adsOpenApp.init(context);

                //初始化
                admobBanner.init(context,wid);
                admobInterstitial.init(context);
                admobRewarded.init(context);

                // loadBannerAd();
                // loadInterstitalAd();
                // initVideoAd();
                //开启广告测试界面
                // AppLovinSdk.getInstance( context ).showMediationDebugger();
        
                // this.GetGAID(this.mainActive);
            }
        } );

        // admobBanner.init(this.mainActive,this.screenWidth);
        // admobInterstitial.init(this.mainActive);
        // admobRewarded.init(this.mainActive);
            // loadBannerAd();
            // loadInterstitalAd();
            // initVideoAd();
        
            // //开启广告测试界面
            // AppLovinSdk.getInstance( this.mainActive ).showMediationDebugger();
        
            // this.GetGAID(this.mainActive);
    }
 
    public String getUnitID(String type){
        if(type.equals(BANNER_TYPE)){
            return AD_BANNER_UNIT_ID;
        }else if(type.equals(REWARDED_TYPE)){
            return AD_VIDEO_ID;
        }else if(type.equals(INTERSTITIAL_TYPE_1)){
            return AD_UNIT_ID_1;
        }else if(type.equals(INTERSTITIAL_TYPE_2)){
            return AD_UNIT_ID_2;
        }else{
            return "";
        }
    }
 
    public void GetGAID(Context context)
    {
        String GAID="";
        Context mActivity = context;
        new Thread(new Runnable() {
            public void run() {
                try {
                    AdvertisingIdClient.Info adInfo = AdvertisingIdClient
                            .getAdvertisingIdInfo(mActivity);
                    String advertisingId = adInfo.getId();
                    Log.d(AdManage.getInstance().TAG, "advertisingId: "+advertisingId);
                    // Log.i("ABC", "advertisingId" + advertisingId);
                    // Log.i("ABC", "optOutEnabled" + optOutEnabled);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //自定义事件上报
    public static void FAEvent(String eventName){
        FireBaseAnalyticsManager.getInstance().FAEvent(eventName);
    }
 
    // public void FAEventWithParam(String eventName , String[] key , String[] value){
    //     FireBaseAnalyticsManager.getInstance().FAEventWithParFAEventam(eventName,key,value);
    // }
    //自定义事件上报1
    public static void FAEventWithParam(String eventName , String key , String value){
        FireBaseAnalyticsManager.getInstance().FAEventWithParFAEventam(eventName,key,value);
    }

    //自定义事件上报2
    public static void FAEventWithParamsThr(String eventName , String key1 , String value1,String key2, String value2){
        FireBaseAnalyticsManager.getInstance().FAEventWithParamsThr(eventName,key1,value1,key2,value2);
    }
    //按用户属性类型上报
    public static void FAUserProperty(String eventName,String value){
        FireBaseAnalyticsManager.getInstance().FAUserProperty(eventName,value);
    }

    //AD_IMPRESSION事件上报
    public static void onAdRevenuePaid(MaxAd ad){
        FireBaseAnalyticsManager.getInstance().onAdRevenuePaid(ad);
    }

    //自定义事件-Total_Ads_Revenue_001-同步时间
    public static void FATotalRevenueSwitch(String value , String dayTotal){
        Log.d(AdManage.getInstance().TAG, "FATotalRevenueSwitch: "+value + " " +dayTotal );
        //初始化时启动
        if(value!=null && dayTotal!=null){
            // FireBaseAnalyticsManager.getInstance().canReport = value == "1";
            dayTotal = dayTotal.replace(",",".");
            double _d  = Double.parseDouble(dayTotal);
            FireBaseAnalyticsManager.getInstance().dayTotalRevenue = _d;
        }
    }

    /*
   加载 banner广告
 */
    public static void loadBannerAd() {
        AdManage.getInstance().admobBanner.loadBannerAd();
    }
 
    /*
       显示banner广告
     */
    public static void showBannerAd(String pos){
        AdManage.getInstance().admobBanner.showBannerAd(pos);
    }
    
    /*
   隐藏 banner广告
    */
    public static void hideBannerAd(){
        AdManage.getInstance().admobBanner.hideBannerAd();
    }
 
    public static void loadInterstitalAd(){
        AdManage.getInstance().admobInterstitial.loadInterstitalAd();
    }
 
    public static void showInterstitialAd() {
        AdManage.getInstance().admobInterstitial.showInterstitialAd();
    }
 
    public static void initVideoAd(){
        AdManage.getInstance().admobRewarded.initVideoAd();
    }
 
    //展示广告
    public static void showRewardVideoAd(String positionId){
        AdManage.getInstance().admobRewarded.showRewardVideoAd(positionId);
    }
 
    //展示开屏广告
    public static void showOpenAppAd( ){
        AdManage.getInstance().adsOpenApp.showAdIfReady();
    }

    public static void loadOpenAppAd( ){
        // AdManage.getInstance().adsOpenApp.load();
    }

    public static void hideOpenAppAd( ){
        AdManage.getInstance().adsOpenApp.hide();
    }

    public static void openMaxAdDebug( ){
        //开启广告测试界面
        AppLovinSdk.getInstance( AdManage.getInstance().mainActive ).showCreativeDebugger();
    }

    //用于cocos监听视频广告播放完成
    public static boolean videoRewardedListener(){
        return AdManage.getInstance().isVideoRewarded;
    }
 
    //用于cocos监听视频广告播放关闭
    public static boolean videoCloseListener(){
        return AdManage.getInstance().isVideoClose;
    }
 
 
    public boolean networkConnect() {
        return true;
    }
 
}