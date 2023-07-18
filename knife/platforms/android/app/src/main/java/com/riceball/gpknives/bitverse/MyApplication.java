package com.riceball.gpknives.bitverse;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

// import com.walletconnect.android.Core;
// import com.walletconnect.android.CoreClient;
// import com.walletconnect.android.relay.ConnectionType;
// import com.walletconnect.sign.client.Sign;
// import com.walletconnect.sign.client.SignClient;
// import com.walletconnect.sign.client.SignInterface;
// import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// import kotlin.TuplesKt;
// import kotlin.Unit;
// import kotlin.collections.CollectionsKt;
// import kotlin.collections.MapsKt;
// import kotlin.jvm.functions.Function0;
// import kotlin.jvm.functions.Function1;
// import kotlin.jvm.internal.Intrinsics;
import com.riceball.gpknives.connect.BitverseConnectApi;
import com.riceball.gpknives.connect.BitverseConnectDelegate;

public class MyApplication {
    private final List<String> iconList = new ArrayList<>();
    private static MyApplication mInstace = null;
    private BitverseConnectDelegate delegate = null;
    private static final String Tag = "MyApplication";

    public static MyApplication getInstance() {
        if (null == mInstace) {
            mInstace = new MyApplication();
        }
        return mInstace;
    }

    public void init(Application application) {
        Log.d(Tag, "init");
        iconList.add("https://verify.riceballgames.com/client/icons/1.png");
        iconList.add("https://verify.riceballgames.com/client/icons/2.png");
        iconList.add("https://verify.riceballgames.com/client/icons/3.png");

        BitverseConnectDelegate delegate = new MainConnectModel();
        BitverseConnectApi bitverseConnectApi = BitverseConnectApi.INSTANCE;
        bitverseConnectApi.initilize(application, "Knives Crash", "Knives Crash", "https://verify.riceballgames.com/kc_r/index.html", iconList, URLEncoder.encode("https://gpknives.page.link/?link=https://gpknives.page.link/N8fh&apn=com.riceball.gpknives"), delegate);
    }

    public void connect(Context context) {
        Log.d(Tag, "connect");
        BitverseConnectApi bitverseConnectApi = BitverseConnectApi.INSTANCE;
        bitverseConnectApi.connect(context,
                "Knives Crash",
                "Knives Crash",
                "https://verify.riceballgames.com/kc_r/index.html",
                iconList,
                URLEncoder.encode("https://gpknives.page.link/?link=https://gpknives.page.link/N8fh&apn=com.riceball.gpknives"));
    }

    // public void init2(Application application) {
    //     String serverUri = "wss://relay.walletconnect.com?projectId=cf8dec22ff95d4cec5f213305b5ff01f";
    //     CoreClient coreClient = new CoreClient();
    //     Core.Model.AppMetaData appMetaData = new
    //             Core.Model.AppMetaData(
    //             "Example App",
    //             "bitizenconnect_android_example",
    //             "https://example.com",
    //             CollectionsKt.listOf("https://bitizen.org/wp-content/uploads/2022/04/cropped-vi-192x192.png"),
    //             URLEncoder.encode("https://gpknives.page.link/?link=https://gpknives.page.link/N8fh&apn=com.riceball.gpknives"), "");

    //     coreClient.initialize(appMetaData,
    //             serverUri,
    //             ConnectionType.AUTOMATIC,
    //             application, null, "", null, null
    //     );

    //     Sign.Params.Init init = new Sign.Params.Init(coreClient);

    //     SignClient signClient = new SignClient();
    //     signClient.initialize(init, null, null);

    //     SignClient.DappDelegate dappDelegate = null;


    //     dappDelegate = new SignClient.DappDelegate() {
    //         public void onSessionApproved(@NotNull Sign.Model.ApprovedSession approvedSession) {
    //             Intrinsics.checkNotNullParameter(approvedSession, "approvedSession");
    //             String[] componets = approvedSession.accounts.get(0).split(":");
    //             int size = componets.length;
    //             BitverseConnectDelegate var10000;
    //             if (size > 1) {
    //                 var10000 = delegate;
    //                 if (var10000 != null) {
    //                     String var5 = (String) componets[size - 2];
    //                     var10000.didConnect(Integer.parseInt(var5), CollectionsKt.listOf(componets[size - 1]));
    //                 }
    //             } else {
    //                 var10000 = delegate;
    //                 if (var10000 != null) {
    //                     var10000.failedToConnect();
    //                 }
    //             }

    //         }

    //         public void onSessionRejected(@NotNull Sign.Model.RejectedSession rejectedSession) {
    //             Intrinsics.checkNotNullParameter(rejectedSession, "rejectedSession");
    //             BitverseConnectDelegate var10000 = delegate;
    //             if (var10000 != null) {
    //                 var10000.didDisconnect();
    //             }

