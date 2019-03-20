package com.example.smackandroid.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.os.Handler
import android.os.Looper
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.SmackException
import java.io.IOException


class NetworkConnectionService:Service(){

    private var mActive: Boolean = false//Stores whether or not the thread is active
    private var mThread: Thread?=null
    private var mTHandler: Handler?=null//We use this handler to post messages to
    //the background thread.

    private var mConnection: NetworkConnection? = null

    companion object {

        const val TAG="NetworkConnection"

       const val UI_AUTHENTICATED = "com.example.smackandroid.uiauthenticated"
       const  val SEND_MESSAGE = "com.example.smackandroid.sendmessage"
       const  val BUNDLE_MESSAGE_BODY = "b_body"
        const  val BUNDLE_TO = "b_to"

       const val NEW_MESSAGE = "com.blikoon.rooster.newmessage"
       const  val BUNDLE_FROM_JID = "b_from"



        var sConnectionState: NetworkConnection.ConnectionState? = null
        var sLoggedInState: NetworkConnection.LoggedInState? = null


        fun getState(): NetworkConnection.ConnectionState {
            return if (sConnectionState == null) {
                NetworkConnection.ConnectionState.DISCONNECTED
            } else sConnectionState!!
        }


        fun getLoggedInState(): NetworkConnection.LoggedInState {
            return if (sLoggedInState == null) {
                NetworkConnection.LoggedInState.LOGGED_OUT
            } else sLoggedInState!!
        }
    }





    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"onCreate")
    }


    private fun initConnection() {
        Log.d(TAG, "initConnection()")
        if (mConnection == null) {
            mConnection = NetworkConnection(this)
        }
        try {
            mConnection?.connect()

        } catch (e: IOException) {
            Log.d(TAG, "Something went wrong while connecting ,make sure the credentials are right and try again")
            e.printStackTrace()
            //Stop the service all together.
            stopSelf()
        } catch (e: SmackException) {
            Log.d(TAG, "Something went wrong while connecting ,make sure the credentials are right and try again")
            e.printStackTrace()
            stopSelf()
        } catch (e: XMPPException) {
            Log.d(TAG, "Something went wrong while connecting ,make sure the credentials are right and try again")
            e.printStackTrace()
            stopSelf()
        }

    }


   private fun start() {
        Log.d(TAG, " Service Start() function called.")
        if (!mActive) {
            mActive = true
            if (mThread == null || !mThread?.isAlive!!) {
                mThread = Thread(Runnable {
                    Looper.prepare()
                    mTHandler = Handler()
                    initConnection()
                    //THE CODE HERE RUNS IN A BACKGROUND THREAD.
                    Looper.loop()
                })
                mThread?.start()
            }


        }


    }

  private fun stop() {
        Log.d(TAG,"stop()")
        mActive = false;
        mTHandler?.post(object :Runnable {
            override fun run() {
                if (mConnection!=null){
                    mConnection?.disconnect()
                }
            }
        })

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStartCommand()")
        start()
        return Service.START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG,"onDestroy()")
        super.onDestroy()
        stop()
    }
}