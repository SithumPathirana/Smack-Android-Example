package com.example.smackandroid.service

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import java.lang.Exception
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smack.ReconnectionManager
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import java.io.IOException
import org.jxmpp.jid.impl.JidCreate





class NetworkConnection(context: Context):ConnectionListener {

    private val TAG = "NetworkConnection"

    private var mApplicationContext: Context? = null
    private var mUsername: String? = null
    private var mPassword: String? = null
    private var mServiceName: String? = null
    lateinit var mConnection: XMPPTCPConnection

    enum class ConnectionState {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED
    }

    enum class LoggedInState {
        LOGGED_IN, LOGGED_OUT
    }


    init {
        Log.d(TAG,"NetworkConnection Constructor called.")
        mApplicationContext=context.applicationContext
        val jid=PreferenceManager.getDefaultSharedPreferences(mApplicationContext).getString("xmpp_jid",null)
        mPassword=PreferenceManager.getDefaultSharedPreferences(mApplicationContext).getString("xmpp_password",null)

        if (jid!=null){
            mUsername=jid.split("@")[0]
            mServiceName=jid.split("@")[1]

        }else{
            mUsername=""
            mPassword=""
        }
    }

    @Throws(IOException::class, XMPPException::class, SmackException::class)
    fun connect() {
        Log.d(TAG, "Connecting to server $mServiceName")
        val builder = XMPPTCPConnectionConfiguration.builder()
        val serviceName = JidCreate.domainBareFrom(mServiceName)
        builder.setServiceName(serviceName)
        builder.setUsernameAndPassword(mUsername, mPassword)
        builder.setRosterLoadedAtLogin(true)
        builder.setResource("Rooster")

        //Set up the ui thread broadcast message receiver.
        //setupUiThreadBroadCastMessageReceiver();

        mConnection = XMPPTCPConnection(builder.build())
        mConnection.addConnectionListener(this)
        mConnection.connect()
        mConnection.login()

        val reconnectionManager = ReconnectionManager.getInstanceFor(mConnection)
        reconnectionManager.setEnabledPerDefault(true)
        reconnectionManager.enableAutomaticReconnection()

    }




    override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun connected(connection: XMPPConnection?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun connectionClosed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun connectionClosedOnError(e: Exception?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun reconnectingIn(seconds: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun reconnectionFailed(e: Exception?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun reconnectionSuccessful() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}