    //         }

    //         public void onSessionUpdate(@NotNull Sign.Model.UpdatedSession updatedSession) {
    //             Intrinsics.checkNotNullParameter(updatedSession, "updatedSession");
    //         }

    //         public void onSessionExtend(@NotNull Sign.Model.Session session) {
    //             Intrinsics.checkNotNullParameter(session, "session");
    //         }

    //         public void onSessionEvent(@NotNull Sign.Model.SessionEvent sessionEvent) {
    //             Intrinsics.checkNotNullParameter(sessionEvent, "sessionEvent");
    //         }

    //         public void onSessionDelete(@NotNull Sign.Model.DeletedSession deletedSession) {
    //             Intrinsics.checkNotNullParameter(deletedSession, "deletedSession");
    //         }

    //         public void onSessionRequestResponse(@NotNull Sign.Model.SessionRequestResponse response) {
    //             Intrinsics.checkNotNullParameter(response, "response");
    //         }

    //         public void onConnectionStateChange(@NotNull Sign.Model.ConnectionState state) {
    //             Intrinsics.checkNotNullParameter(state, "state");
    //             boolean var2 = state.isAvailable();
    //             if (!var2) {
    //                 BitverseConnectDelegate var10000 = delegate;
    //                 if (var10000 != null) {
    //                     var10000.didDisconnect();
    //                 }
    //             } else {
    //                 String var3 = "connected";
    //                 System.out.print(var3);
    //             }

    //         }

    //         public void onError(@NotNull Sign.Model.Error error) {
    //             Intrinsics.checkNotNullParameter(error, "error");
    //         }
    //     };
    //     SignClient.INSTANCE.setDappDelegate((SignInterface.DappDelegate) dappDelegate);
    // }

    // public final void connect2(@NotNull final Context context, @NotNull String dappName, @NotNull String dappDescription, @NotNull String dappUrl, @NotNull List icons, @NotNull final String callbackUrl) {
    //     {
    //         Core.Model.Pairing pairing = CoreClient.INSTANCE.getPairing().create((Core.Model.Error error) -> {
    //             if (delegate != null) {
    //                 delegate.didDisconnect();
    //             }
    //             return null;
    //         });

    //         String namespace = "eip155";
    //         List chains = CollectionsKt.listOf("eip155:1");
    //         List methods = CollectionsKt.listOf(new String[]{"eth_sendTransaction", "personal_sign", "eth_sign", "eth_signTypedData"});
    //         List events = CollectionsKt.listOf(new String[]{"chainChanged", "accountsChanged"});//    val requiredNamespaces: Map<String, Sign.Model.Namespace.Proposal> = mapOf(namespace to Sign.Model.Namespace.Proposal(chains, methods, events)) /*Required namespaces to setup a session*/

    //         Map requiredNamespaces = MapsKt.mapOf(TuplesKt.to(namespace, new Sign.Model.Namespace.Proposal(chains, methods, events)));
    //         Sign.Params.Connect connectParams = new Sign.Params.Connect(requiredNamespaces, (Map) null, (Map) null, pairing);


    //         ConnectDelegate connectDelegate = new ConnectDelegate() {
    //             @Override
    //             public void onSuccess() {
    //                 new Runnable() {
    //                     @Override
    //                     public void run() {
    //                         String wsURL = pairing.getUri();
    //                         Log.e("BitverseConnectApi", "wsURL:" + wsURL);
    //                         String url = "https://bitverseapp.page.link/?apn=com.bitverse.app&afl=https://bitverse.zone/download?deeplink%3Dbitverseapp://open/wallet&isi=1645515614&ibi=com.bitverse.app&link=https://bitverse.zone/download?deeplink%3Dbitverseapp://open/wallet?uri=" + URLEncoder.encode(URLEncoder.encode(wsURL));
    //                         url = url + URLEncoder.encode(URLEncoder.encode("&callbackUrl=" + URLEncoder.encode(callbackUrl)));

    //                         Intent intent = new Intent();
    //                         intent.setAction("android.intent.action.VIEW");
    //                         Uri content_url = Uri.parse(url);
    //                         intent.setData(content_url);
    //                         context.startActivity(intent);
    //                     }
    //                 };
    //             }
    //         };

    //         ConnectDelegate connectDelegate2 = new ConnectDelegate() {
    //             @Override
    //             public void onSuccess() {
    //                 Log.e("BitverseConnectApi", "链接失败报错");
    //                 if (delegate != null) {
    //                     delegate.failedToConnect();
    //                 }
    //             }
    //         };

    //         SignClient.INSTANCE.connect(connectParams, (Function0<Unit>) connectDelegate, (Function1<? super Sign.Model.Error, Unit>) connectDelegate2);
    //     }
    // }
}