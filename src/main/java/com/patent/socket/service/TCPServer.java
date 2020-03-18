package com.patent.socket.service;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;


public abstract class TCPServer implements TCPRunnable.OnTCPReceiveListener{
    private static final String TAG = TCPServer.class.getName();
    private TCPRunnable tcpRunnable;
    private Handler mTCPHandler;
    private HandlerThread mHanlderTCPThread;
    private int reConnectCount;
    public TCPServer(String tag) {
        if (mHanlderTCPThread == null) {
            mHanlderTCPThread = new HandlerThread(tag);
            mHanlderTCPThread.start();
        }

        if (mTCPHandler == null) {
            mTCPHandler = new Handler(mHanlderTCPThread.getLooper());
        }
    }

    public void start(int port) {
        release();
        reConnectCount ++;
        if (reConnectCount >= 10  && port != 8953)
            return;
        tcpRunnable = new TCPRunnable(port);
        if (mTCPHandler!=null) {
            mTCPHandler.removeCallbacksAndMessages(null);
            mTCPHandler.postDelayed(tcpRunnable,6000);
        }
        tcpRunnable.setOnTCPReceiveListener(this);
    }
   public void release(){
        if (tcpRunnable!=null){
            tcpRunnable.removeOnTCPReceiveListener();
            tcpRunnable.closeSelf();
            tcpRunnable = null;
        }
       if (mTCPHandler!=null) {
           mTCPHandler.removeCallbacksAndMessages(null);
       }
       Log.d(TAG,"release");
    }
    public TCPRunnable getTcpRunnable() {
        return tcpRunnable;
    }
}
