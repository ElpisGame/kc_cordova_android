package com.riceball.gpknives;
 
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
 
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

//从商城获取app,或者打开本地
public class JumpToApp {
    private static JumpToApp mInstace = null;
    //得到版本号
    public static String versionName;
 
    private Context mainActive = null;
    public static JumpToApp getInstance() {
        if (null == mInstace) {
            mInstace = new JumpToApp();
        }
        return mInstace;
    }
 
    public void init(Context context){
        mainActive = context;
    }
    //跳谷歌商城下的游戏页面=============================
    public static void getAppByGooglePlay(String appPkg){
		Log.e("JumpToApp", "---getAppByGooglePlay---"+ appPkg);
        mInstace.appUpdate(JumpToApp.getInstance().mainActive,appPkg,"com.android.vending");
    }


    /**
     * 判断应用市场是否存在的方法
     * @param context
     * @param packageName
     * 主流应用商店对应的包名
     *  com.android.vending    -----Google Play
     *  com.tencent.android.qqdownloader    -----应用宝
     *  com.qihoo.appstore    -----360手机助手
     *  com.baidu.appsearch    -----百度手机助
     *  com.xiaomi.market    -----小米应用商店
     *  com.wandoujia.phoenix2    -----豌豆荚
     *  com.huawei.appmarket    -----华为应用市场
     *  com.taobao.appcenter    -----淘宝手机助手
     *  com.hiapk.marketpho    -----安卓市场
     *  cn.goapk.market        -----安智市场
     * @return
     */
    public static boolean isAvilible(Context context, String packageName){
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
 
    //得到版本号
    public static void getVersionName(Context context){
        PackageManager manager = context.getPackageManager();
        try{
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),0);
            versionName = info.versionName;
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
    }
 
    private void appUpdate(Context context,String appPkg, String marketPkg){
        //判断应用市场是否有包名
        if(isAvilible(context,appPkg)){
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPkg);
            context.startActivity(intent);
        }else if(isAvilible(context,marketPkg)){
            launchAppDetail(context,appPkg,marketPkg);
        }else { //没有，用浏览器打开地址进行下载
            //安装市场应用，那么根据后台返回的地址，用浏览器打开地址进行下载
            //Uri uri = Uri.parse(url);
            //Intent intent =new Intent(Intent.ACTION_VIEW, uri);
            //startActivity(intent);
        }
        //Toast.makeText(this,"立即更新",Toast.LENGTH_LONG).show();
    }
 
    /**
     * 启动到应用商店app详情界面
     * @param mContext
     * @param appPkg 目标App的包名
     * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面
     */
    //跳转更新
    public static void launchAppDetail(Context mContext, String appPkg, String marketPkg){
        try{
            if (TextUtils.isEmpty(appPkg)){
                return;
            }
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent =new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)){
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}