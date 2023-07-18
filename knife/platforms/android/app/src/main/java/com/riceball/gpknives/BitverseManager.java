package com.riceball.gpknives;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;


import androidx.annotation.Nullable;

import com.riceball.gpknives.R;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.riceball.gpknives.connect.BitverseConnectApi;
import com.riceball.gpknives.connect.BitverseConnectDelegate;
import com.riceball.gpknives.bitverse.MyApplication;

public class BitverseManager {
    private static BitverseManager mInstace = null;
    private Context mainActive = null;
    private static final String Tag = "BitverseManager";


    // private  BitverseConnectApi connectApi = null;
    private List<String> iconList=new ArrayList<>();

    //private List<String> walletAdressList=new ArrayList<>();
    private String  walletAdress = "";

    public static BitverseManager getInstance() {
        if (null == mInstace) {
            mInstace = new BitverseManager();
        }
        return mInstace;
    }

    public void init(Context context){
        this.mainActive = context;

        iconList.add("https://verify.riceballgames.com/client/icons/1.png");
        iconList.add("https://verify.riceballgames.com/client/icons/2.png");
        iconList.add("https://verify.riceballgames.com/client/icons/3.png");
    }

    public static void Connect(){
        Log.d(Tag, "Connect 1");
        BitverseManager.getInstance().connect();
    }

    public void connect(){
        Log.d(Tag, "connect 2");
        MyApplication.getInstance().connect(this.mainActive);

    //     //BitverseConnectApi
    //     connectApi = new BitverseConnectApi(new BitverseConnectDelegate() {
    //         @Override
    //         public void didConnect(@Nullable Integer integer, @Nullable List<String> list) {
    //             Log.d("BitverseConnectApi","didConnect 连接成功，返回chain id 和钱包地址");
    //             for (int i = 0; i <list.size() ; i++) {
    //                 Log.d("integer","integer = " + integer);
    //                 Log.d("list","list[i] = " + i + " " + list.get(i));
    //             }
    //             walletAdress = list.get(0); //测试： 0xDC0DB1059348584132DbB65C43bfA746ED960398
    //             jsCallback("onBitVerseConnectCallBack_"+ walletAdress);
    //         }
    //         @Override
    //         public void failedToConnect() {
    //             Log.d("BitverseConnectApi","failedToConnect 连接失败");
    //         }
    //         @Override
    //         public void didDisconnect() {
    //             Log.d("BitverseConnectApi","didDisconnect 连接断开");

    //         }
    //     });

    //     //链接钱包
    //    connectApi.connect(this.mainActive,
    //            "Knife Crash",
    //            "bitverseconnect_android_example",
    //            "https://verify.riceballgames.com/kc_r/index.html",
    //            iconList,
    //            URLEncoder.encode("https://gpknives.page.link/?link=https://gpknives.page.link/N8fh&apn=com.riceball.gpknives")
    //    );
    }

    public boolean isAvilible(Context context, String packageName){
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> pName = new ArrayList<String>();
        // 从pinfo中将包名字取出
        if (pinfo != null){
            for (int i = 0; i < pinfo.size(); i++){
                String pf = pinfo.get(i).packageName;
                pName.add(pf);
            }
        }
        // 判断pName中是否有目标程序的包名，有true，没有false
        return pName.contains(packageName);
    }

    public void Installed()
    {
        String packageName = "com.bitverse.app";
        boolean isInstalled =  isAvilible(this.mainActive,packageName);
        if(isInstalled){
            jsCallback("onBitVerseInstalledCallBack_true");
        }
        else{
            jsCallback("onBitVerseInstalledCallBack_false");
        }
    }

    public static void isInstalled(){
       BitverseManager.getInstance().Installed();
    }

    public static void openUrl(String url) {
        BitverseManager.getInstance().oepnwebUrl(url);
    }

    public void oepnwebUrl(String url) {
         Intent intent = new Intent();
         intent.setAction("android.intent.action.VIEW");
         Uri content_url = Uri.parse(url);
         intent.setData(content_url);
         this.mainActive.startActivity(intent);
        // ((MainActivity)this.mainActive).webViewOpen(url);
//        WebView webView = new WebView(this);
//        webView.loadUrl("http://shouji.baidu.com");
    }

    public void jsCallback(final String jsCodeStr) {
        Log.d(Tag, "jsCallback code:" + jsCodeStr);
        MainActivity.getInstance().handleJsCallabackMessage(jsCodeStr);
    }
}