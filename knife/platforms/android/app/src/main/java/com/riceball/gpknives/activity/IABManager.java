package com.riceball.gpknives.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.nfc.Tag;
import android.util.Base64;
import android.util.Log;

import com.riceball.gpknives.MainActivity;
import com.riceball.gpknives.callback.PayCallback;
import com.riceball.gpknives.manager.PayManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Random;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.util.Hex;

import javax.security.auth.callback.Callback;

/**
 * 可以用一个单例来保存IABManager实例，在需要初始化sdk的时候调用
 */
public class IABManager  {

    private static IABManager mInstance = null;

    public Activity mainActive = null;
    private static final String TAG = "IABManager";
    static final int RC_REQUEST = 10001;
    static final String payload = "knife666";
    private List products = new ArrayList<String>();
    private String productPrices = "";
    private boolean isVipWithoutInterstitial = false;

    public static IABManager getInstance() {
        if (null == mInstance) {
            mInstance = new IABManager();
        }
        return mInstance;
    }

    public void init(Context context) {
        IABManager.getInstance().mainActive = (Activity) context;

        doInit(context);
    }

    private void doInit(Context context) {
        products.add("com.riceball.gpknives.diamond10");    
        products.add("com.riceball.gpknives.diamond50");
        products.add("com.riceball.gpknives.diamond100");
        products.add("com.riceball.gpknives.diamond250");
        products.add("com.riceball.gpknives.diamond500");
        products.add("com.riceball.gpknives.subweekly");
        // products.add("com.riceball.gpknives.subweekly:p7d");
        products.add("com.riceball.gpknives.nointerstitial");
        PayManager.getInstance().init(context);
//        String base64EncodedPublicKey ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjaSKBV1zUhhtI2pDQxfETKWcYNPF5G+FXAtFPYuAuc9AZIRn8+RycpiTuwM1znFdYPUB3Vy+6Mv1zPh2W4xSaL/2pU6VjdSjq53sf1Oklewp13AKxsNO4L9fRebIz1+UvM/r+TV3QW2dDehN5OdSU8Bg55+docwWbyzQG0gHUS3hRg4EqnAE9dMw7ccxZO4vr3HPQdmR2hLJM2lhygvP1rgDyhX5AAd9lICZEZjfsCEblWCwoxRRvVxI90iXSV0Q5eIG8heFo4Qz3TxH+wL8JJjuxYlMPSDRMKDIshhco057fquRcRBFvFEQUcuzzh4pzMd2Czil3rMlVJPgsSzWYQIDAQAB";
//        mHelper = new IabHelper(this.mainActive, base64EncodedPublicKey);
//
//        // enable debug logging (for a production application, you should set this to false).
//        mHelper.enableDebugLogging(true);
//
//        final IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
//            @Override
//            public void onConsumeFinished(Purchase purchase, IabResult result) {
//                Log.i(TAG, result.getMessage());
//
//            }
//        };
//
////        final IabHelper.QueryInventoryFinishedListener queryInventoryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
////            @Override
////            public void onQueryInventoryFinished(IabResult result, final Inventory inv) {
////                if (mHelper == null) return;
////
////                Purchase mPurchase = inv.getPurchase("com.riceball.gpknives.diamond100");
////                if (mPurchase != null) {
////                    try {
////                        mHelper.consumeAsync(inv.getPurchase("com.riceball.gpknives.diamond100"), mConsumeFinishedListener);//查询到之后调用消耗监听
////                    } catch (IabHelper.IabAsyncInProgressException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }
////        };
//
//        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
//            public void onIabSetupFinished(IabResult result) {
//                if (!result.isSuccess()) {
//                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
//                    jsCallback("PayMgr.errorPayCallBack();");
//                    return;
//                }
//                if (mHelper == null) return;
//                try {
////                    mHelper.queryInventoryAsync(queryInventoryFinishedListener);//查询有是否需要消耗的商品
//
//                    mHelper.queryInventoryAsync(true, products, null, new IabHelper.QueryInventoryFinishedListener() {
//                        @Override
//                        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
//                            if (result.isFailure()) {
//                                Log.d(TAG, "Problem setting up In-app Billing: onQueryInventoryFinished " + result);
//                                jsCallback("PayMgr.errorPayCallBack();");
//                                return;
//                            }
//                            StringBuffer priceRet = new StringBuffer();
//                            priceRet.append("PayMgr.queryProductsCallBack('price");
//
//                            saveInventory = inventory;
//
//                            //消耗品相关价格查询以及消耗
//                            for (int i = 0; i < products.size() - 1; i++) {
//                                String product = (String) products.get(i);
////                                String currencyCode = inventory.getSkuDetails(product).getPriceCurrencyCode();
//                                String price = inventory.getSkuDetails(product).getPrice();
////                            Log.d(TAG, currencyCode);//SGD
////                            Log.d(TAG, price);//$1.48
//                                priceRet.append("#");
////                                priceRet.append(currencyCode);
//                                priceRet.append(price);
//
//                                Purchase mPurchase = inventory.getPurchase(product);
//                                if (mPurchase != null) {
//                                    try {
//                                        mHelper.consumeAsync(inventory.getPurchase(product), mConsumeFinishedListener);//查询到之后调用消耗监听
//                                    } catch (IabHelper.IabAsyncInProgressException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                            priceRet.append("');");
////                            jsCallback(priceRet.toString());
//                            Log.d(TAG, priceRet.toString());
//                            IABManager.getInstance().productPrices = priceRet.toString();
//
//                            //非消耗品查询
//                            String noAdProduct = (String) products.get(products.size()-1);
//                            Purchase mPurchase = inventory.getPurchase(noAdProduct);
//                            if (mPurchase != null) {
////                                jsCallback("PayMgr.restoreCallBack(true);");
//                                IABManager.getInstance().isVipWithoutInterstitial = true;
//                            }
//                        }
//                    });
//                } catch (IabHelper.IabAsyncInProgressException e) {
//                    jsCallback("PayMgr.errorPayCallBack();");
//                    e.printStackTrace();
//                }
//            }
//        });

    }

