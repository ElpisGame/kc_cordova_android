package com.riceball.gpknives.ads;
 
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
 
import androidx.annotation.NonNull;
 
 
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
 
import com.riceball.gpknives.AdManage;
import com.riceball.gpknives.MainActivity;

 
import java.util.concurrent.TimeUnit;
 
public class AdsInterstitial implements MaxAdListener {
 
    private static String INTERSTITIAL_TYPE_1 = "10"; //类型常量
    private static String INTERSTITIAL_TYPE_2 = "11"; //类型常量
    private  static String AD_INTERSTITIAL_ID;//正式插屏
    private  static String AD_INTERSTITIAL_ID_1;//正式插屏1
    private  static String AD_INTERSTITIAL_ID_2;//正式插屏2
 
 
    private  static MaxInterstitialAd interstitialAd;
 
    private  int retryAttempt;

    private int insUnitIdIdex = 0;
    private int insReLoadTime = 1; //插屏加载失败时，允许重新加载几次插屏
    private int curInsReloadTime = 0; //当前插屏重新加载次数
 
    private Context mainActive = null;
 
 
    public void init(Context context){
        this.mainActive = context;
        AD_INTERSTITIAL_ID_1 = AdManage.getInstance().getUnitID(INTERSTITIAL_TYPE_1);
        AD_INTERSTITIAL_ID_2 = AdManage.getInstance().getUnitID(INTERSTITIAL_TYPE_2);
    }
 
    public void loadInterstitalAd(){
        MainActivity mActivity = (MainActivity)this.mainActive;
        AdsInterstitial mUser=this;

        AdsInterstitial.AD_INTERSTITIAL_ID = insUnitIdIdex % 2 == 0 ? AdsInterstitial.AD_INTERSTITIAL_ID_1 : AdsInterstitial.AD_INTERSTITIAL_ID_2;
        insUnitIdIdex++;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(AdManage.getInstance().TAG, "loadInterstitalAd....: "+AdsInterstitial.AD_INTERSTITIAL_ID);
                //插屏广告请求
                
                MaxInterstitialAd interstitialAd = new MaxInterstitialAd( AdsInterstitial.AD_INTERSTITIAL_ID, mActivity );
                // Load the first ad
                AdsInterstitial.interstitialAd=interstitialAd;
                interstitialAd.setListener(mUser);
                interstitialAd.loadAd();
            }
        });
    }
    public void showInterstitialAd() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        Log.d(AdManage.getInstance().TAG, "showInterstitial: ");
        MainActivity mActivity = (MainActivity)this.mainActive;

        //场景应当出插屏（相当于出插屏的场景）
        AdManage.FAEvent("adi_should_show");

        //一定要确保在UI线程操作
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (AdsInterstitial.interstitialAd != null && interstitialAd.isReady()) {
                    //插屏广告成功展示
                    AdManage.FAEvent("adi_show_succsess");
                    interstitialAd.showAd();
                } else {
                    Toast.makeText((Activity) mActivity, "Ad did not load", Toast.LENGTH_SHORT).show();
                    // interstitialAd.loadAd();
                }
            }
        });
    }
 
    @Override
    public void onAdLoaded(MaxAd ad) {
        Log.i(AdManage.getInstance().TAG, "onAdLoaded(AdsInterstitial)");
        //加载插屏广告成功
        retryAttempt = 0;
    }
 
    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        String error1 =String.format("code: %d, message: %s",error.getCode(), error.getMessage());
        Log.d(AdManage.getInstance().TAG, "AdsInterstitial Failed : " + error1);
 
        String errorMessage = error.getMessage();
        //加载错误事件
        int errorCode = error.getCode();
        //广告填充失败
        //Toast.makeText((Activity) mActivity, "onAdFailedToLoad() with error: " + error1, Toast.LENGTH_SHORT).show();
 
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );
        delayMillis = 1000; //设置1秒后重新加载
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {   
                // interstitialAd.loadAd();
                interstitialAd.destroy();
                interstitialAd = null;
                loadInterstitalAd();
            }
        }, delayMillis );
    }
 
    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        Log.d(AdManage.getInstance().TAG, "onAdDisplayFailed");
        interstitialAd.loadAd();
    }
 
    @Override
    public void onAdDisplayed(MaxAd ad) {
        Log.d(AdManage.getInstance().TAG, "onAdDisplayed");
        AdManage.onAdRevenuePaid(ad);
    }
 
    @Override
    public void onAdClicked(MaxAd ad) {
        Log.d(AdManage.getInstance().TAG, "onAdClicked");
    }
 
    @Override
    public void onAdHidden(MaxAd ad) {
        Log.d(AdManage.getInstance().TAG, "onAdHidden");
        interstitialAd.loadAd();
    }
}