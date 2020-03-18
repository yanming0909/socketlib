package com.patent.socket.client.manager;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import com.patent.common.BaseModel;
import com.patent.common.DataUtils;
import com.patent.socket.client.helper.TcpConnConfig;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SocketManager extends BaseModel {
    private String TAG = SocketManager.class.getName();
    private String ip;
    private int port;
    private SocketCallback callback;
    private Handler mSocketConnectHandler;
    private HandlerThread mHanlderConnectThread;
    private Handler mSocketHandler;
    private HandlerThread mHanlderThread;
    private volatile boolean isConnected = false;//是否连接的标识

    protected TcpConnConfig mTcpConnConfig;

    //io流
    private  DataOutputStream out;
    private  DataInputStream in;
    private  Socket mSocket;

    //收发连接线程
//    private static Thread mReceivedThread;
    public boolean isConnected() {
        return isConnected&&mSocket!=null&&!mSocket.isClosed()&&mSocket.isConnected();
    }
    private Runnable mSocketReceiveRunnable = new Runnable() {
        @Override
        public void run() {
            while (isConnected&&callback != null) {
                try {
                    byte[] result = mTcpConnConfig.getStickPackageHelper().execute(in);//粘包处理
                    if (callback != null) {
                        if (result!=null&&result.length>0) {
                            callback.receive(result);
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG,e.getMessage());
                    e.printStackTrace();
                }
            }
            if (callback != null) {
                callback.disConnect();
            }
        }
    };

    private Runnable mSocketConnectRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "connect start");
            try {
                mSocket = new Socket(SocketManager.this.ip, SocketManager.this.port);
                in = new DataInputStream(mSocket.getInputStream());
                out = new DataOutputStream(mSocket.getOutputStream());
                isConnected = true;
                startReceiveThread();
                Log.d(TAG, "connect");
                if (callback != null) {
                    callback.connect();
                }
                isConnecting = false;
            } catch (Exception e) {
                e.printStackTrace();
                isConnecting = false;
                isConnected = false;
                Log.d(TAG, "connect failed");
                if (callback != null) {
                    callback.connectFailed();
                }
            }
        }
    };

    public SocketManager(Builder builder) {
        int category = builder.category;
        this.ip = builder.ip;
        this.port = builder.port;
        TAG += category;
        if (mTcpConnConfig == null) {
            mTcpConnConfig = new TcpConnConfig.Builder().create();
        }
        if (mHanlderThread == null) {
            mHanlderThread = new HandlerThread("SocketHandler"+category);
            mHanlderThread.start();
        }
        if (mSocketHandler == null) {
            mSocketHandler = new Handler(mHanlderThread.getLooper());
        }
        if (mHanlderConnectThread == null) {
            mHanlderConnectThread = new HandlerThread("mHanlderConnectThread"+category);
            mHanlderConnectThread.start();
        }
        if (mSocketConnectHandler == null) {
            mSocketConnectHandler = new Handler(mHanlderConnectThread.getLooper());
        }
    }

    public void resetAddress(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    //function__功能相关
    //释放io，线程资源
    public void release() {
        Log.d(TAG,"release");
        isConnected = false;
        if (mSocket != null) {
            try {
                if (!mSocket.isClosed()) {
                    if (mSocket.isInputShutdown()) {
                        mSocket.shutdownInput();
                    }
                    if (mSocket.isOutputShutdown()) {
                        mSocket.shutdownOutput();
                    }
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    mSocket.close();
                    mSocket = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private boolean isConnecting = false;
    //connect__连接
    public void connect() {
        if(isConnecting){
            return;
        }
        isConnecting = true;
        release();
        if (TextUtils.isEmpty(ip)||ip.equals("0.0.0.0")) return;
        if(mSocketConnectHandler!=null){
            mSocketConnectHandler.removeCallbacksAndMessages(null);
            mSocketConnectHandler.postDelayed(mSocketConnectRunnable,5000);
        }

    }

    private void startReceiveThread() {
        if (mSocketHandler != null) {
            mSocketHandler.removeCallbacksAndMessages(null);
            mSocketHandler.post(mSocketReceiveRunnable);
        }
    }

    //发送
    public synchronized void send(final byte[] bytes, final SendResult sendResult) {
        Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> emitter) throws Exception {
                if (out != null && bytes != null) {
                    try {
                        out.write(bytes);
                        out.flush();
                        Log.d(TAG, "send success : " + DataUtils.ByteArrToHex(bytes));
                        if (callback != null) {
                            callback.sendSuccessed(bytes);
                        }
                        if (sendResult != null) {
                            sendResult.successed();
                        }
                    } catch (Exception e) { //管道可能阻塞  保存数据重连socket
                        Log.d(TAG, "failed: " + e);
                        if (callback != null) {
                            callback.sendFailed(bytes);
                        }
                        if (sendResult != null) {
                            sendResult.failed(bytes);
                        }
                    }
                }
                emitter.onNext(bytes);
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSafeObserver<byte[]>() {
                    @Override
                    protected void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onNext(byte[] bytes) {
                        super.onNext(bytes);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                });
    }

    public interface SendResult {
        void successed();

        void failed(byte[] data);
    }

    //监听
    public void setOnSocketCallback(SocketCallback callback) {
        this.callback = callback;
    }

    public void removeSocketCallback() {
        this.callback = null;
    }

    public interface SocketCallback {
        void connect();

        void disConnect();

        void sendSuccessed(byte[] bytes);

        void sendFailed(byte[] bytes);

        void receive(byte[] bytes);

        void connectFailed();
    }

    public static class Builder {
        private String ip;
        private int port;
        private int category;

        public Builder(int category) {
            this.category = category;
        }

        public int getPort() {
            return port;
        }

        public String getIp() {
            return ip;
        }

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public SocketManager build() {
            if (TextUtils.isEmpty(this.ip)) {
                throw new IllegalArgumentException("ip error");
            }
            return new SocketManager(Builder.this);
        }
    }
}