    public static void jsCallback(final String jsCodeStr) {
        Log.d("IABManager", "jsCallback code:" + jsCodeStr);
        MainActivity.getInstance().handleJsCallabackMessage(jsCodeStr);
    }

    public static void requestProducts() {
        Log.d("IABManager", "requestProducts");
        if (IABManager.getInstance().isVipWithoutInterstitial) {
            jsCallback("restoreCallBack_true");
        }
        String str = PayManager.getInstance().getGPPrices();
        if(str=="prices"){
            return;
        }
        jsCallback(str);

        //测试，请求订阅商品配置
        // PayManager.getInstance().querySubscribeSku();

        //测试，请求订阅商品状态
        // PayManager.getInstance().querySubscribePurchase();

    }

    public static void requestSubscribe() {
        String jsStr = PayManager.getInstance().getSUBPurchase();
        if(jsStr=="purchase"){
            return;
        }
        jsCallback(jsStr);
    }


//    public static  void  AFPushEvent(final Purchase purchase,final boolean isSubscribe)
//    {
//        Verification(purchase, new VerificationCallback() {
//            @Override
//            public void call() {
//                SkuDetails skuDetail =  saveInventory.getSkuDetails(purchase.getSku());
//                float price = skuDetail.getPriceAmountMicros() / (float)1000000;
//                String currencyCode = skuDetail.getPriceCurrencyCode();
//                String contentType =  skuDetail.getType();
//
//
//                Log.i(TAG, "消费成功。Provisioning. Price " + price + " currency code " + currencyCode + " sku " + purchase.getSku());
//                currencyCode = "USD";
//                if ( isSubscribe )
//                {
//                    price = 2.99f;
//                }
//                else {
//                    switch (purchase.getSku()) {
//                        case "com.riceball.gpknives.diamond10":
//                            price = 1.99f;
//                            break;
//                        case "com.riceball.gpknives.diamond50":
//                            price = 7.99f;
//                            break;
//                        case "com.riceball.gpknives.diamond100":
//                            price = 14.99f;
//                            break;
//                        case "com.riceball.gpknives.diamond250":
//                            price = 29.99f;
//                            break;
//                        case "com.riceball.gpknives.diamond500":
//                            price = 54.99f;
//                            break;
//                        case "com.riceball.gpknives.nointerstitial":
//                            price = 2.99f;
//                            break;
//                    }
//                }
//                Map<String, Object> eventValue = new HashMap<String, Object>();
//                eventValue.put(AFInAppEventParameterName.REVENUE, price);
//                //eventValue.put(AFInAppEventParameterName.PRICE, price);
//                eventValue.put(AFInAppEventParameterName.CONTENT_ID, purchase.getSku());
//                // for multiple product categories, set the param value as: new String {"221", "124"}
//                eventValue.put(AFInAppEventParameterName.CONTENT_TYPE, contentType);
//                // for multiple product categories,, set the param value as: new String {"shirt", "pants"}
//                eventValue.put(AFInAppEventParameterName.CURRENCY, currencyCode);
//                eventValue.put(AFInAppEventParameterName.QUANTITY, 2);
//                //eventValue.put("RiceBallGames","Server");
//                Context context = IABManager.getInstance().mainActive;
//
//                if ( isSubscribe )
//                {
//                    AppsFlyerLib.getInstance().trackEvent(context, "af_iap_usd", eventValue);
//                }
//                else {
//                    AppsFlyerLib.getInstance().trackEvent(context, "af_iap_usd", eventValue);
//                }
//            }
//        });
//    }

