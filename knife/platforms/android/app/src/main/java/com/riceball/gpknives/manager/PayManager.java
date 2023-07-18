package com.riceball.gpknives.manager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import com.riceball.gpknives.AdManage;
import com.riceball.gpknives.callback.PayCallback;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class PayManager {
    public static volatile PayManager instance = null;

    public static PayManager getInstance() {
        if (null == instance){
            synchronized (PayManager.class){
                if (null == instance){
                    instance = new PayManager();
                }
            }
        }
        return instance;
    }

    private BillingClient billingClient;

    private String prices = "prices";
    private String purchase = "purchase";

    private String currency = "";
    private PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, list) -> {
        Log.v("Constents.LOG_TAG", "purchasesUpdatedListener code " + billingResult.getResponseCode() + " msg " + billingResult.getDebugMessage());
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && list != null) {
            if (null != payCallback) {
                handlePurchase(list.get(0));
            }


        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            if (null != payCallback) {
                payCallback.onError("pay cancel");
                payCallback = null;
            }
        } else {
            // Handle any other error codes.
            if (null != payCallback) {
                payCallback.onError("pay error");
                payCallback = null;

//                handlePurchase(list.get(0));
            }
        }
    };

    private boolean isConnectGooglePlay = false;

    private static PayCallback payCallback = null;

    public void init (Context context) {
        billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        this.startConnect();
    }

    private void startConnect() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                Log.v("Constents.LOG_TAG", "startConnection code " + billingResult.getResponseCode() + " msg " + billingResult.getDebugMessage());
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    isConnectGooglePlay = true;
                    getPrice();
