package com.riceball.gpknives.connect.impls

import com.riceball.gpknives.connect.Client
import com.riceball.gpknives.connect.Session
import com.riceball.gpknives.connect.Transaction
import org.komputing.khex.extensions.toNoPrefixHexString
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class BCClient(
    private val clientMeta: Session.PeerMeta,
    private val payloadAdapter: Session.PayloadAdapter,
    private val transportBuilder: Session.Transport.Builder,
    private val handleStatus: (Session.Transport.Status) -> Unit,
    private val handleMessages: (Session.MethodCall) -> Unit,
) : Client {

    private val keyLock = Any()

    // Persisted state
    private var config: Client.Config? = null

    // Getters
    private val encryptionKey: String
        get() = config!!.key!!

    private val decryptionKey: String
        get() = config!!.key!!

    // Non-persisted state
    private var transport: Session.Transport? = null
    private val requests: MutableMap<Long, (Session.MethodCall.Response) -> Unit> =
        ConcurrentHashMap()

    @Suppress("NewApi")
    override fun connect(
        config: Client.Config,
        callback: (Session.MethodCall.Response) -> Unit
    ): String {
        val isOldConnection = config.topic != null

        if (config.key == null) config.key =
            ByteArray(32).also { Random().nextBytes(it) }.toNoPrefixHexString()
        if (config.peerId == null) config.peerId = UUID.randomUUID().toString()
        if (config.topic == null) config.topic = UUID.randomUUID().toString()

        this.config = config
        transport = transportBuilder.build(config.bridge, handleStatus, ::handleMessage)

        transport?.send(
            Session.Transport.Message(
                config.peerId!!, "sub", ""
            )
        )

        if (!isOldConnection) {
            send(
                Session.MethodCall.SessionRequest(
                    WCSession.createCallId(),
                    Session.PeerData(id = config.peerId!!, clientMeta)
                ),
                topic = config.topic!!,
                callback
            )
        }

        val wcURL = Session.Config(
            handshakeTopic = config.topic!!,
            bridge = config.bridge,
            key = config.key
        );
        return  "https://bitverseapp.page.link/?apn=com.bitverse.app&afl=https://bitverse.zone/download?deeplink%3Dbitverseapp://open/wallet&isi=1645515614&ibi=com.bitverse.app&link=https://bitverse.zone/download?deeplink%3Dbitverseapp://open/wallet?uri="+URLEncoder.encode(URLEncoder.encode(wcURL.toWCUri()));

    }

    override fun ethSign(message: String, account: String, callback: (Session.MethodCall.Response) -> Unit){
        performMethodCall(Session.MethodCall.SignMessage(
            WCSession.createCallId(),
            address = account,
            message = message,
        ),callback)
    }

    override fun personalSign(
        message: String,
        account: String,
        callback: (Session.MethodCall.Response) -> Unit
    ) {
        performMethodCall(Session.MethodCall.Custom(
            WCSession.createCallId(),
            method = "personal_sign",
            params = listOf(message,account)
        ),callback)
    }

    override fun ethSignTypedData(
        message: String,
        account: String,
        callback: (Session.MethodCall.Response) -> Unit
    ) {
        performMethodCall(Session.MethodCall.Custom(
            WCSession.createCallId(),
            method = "eth_signTypedData",
            params = listOf(account,message)
        ),callback)
    }

    override fun ethSendTransaction(transaction: Transaction,callback: (Session.MethodCall.Response) -> Unit) {
        performMethodCall(Session.MethodCall.SendTransaction(
         id = WCSession.createCallId(),
         from=transaction.from,
         to=transaction.to,
         nonce=transaction.nonce,
         gasPrice=transaction.gasPrice,
         gasLimit=transaction.gasLimit,
         value=transaction.value,
         data=transaction.data,
        ),callback)
    }

    override fun performMethodCall(
        call: Session.MethodCall,
        callback: ((Session.MethodCall.Response) -> Unit)?
    ) {
        send(call, callback = callback)
    }

    @Suppress("NewApi")
    private fun handleMessage(message: Session.Transport.Message) {
        if (message.type != "pub") return
        val data: Session.MethodCall
        synchronized(keyLock) {
            try {
                data = payloadAdapter.parse(message.payload, decryptionKey)
            } catch (e: Exception) {
                println("bingo handleMessage $e")
                return
            }
        }
        println("bingo handleMessage $data")
        when (data) {
            is Session.MethodCall.Response -> {
                val callback = requests[data.id] ?: return
                callback(data)
            }
            else -> {
                handleMessages(data)
            }
        }
    }

    // Returns true if method call was handed over to transport
    private fun send(
        msg: Session.MethodCall,
        topic: String? = config!!.topic!!,
        callback: ((Session.MethodCall.Response) -> Unit)? = null
    ): Boolean {
        topic ?: return false

        val payload: String
        synchronized(keyLock) {
            payload = payloadAdapter.prepare(msg, encryptionKey)
        }
        callback?.let {
            requests[msg.id()] = callback
        }
        transport?.send(Session.Transport.Message(topic, "pub", payload))
        return true
    }

    override fun disconnect() {
        send(
            Session.MethodCall.SessionUpdate(
                id = System.currentTimeMillis(),
                params = Session.SessionParams(
                    approved = false,
                    chainId = null,
                    accounts = null,
                    peerData = null
                )
            )
        )
        transport?.close()
        transport = null;
    }

    override fun reconnectIfNeeded() {
        transport?.let {
            if(!it.isConnected()){
                config?.apply {
                    connect(this){}
                }
            }
        }
    }

    override fun serialize(): Client.Config? {
        return config
    }

    override fun transport(): Session.Transport? {
        return transport
    }
}
