package com.riceball.gpknives.bitverse;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import com.riceball.gpknives.connect.BitverseConnectDelegate;

import com.riceball.gpknives.MainActivity;
public class MainConnectModel implements BitverseConnectDelegate {
    @Override
    public void didConnect(@Nullable Integer integer, @Nullable List<String> list) {
        Log.d("MainConnectModel","didConnect 连接成功，返回chain id 和钱包地址");
        for (int i = 0; i <list.size() ; i++) {
            Log.d("integer","integer = " + integer);
            Log.d("list","list[i] = " + i + " " + list.get(i));
        }
        String walletAdress = list.get(0); //测试： 0xDC0DB1059348584132DbB65C43bfA746ED960398
        MainActivity.getInstance().handleJsCallabackMessage("onBitVerseConnectCallBack_" + walletAdress);
//        BitverseManager.getInstance().jsCb("AdvertMgr.onBitVerseConnectCallBack('"+ walletAdress +"')");
    }
    @Override
    public void failedToConnect() {
        Log.d("MainConnectModel","failedToConnect 连接失败");
    }
    @Override
    public void didDisconnect() {
        Log.d("MainConnectModel","didDisconnect 连接断开");
    }
}
