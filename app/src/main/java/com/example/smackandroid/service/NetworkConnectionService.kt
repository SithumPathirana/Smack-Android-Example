package com.example.smackandroid.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.os.Handler
import android.os.Looper



class NetworkConnectionService:Service(){

    private var mActive: Boolean = false//Stores whether or not the thread is active
    lateinit var mThread: Thread
    lateinit var mTHandler: Handler//We use this handler to post messages to
    //the background thread.

    private val TAG="NetworkConnection"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"onCreate")
    }


   private fun start() {
        Log.d(TAG, " Service Start() function called.")
        if (!mActive) {
            mActive = true
            if (mThread == null || !mThread.isAlive) {
                mThread = Thread(Runnable {
                    Looper.prepare()
                    mTHandler = Handler()
                    //initConnection();
                    //THE CODE HERE RUNS IN A BACKGROUND THREAD.
                    Looper.loop()
                })
                mThread.start()
            }


        }


    }

  private fun stop() {
        Log.d(TAG,"stop()")
        mActive = false;
        mTHandler.post(object :Runnable {
            override fun run() {

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