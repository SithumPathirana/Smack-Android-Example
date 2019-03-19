package com.example.smackandroid.service

import android.content.Context
import android.content.Intent
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
import org.jivesoftware.smack.roster.Roster
import java.io.IOException
import org.jxmpp.jid.impl.JidCreate





class NetworkConnection(context: Context):ConnectionListener {

    private val TAG = "NetworkConnection"

    private var mApplicationContext: Context? = null
    private var mUsername: String? = null
    private var mPassword: String? = null
    private var mServiceName: String? = null
    private var mConnection: XMPPTCPConnection?=null

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
            mServiceName=""
        }
    }

    @Throws(IOException::class, XMPPException::class, SmackException::class)
    fun connect() {
        Log.d(TAG, "Connecting to server $mServiceName")
        val builder = XMPPTCPConnectionConfiguration.builder()
        val serviceName = JidCreate.domainBareFrom(mServiceName)
        builder.setXmppDomain(serviceName)
        builder.setUsernameAndPassword(mUsername, mPassword)
        builder.setResource("SmackAndroid")

        //Set up the ui thread broadcast message receiver.
        //setupUiThreadBroadCastMessageReceiver();



        mConnection = XMPPTCPConnection(builder.build())
        mConnection?.addConnectionListener(this)
        val roster=Roster.getInstanceFor(mConnection)
        roster.isRosterLoadedAtLogin=true
        mConnection?.connect()
        mConnection?.login()

        var reconnectionManager = ReconnectionManager.getInstanceFor(mConnection)
        ReconnectionManager.setEnabledPerDefault(true)
        reconnectionManager.enableAutomaticReconnection()

    }

    fun disconnect() {
        Log.d(TAG, "Disconnecting from serser $mServiceName")
        try {
            if (mConnection != null) {
                mConnection?.disconnect()
            }

        } catch (e: SmackException.NotConnectedException) {
            NetworkConnectionService.sConnectionState = ConnectionState.DISCONNECTED
            e.printStackTrace()

        }


    }

    private fun showContactListActivityWhenAuthenticated(){
        val intent=Intent(NetworkConnectionService.UI_AUTHENTICATED)
        intent.setPackage(mApplicationContext?.packageName)
        mApplicationContext?.sendBroadcast(intent)
        Log.d(TAG,"Sent the broadcast that we are authenticated")
    }




    override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
        NetworkConnectionService.sConnectionState=ConnectionState.CONNECTED
        Log.d(TAG,"Authenticated Successfully")
        showContactListActivityWhenAuthenticated()
    }

    override fun connected(connection: XMPPConnection?) {
        NetworkConnectionService.sConnectionState=ConnectionState.CONNECTED
        Log.d(TAG,"Connected Successfully")
    }

    override fun connectionClosed() {
        NetworkConnectionService.sConnectionState=ConnectionState.DISCONNECTED
        Log.d(TAG,"Connection closed")
    }

    override fun connectionClosedOnError(e: Exception?) {
        NetworkConnectionService.sConnectionState=ConnectionState.DISCONNECTED
        Log.d(TAG,"ConnectionClosedOnError, error "+ e.toString())
    }

    override fun reconnectingIn(seconds: Int) {
        NetworkConnectionService.sConnectionState = ConnectionState.CONNECTING
        Log.d(TAG,"ReconnectingIn")
    }

    override fun reconnectionFailed(e: Exception?) {
        NetworkConnectionService.sConnectionState = ConnectionState.DISCONNECTED
        Log.d(TAG,"ReconnectionFailed()")
    }

    override fun reconnectionSuccessful() {
        NetworkConnectionService.sConnectionState = ConnectionState.CONNECTED
        Log.d(TAG,"ReconnectionSuccessful")
    }


}