//                    queryGPPurchase();
                    querySubscribePurchase(); //返回有效订阅，可以恢复订阅商品
                    // queryPurchaseHistory();   //返回购买记录，配合API可以恢复内购商品（读取google play vending缓存）
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.v("Constents.LOG_TAG", "startConnection onBillingServiceDisconnected ");
                isConnectGooglePlay = false;
                // startConnect();
            }
        });
    }

    public void getPrice () {
        Log.v("Constents.LOG_TAG", "getPrice-->");
        List<String> products = new ArrayList<>();
        products.add("com.riceball.gpknives.diamond10");
        products.add("com.riceball.gpknives.diamond50");
        products.add("com.riceball.gpknives.diamond100");
        products.add("com.riceball.gpknives.diamond250");
        products.add("com.riceball.gpknives.diamond500");
        // products.add("com.riceball.gpknives.nointerstitial");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(products).setType(BillingClient.SkuType.INAPP);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("queryProductsCallBack_price");

        billingClient.querySkuDetailsAsync(params.build(), (billingResult, skuDetailsList) -> {
            if(skuDetailsList!=null){
                Log.v("Constents.LOG_TAG", skuDetailsList.size() + "");
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList.size() != 0) {
                    List<String> pricesList = new ArrayList<>();
                    
                    for (int i = 0; i < skuDetailsList.size(); i++) {
                        SkuDetails skuDetails = skuDetailsList.get(i);
                        // stringBuffer.append("#");
                        // stringBuffer.append(skuDetails.getPrice());
                        pricesList.add(skuDetails.getPrice());
                        if(currency==""){
                            currency = skuDetails.getPriceCurrencyCode();
                        }
                    }

                    currency = currency.substring(0,currency.length()-1);

                    // Log.v("Constents.LOG_TAG.currency1", currency);

                    String str = pricesList.get(0);

                    // Log.v("Constents.LOG_TAG.str", str);

                    //货币符号
                    char c = str.charAt(currency.length());
                    String str2 = String.valueOf(c);

                    // Log.v("Constents.LOG_TAG.str2", str2);

                    currency = currency + str2;
                    Log.v("Constents.LOG_TAG.currency", currency);
                    Collections.sort(pricesList, new Comparator<String>() {
                        @Override
                        public int compare(String s, String t1) {
                            if(s.length() < t1.length()){
                                return -1;
                            }
                            try {
                                String s_n = s.replace(currency,"");
                                String t1_n = t1.replace(currency,"");

                                double price_s = Double.parseDouble(s_n);
                                double price_t1 = Double.parseDouble(t1_n);

                                if(price_s < price_t1){
                                    return -1;
                                }
                                return 0;
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            return 0;
                        }
                    });
                    for (int i = 0; i < pricesList.size(); i++) {
                        stringBuffer.append("#");
                        stringBuffer.append(pricesList.get(i));
                    }
                    stringBuffer.append("');");
                    prices = stringBuffer.toString();
                    Log.v("Constents.LOG_TAG.prices", prices);
                } else {
                    prices = "queryProductsCallBack_price#PHP 105.00#PHP 410.00#PHP 775.00#PHP 1,550.00#PHP 2,850.00";
                }
            }
            else{
                Log.v("Constents.LOG_TAG" ,"skuDetailsList==null");
                Log.v("Constents.LOG_TAG", prices);
            }
        });
    }

    public String getGPPrices () {
        return this.prices;
    }


    public void pay (Context context, String id, PayCallback callback) {
        if (!isConnectGooglePlay) {
            callback.onError("connect to GooglePlay");
            return;
        }
//        if(Constents.IS_DEBUG) {
//        id = "testtest";
//        }
        List<String> skuList = new ArrayList<>();
        skuList.add(id);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();

        Log.d("PayManager","pay->" + id);

        if(id=="com.riceball.gpknives.subweekly"){
            params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
        }
        else{
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        }
        billingClient.querySkuDetailsAsync(params.build(), (billingResult, skuDetailsList) -> {
            // Process the result.
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList.size() != 0) {
                // The BillingClient is ready. You can query purchases here.
                SkuDetails skuDetails = skuDetailsList.get(0);
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();
                int responseCode = billingClient.launchBillingFlow((Activity) context, billingFlowParams).getResponseCode();
                if (responseCode != BillingClient.BillingResponseCode.OK) {
                    Log.v("Constents.LOG_TAG", "querySkuDetailsAsync pay error code " + billingResult.getResponseCode() + " msg " + billingResult.getDebugMessage());
                    callback.onError("pay error");
                }
                else {
                    payCallback = callback;
                }
            } else {
                Log.v("Constents.LOG_TAG", "querySkuDetailsAsync don't have product code " + billingResult.getResponseCode() + " msg " + billingResult.getDebugMessage());
                Log.v("Constents.LOG_TAG", "don't have product skuDetailsList " + skuDetailsList.size());
                callback.onError("don't have product");
            }
        });
    }

    private void handlePurchase(Purchase purchase) {
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.
        //处理购买交易，所有购买都需要确认。未能确认购买将导致该购买被退款。
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                String getOriginalJson  = purchase.getOriginalJson ();
                String productId = "";
                try {
                    JSONObject jsonPurchaseInfo = new JSONObject(getOriginalJson);
                    // String purchaseToken =  jsonPurchaseInfo.get("purchaseToken");
                    productId = jsonPurchaseInfo.getString("productId");
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
                Log.d("Constents.LOG_TAG","getOriginalJson " + getOriginalJson);
                Log.d("Constents.LOG_TAG","productId " + productId);
                if(Objects.equals(productId,"com.riceball.gpknives.subweekly")){
                    Log.d("Constents.LOG_TAG","处理订阅商品");
                    //处理订阅商品
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();

                    AcknowledgePurchaseResponseListener listener = (billingResult) -> {
                        Log.v("Constents.LOG_TAG", "billingClient.acknowledgePurchase code " + billingResult.getResponseCode() + " msg " + billingResult.getDebugMessage());
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            // Handle the success of the consume operation.
                            payCallback.onSucc();
                            payCallback = null;
                            AdManage.getInstance().isVipNoOpenAd = true;
                        }
                        else {
                            payCallback.onError("billingClient.consumeAsync fail");
                            payCallback = null;
                        }
                    };

                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, listener);
                }
                else{
                    //处理一次性产品
                    ConsumeParams consumeParams =
                            ConsumeParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
                        Log.v("Constents.LOG_TAG", "billingClient.consumeAsync code " + billingResult.getResponseCode() + " msg " + billingResult.getDebugMessage());
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            // Handle the success of the consume operation.
                            payCallback.onSucc();
                            payCallback = null;
                        }
                        else {
                            payCallback.onError("billingClient.consumeAsync fail");
                            payCallback = null;
                        }
                    };
                    billingClient.consumeAsync(consumeParams, listener);
                }
            }
        }
    }

    public void querySubscribeSku(){
        List<String> skuList = new ArrayList<>();
        skuList.add("com.riceball.gpknives.subweekly");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS); //查询订阅

        billingClient.querySkuDetailsAsync(params.build(), (billingResult, skuDetailsList) -> {
            // Process the result.
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList.size() != 0) {
                // The BillingClient is ready. You can query purchases here.
                SkuDetails skuDetails = skuDetailsList.get(0);
                //订阅商品本地区价格
                String price = skuDetails.getPrice();

                String skuType = skuDetails.getType();

                //到期时间
                String expiryTime = skuDetails.getSubscriptionPeriod();

                String freeTime = skuDetails.getFreeTrialPeriod();

                //packageName
                String zzd = skuDetails.zzd();

                //skuDetailsToken
//                String zzf = skuDetails.zzf();
                String zzf = "token";

                Log.v("Constents.LOG_TAG", "price = " + price + " " + skuType + " " + expiryTime + " " + freeTime + " "
                    + zzd + " " + zzf
                );
            }
        });
    }

    public void queryGPPurchase(){
        Log.v("Constents.LOG_TAG","queryGPPurchase-------start>");
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult, skuDetailsList) -> {
            // Process the result.
            Log.v("Constents.LOG_TAG","queryGPPurchase getResponseCode " + billingResult.getResponseCode());
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if(skuDetailsList.size() != 0){
                    // The BillingClient is ready. You can query purchases here.

                    for (int i = 0; i < skuDetailsList.size(); i++) 
                    {
                        Purchase skuDetails = skuDetailsList.get(i);

                        //是否自动更新
                        Boolean isAutoRenewing = skuDetails.isAutoRenewing();

                        //是否确认
                        Boolean isAcknowledged = skuDetails.isAcknowledged();

                        //购买状态
                        int getPurchaseState = skuDetails.getPurchaseState();

                        //购买时间
                        long getPurchaseTime = skuDetails.getPurchaseTime();

                        //packageName
                        String getPackageName = skuDetails.getPackageName();

                        String getPurchaseToken = skuDetails.getPurchaseToken();

                        Log.v("Constents.LOG_TAG", "purchase deltail = " + isAutoRenewing + " " + isAcknowledged + " " 
                            + getPurchaseState + " " + getPurchaseTime + " "
                            + getPackageName + " " +getPurchaseToken
                        );
                    }
                }
                else{
                    Log.v("Constents.LOG_TAG","skuDetailsList.size " + skuDetailsList.size());
                }

            }
            else{
                
            }
        });
    }

    public void queryPurchaseHistory (){
        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, (billingResult, recordList) -> {
            // Process the result.

            Log.v("Constents.LOG_TAG","queryPurchaseHistory getResponseCode " + billingResult.getResponseCode() + " " + recordList.size());

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if(recordList.size() != 0){
                    // The BillingClient is ready. You can query purchases here.

                    for (int i = 0; i < recordList.size(); i++) 
                    {
                        PurchaseHistoryRecord details = recordList.get(i);
                        List<String>skus = details.getSkus();
                        for (int j = 0; j < skus.size(); j++) {
                            Log.d("Constents.LOG_TAG",j + " " + skus.get(j));
                        }
                        //购买时间
                        long getPurchaseTime = details.getPurchaseTime();
                        //令牌
                        String getPurchaseToken = details.getPurchaseToken();
                        //productID
                        String productID = skus.get(0);
                        checkProductByAPI(productID,getPurchaseToken);

                        Log.v("Constents.LOG_TAG", "purchase deltail = "  + getPurchaseTime + " " + details.toString());
                    }
                }
                else{
                    Log.v("Constents.LOG_TAG"," queryPurchaseHistory recordList.size = " + recordList.size());
                }
            }
            else{

            }
        });
    }

    //需检查订阅的状态，您的应用可以使用 Google Play 结算库中的 BillingClient.queryPurchasesAsync() 或 
    //Google Play Developer API 中的 Purchases.subscriptionsv2:get 进行查询。
    public void querySubscribePurchase(){
        purchase = "querySubscribePurchaseCallback_0";


        Log.v("Constents.LOG_TAG","querySubscribePurchase-------start>");

        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult, skuDetailsList) -> {
            // Process the result.

            Log.v("Constents.LOG_TAG","querySubscribePurchase getResponseCode " + billingResult.getResponseCode());

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                if(skuDetailsList.size() != 0){
                    // The BillingClient is ready. You can query purchases here.
                    Purchase skuDetails = skuDetailsList.get(0);

                    //是否自动更新
                    Boolean isAutoRenewing = skuDetails.isAutoRenewing();

                    //是否确认
                    Boolean isAcknowledged = skuDetails.isAcknowledged();

                    //购买状态
                    int getPurchaseState = skuDetails.getPurchaseState();

                    //购买时间
                    long getPurchaseTime = skuDetails.getPurchaseTime();

                    //packageName
                    String getPackageName = skuDetails.getPackageName();

                    String getPurchaseToken = skuDetails.getPurchaseToken();

                    Log.v("Constents.LOG_TAG", "purchase deltail = " + isAutoRenewing + " " + isAcknowledged + " " 
                        + getPurchaseState + " " + getPurchaseTime + " "
                        + getPackageName + " " +getPurchaseToken
                    );

                    purchase = "querySubscribePurchaseCallback_"+ getPurchaseTime;

                    AdManage.getInstance().isVipNoOpenAd = true;
                }
                else{
                    Log.v("Constents.LOG_TAG"," querySubscribePurchase skuDetailsList.size = " + skuDetailsList.size());
                }

            }
            else{
                
            }
        });
    }

    public String getSUBPurchase () {
        return this.purchase;
    }


    public void checkProductByAPI(String productId,String token){
//        RestTemplate restTemplate=new RestTemplate();
//        String result=restTemplate.exchange(url, HttpMethod.GET,null,String.class).getBody();
        StringBuffer url =new StringBuffer();
//         url.append("https://developers.google.com/android-publisher/api-ref/purchases/products /get");
        url.append("https://androidpublisher.googleapis.com/androidpublisher/v3/applications/com.riceball.gpknives/purchases/products/");
        url.append(productId);
        url.append("/tokens/");
        url.append(token);
        String res = doGet(url.toString());
        Log.d("Constents.LOG_TAG",res);
    }


    public String doGet(String httpUrl){
        //链接
        Log.d("Constents.LOG_TAG","doGet = " +  httpUrl);
        if(1 > 0){
            return "";
        }
        HttpURLConnection connection=null;
        InputStream is=null;
        BufferedReader br = null;
        StringBuffer result=new StringBuffer();
        try {
            //创建连接
            URL url=new URL(httpUrl);
            connection= (HttpURLConnection) url.openConnection();
            //设置请求方式
            connection.setRequestMethod("GET");
            //设置连接超时时间
            connection.setConnectTimeout(15000);
            //设置读取超时时间
            connection.setReadTimeout(15000);
            //开始连接
            connection.connect();
            //获取响应数据
            if(connection.getResponseCode()==200){
                //获取返回的数据
                is=connection.getInputStream();
                if(is!=null){
                    br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    String temp = null;
                    while ((temp=br.readLine())!=null){
                        result.append(temp);
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            connection.disconnect();// 关闭远程连接
        }
        return result.toString();
    }


}
