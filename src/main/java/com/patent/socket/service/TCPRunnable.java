package com.patent.socket.service;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPRunnable implements Runnable{
    private String TAG = TCPRunnable.class.getName();
    private int port = 8953;
    private boolean isListen = true;   //线程监听标志位
    private Handler mTCPHandler;
    private HandlerThread mHanlderThread;
    private ServerSocketRunnable serverSocketRunnable;
    public TCPRunnable(int port){
        isListen = true ;
        this.port = port;
        TAG += port;
        if (mHanlderThread == null) {
            mHanlderThread = new HandlerThread("TCPHandler"+port);
            mHanlderThread.start();
        }
        if (mTCPHandler == null) {
            mTCPHandler = new Handler(mHanlderThread.getLooper());
        }
    }

    //更改监听标志位
    public void setIsListen(boolean b){
        isListen = b;
    }
    boolean isConnect(){
        return serverSocketRunnable!=null;
//        return serverSocketRunnable!=null&&serverSocketRunnable.isRun
//                && !serverSocketRunnable.socket.isClosed() && !serverSocketRunnable.socket.isInputShutdown();
    }
    public void send(byte[] data, String tag){
       if (!isConnect())return;
       serverSocketRunnable.send(tag,data);
    }
    public void closeSelf(){
        isListen = false;
//        for (ServerSocketThread s : SST){
//            s.isRun = false;
//        }
        if (serverSocketRunnable!=null){
            serverSocketRunnable.isRun = false;
        }
    }

    private Socket getSocket(ServerSocket serverSocket){
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
            return null;
        }
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(30000);
            while (isListen){
                Log.i(TAG, "run: startListening...");
                if (onTCPReceiveListener != null)
                    onTCPReceiveListener.onStartListening();
                Socket socket = getSocket(serverSocket);
                if (socket != null){
                    if (serverSocketRunnable != null){
                        serverSocketRunnable.close();
                        serverSocketRunnable = null;
                    }
                    serverSocketRunnable = new ServerSocketRunnable(socket);
                    if (mTCPHandler != null){
                        mTCPHandler.removeCallbacksAndMessages(null);
                        mTCPHandler.post(serverSocketRunnable);
                    }
                }
            }
            if (onTCPReceiveListener != null)
                onTCPReceiveListener.onStopListening();
            serverSocket.close();
        } catch (Exception e) {
            closeSelf();
            Log.d(TAG,e.getMessage());
            if (onTCPReceiveListener != null)
                onTCPReceiveListener.onStartFail();
            e.printStackTrace();
        }
    }

    public class ServerSocketRunnable implements Runnable{
        Socket socket;
        private InputStream is = null;
        private OutputStream os = null;
        private String ip = null;
        private boolean isRun = true;

        ServerSocketRunnable(Socket socket){
            this.socket = socket;
            ip = socket.getInetAddress().toString();
            Log.i(TAG, "ServerSocketRunnable connect,ip:" + ip);
            if (onTCPReceiveListener != null)
                onTCPReceiveListener.onConnect(ip+":"+port);
            try {
                socket.setSoTimeout(30000);
                os = socket.getOutputStream();
                is = socket.getInputStream();
            } catch (IOException e) {
                if (onTCPReceiveListener != null)
                    onTCPReceiveListener.onConnectFail();
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte buff[]  = new byte[980];
            int rcvLen;
            while (isRun && !socket.isClosed() && !socket.isInputShutdown()){
                try {
                    if (is != null && (rcvLen = is.read(buff)) != -1){
//                        Log.d(TAG, "run:receiver: " +rcvLen);
                        if (onTCPReceiveListener != null)
                            onTCPReceiveListener.onDataReceive(buff,rcvLen);
                    }
                } catch (IOException e) {
                    Log.d(TAG,"run error "+e.getMessage());
                    e.printStackTrace();
                    isRun = false;
                }
            }
            close();
        }
    public void send(String tag,byte[] msg){
        try {
            if (os==null) {
                if (onTCPReceiveListener != null)
                    onTCPReceiveListener.onSendFail(tag,msg);
                return;
            }
            os.write(msg);
            os.flush(); //强制送出数据
            if (onTCPReceiveListener != null)
                onTCPReceiveListener.onSendSuccess(tag,msg);
        } catch (IOException e) {
            if (onTCPReceiveListener != null)
                onTCPReceiveListener.onSendFail(tag,msg);
            e.printStackTrace();
        }
    }
        private void close(){
            try {
                if (socket != null)socket.close();
                if (is != null)is.close();
                if (os != null)os.close();
                Log.i(TAG, "run: close");
                if (onTCPReceiveListener != null)
                    onTCPReceiveListener.onDisconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * socket response data listener
     * */
    private OnTCPReceiveListener onTCPReceiveListener = null;

    /**
     * 线程中
     */
    public interface OnTCPReceiveListener {
        public void onStartListening();
        public void onStopListening();
        public void onConnect(String ip);
        public void onConnectFail();
        public void onStartFail();
        public void onSendFail(String tag, byte[] msg);
        public void onSendSuccess(String tag, byte[] msg);
        public void onDisconnect();
        public void onDataReceive(byte[] buffer, int size);
    }
    public void setOnTCPReceiveListener(OnTCPReceiveListener dataReceiveListener) {
        onTCPReceiveListener = dataReceiveListener;
    }
    public void removeOnTCPReceiveListener() {
        onTCPReceiveListener = null;
    }
}
