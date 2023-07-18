package com.riceball.gpknives.ads;
 
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
 
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;
import com.riceball.gpknives.R;
// import com.riceball.gpknives.R;
 
import com.riceball.gpknives.AdManage;
import com.riceball.gpknives.MainActivity;
 
public class AdsBanner {
 
    private Context mainActive = null;
    private ViewGroup rootView;
    private MaxAdView mAdView;
    private String adPos;
    private int screenWidth;
    private static boolean loaded = false;

    //-----------类型常量-------------
    private static String BANNER_TYPE = "0";
    //获得广告位id
    private static String AD_Banner_ID;
 
    public void init(Context context,int wid){
        this.mainActive = context;
        this.screenWidth = wid;
        AD_Banner_ID = AdManage.getInstance().getUnitID(BANNER_TYPE);
    }
 
    /*
       加载banner广告
     */
    public void loadBannerAd() {
        //请求banner
        if(this.mainActive==null || AdsBanner.loaded){
            Log.d("AdsBanner", "no init");
            return;
        }
        mAdView = new MaxAdView(AD_Banner_ID,this.mainActive );
 
        //自定义
        // int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        // int heightPx = this.mainActive.getResources().getDimensionPixelSize(R.dimen.banner_height2 );
        
        
        //------------自适应横幅-----------//
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heightDp = MaxAdFormat.BANNER.getAdaptiveSize( (Activity) this.mainActive ).getHeight();
        int heightPx = AppLovinSdkUtils.dpToPx( this.mainActive, heightDp );
        mAdView.setExtraParameter( "adaptive_banner", "true" );
        //--------------------------------//
        
        Log.d(AdManage.getInstance().TAG, "screenWidth"+this.screenWidth + " " + heightDp + " " + heightPx);
 
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams( width, heightPx ) ;
        params.gravity=Gravity.BOTTOM;


        mAdView.setLayoutParams(params);
        //Set background or background color for banners to be fully functional
        //adView.setBackgroundColor( ... );
 
        rootView= (ViewGroup)((Activity) this.mainActive).findViewById(android.R.id.content);
        rootView.addView( mAdView );
        mAdView.setVisibility(View.INVISIBLE);
 
        // Load the ad
        mAdView.loadAd();
        //banner监听
        mAdView.setListener(new MaxAdViewAdListener() {
            // MAX Ad Listener
            @Override
            public void onAdLoaded(final MaxAd maxAd)
            {
                Log.d(AdManage.getInstance().TAG, "onAdLoaded: (banner)");
                AdsBanner.loaded = true;
                // AdManage.onAdRevenuePaid(ad);
            }
 
            @Override
            public void onAdLoadFailed(final String adUnitId, final MaxError error)
            {
                Log.d(AdManage.getInstance().TAG, "onAdFailedToLoad: (banner)"+error);
                String errorMessage = error.getMessage();
                //加载错误事件
                int errorCode = error.getCode();
                AdsBanner.loaded = false;
                //-----
                //广告填充失败
            }
 
            @Override
            public void onAdDisplayFailed(final MaxAd maxAd, final MaxError error) {
                Log.d(AdManage.getInstance().TAG, "onAdDisplayFailed");
            }
 
            @Override
            public void onAdClicked(final MaxAd maxAd)
            {
                //广告点击
            }
 
            @Override
            public void onAdExpanded(final MaxAd maxAd) {
                Log.d("AdsBanner", "onAdExpanded");
            }
 
            @Override
            public void onAdCollapsed(final MaxAd maxAd) {
                Log.d("AdsBanner", "onAdCollapsed");
            }
 
            @Override
            public void onAdDisplayed(final MaxAd maxAd) { /* DO NOT USE - THIS IS RESERVED FOR FULLSCREEN ADS ONLY AND WILL BE REMOVED IN A FUTURE SDK RELEASE */ }
 
            @Override
            public void onAdHidden(final MaxAd maxAd) { /* DO NOT USE - THIS IS RESERVED FOR FULLSCREEN ADS ONLY AND WILL BE REMOVED IN A FUTURE SDK RELEASE */ }
        });
    }
 
    /*
       banner广告
     */
    public void showBannerAd(String pos){
        if(this.mainActive==null || mAdView==null){
            return;
        }
        MainActivity mActivity = (MainActivity)this.mainActive;
        this.adPos = pos;
        int mWidth = this.screenWidth;
        Log.d(AdManage.getInstance().TAG, "showBannerAd: adPos " + mWidth + "==" +adPos);
        //一定要确保在UI线程操作
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(AdManage.getInstance().TAG, "showBannerAd runOnUiThread : adPos " + mWidth + "==" +adPos);
                FrameLayout.LayoutParams  params=(FrameLayout.LayoutParams )mAdView.getLayoutParams();
                if(adPos.equals("top")){
                    params.gravity=Gravity.TOP;
                    params.leftMargin = 0;
                }else if(adPos.equals("top-left")){
                    params.gravity=Gravity.TOP;
                    params.leftMargin = -mWidth /2;
                }else if(adPos.equals("bottom")){
                     params.gravity=Gravity.BOTTOM;
//                     params.leftMargin = 0;
//                     params.bottomMargin = 0;
                    // params.setMargins(0,0,900,0);
                }else if(adPos.equals("bottom-left")){
                    params.gravity=Gravity.BOTTOM;
                    params.leftMargin = -mWidth /2;
                }
                mAdView.setLayoutParams(params);
                // Log.d(AdManage.getInstance().TAG, "banner params " + params.leftMargin + "==" +params.topMargin + "==" +params.bottomMargin);

                mAdView.setVisibility(View.VISIBLE);
                if(AdsBanner.loaded){
                    //banner展示成功
                    AdManage.FAEvent("banner_show_success");
                }
            }
        });
    }
 
    /*
       隐藏 banner广告
     */
    public void hideBannerAd(){
        if(this.mainActive==null || mAdView==null){
            return;
        }
        //有用接口，先注释，取消隐藏banner
        MainActivity mActivity = (MainActivity)this.mainActive;
        //一定要确保在UI线程操作
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(AdManage.getInstance().TAG, "hideBannerAd");
                mAdView.setVisibility(View.INVISIBLE);
            }
        });
    }
}