    public static void buyProductByPayIndex(int idx) {
        // launch the gas purchase UI flow.
        // We will be notified of completion via mPurchaseFinishedListener
        final String purchaseId = (String)IABManager.getInstance().products.get(idx < IABManager.getInstance().products.size() ? (idx-1) : (IABManager.getInstance().products.size()-1));
        Log.i(TAG, "开始购买->"+ purchaseId);
        PayManager.getInstance().pay(IABManager.getInstance().mainActive, purchaseId, new PayCallback() {
            @Override
            public void onSucc() {
                jsCallback("normalPayCallBack_true");
            }

            @Override
            public void onError(String msg) {
                jsCallback("normalPayCallBack_false");
            }
        });
//        final IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
//            public void onConsumeFinished(Purchase purchase, IabResult result) {
//                Log.i(TAG, "消耗完。购买（Purchase）： " + purchase + ", result: " + result);
//                // if we were disposed of in the meantime, quit.
//                if (IABManager.getInstance().mHelper == null) return;
//                // We know this is the "gas" sku because it's the only one we consume,
//                // so we don't check which sku was consumed. If you have more than one
//                // sku, you probably should check...
//                if (result.isSuccess()) {
//                    // successfully consumed, so we apply the effects of the item in our
//                    // game world's logic, which in our case means filling the gas tank a bit
//                    Log.i(TAG, "sku " + purchase.getSku());
//                    //Log.i(TAG, purchase.)
//                    Log.i(TAG, "消费成功。Provisioning." );
//                } else {
//                    Log.i(TAG,"Error while consuming: " + result);
//                }
//
//            }
//        };
//
//        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
//            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
//                Log.i(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
//
//                // if we were disposed of in the meantime, quit.
//                if (IABManager.getInstance().mHelper == null) return;
//
//                if (result.isFailure()) {
//                    Log.i(TAG,"Error purchasing: " + result);
//                    return;
//                }
////                if (!verifyDeveloperPayload(purchase)) {
////                    Log.i(TAG,"Error purchasing. Authenticity verification failed.");
////                    return;
////                }
//
//
//
//                Log.i(TAG, "购买完成.");
//                //购买完成时候就能获取到订单的详细信息：purchase.getOriginalJson(),要是想要什么就去purchase中get
//                //根据获取到产品的Id去判断是哪一项产品
//                if (purchase.getSku().equals(purchaseId)) {
//
//                    Log.i(TAG, "购买的是" + purchase.getSku());
//
//                    jsCallback("PayMgr.normalPayCallBack(true);");
//
//                    //非消耗品
//                    if (purchaseId.equals("com.riceball.gpknives.nointerstitial")) {
//                        jsCallback("PayMgr.restoreCallBack(true);");
//                        AFPushEvent(purchase, true);
//                        return;
//                    }
//
//                    //消耗品
//                    try {
//                        //购买完成之后去消耗产品
//                        IABManager.getInstance().mHelper.consumeAsync(purchase, mConsumeFinishedListener);
//                        AFPushEvent(purchase, false);
//
//                    } catch (IabHelper.IabAsyncInProgressException e) {
//                        jsCallback("PayMgr.normalPayCallBack(false);");
//
//                        Log.i(TAG,"Error consuming gas. Another async operation in progress.");
//                        return;
//                    }
//                }
//
//            }
//        };
//
//        try {
//            IABManager.getInstance().mHelper.launchPurchaseFlow(IABManager.getInstance().mainActive, purchaseId, RC_REQUEST, mPurchaseFinishedListener, payload);
//        } catch (Exception e) {
//            Log.i(TAG, "无法完成谷歌支付");
//        }
    }

//    public void destroyIap() {
//        if (mHelper != null) {
//            try {
//                mHelper.dispose();
//                mHelper = null;
//            } catch (IabHelper.IabAsyncInProgressException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public interface VerificationCallback
//    {
//        void call();
//    }
//
//    public static String getMd5(String input)
//    {
//        try {
//
//            // Static getInstance method is called with hashing MD5
//            MessageDigest md = MessageDigest.getInstance("MD5");
//
//            // digest() method is called to calculate message digest
//            //  of an input digest() return array of byte
//            byte[] messageDigest = md.digest(input.getBytes());
//
//            //String hexStr = Hex.bytesToStringLowercase(messageDigest);
//
//            // Convert byte array into signum representation
//            BigInteger no = new BigInteger(1, messageDigest);
//
//            // Convert message digest into hex value
//            String hashtext = no.toString(16);
//            while (hashtext.length() < 32) {
//                hashtext = "0" + hashtext;
//            }
//            return hashtext;
//        }
//
//        // For specifying wrong message digest algorithms
//        catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static void Verification(Purchase purchase, final VerificationCallback callback )
//    {
//        // Instantiate the RequestQueue.
//        Context context = IABManager.getInstance().mainActive;
//        if ( context == null || saveInventory == null )
//            return;
//
//        String deviceId =AppsFlyerLib.getInstance().getAppsFlyerUID(context);
//        RequestQueue queue = Volley.newRequestQueue(context);
//        String url ="https://api.riceballgames.com/v1/iap/a_consum";
//        SkuDetails detail =  saveInventory.getSkuDetails(purchase.getSku());
//        final String params = "game_id=" + "1588930600276" + "&" + "d_id=" + deviceId + "&" + "p_id=" + purchase.getSku() + "&" + "o_id=" + purchase.getOrderId() + "&" + "pk_name="
//                    + purchase.getPackageName() + "&" + "price=" + detail.getPriceAmountMicros() / (float)1000000 + "&" + "cu_code=" + detail.getPriceCurrencyCode() + "&"
//                + "p_token=" + purchase.getToken();
//
//        // Request a string response from the provided URL.
//        url = url + "?" + params;
//
//        Log.d("Knife:", "Post Url Is " + url);
//        Log.d("Knife", "Url Post " + " info = " + purchase.toString());
//        StringRequest stringRequest = new  StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.d("Knife Response", response);
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            if ( obj.getInt("code") == 0 )
//                            {
//                                Log.d("Knife", "verify success " + obj.getInt("code"));
//                                if ( callback != null)
//                                {
//                                    callback.call();
//                                }
//                            }
//                            else
//                            {
//                                Log.d("Knife", "verify failure " + obj.getInt("code"));
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("Knife Response", error.getMessage());
//                    }
//        }){
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//
//                Random rand = new Random();
//                final int salt = rand.nextInt(1000000);
//
//                String appId = "com.riceball.gpknives";
//                String signKey = "16cLtxiyPISCvIGEKgZgv2DrvMrbqWqo";
//                String saltStr = salt+"";
//                String signStr =  getMd5(appId + params + saltStr + signKey);
//
//                Log.d("Knife", "salt " + saltStr + " sign " + signStr);
//                //String signHex = Hex.bytesToStringLowercase( signStr.getBytes() );
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type","application/json");
//                headers.put("appid",appId);
//                headers.put("salt",saltStr);
//                headers.put("sign",signStr);
//                return headers;
//            }
//
//        };
//
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest);
//    }
}
