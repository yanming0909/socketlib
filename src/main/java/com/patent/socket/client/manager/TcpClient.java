package com.patent.socket.client.manager;

import android.util.Log;

/**
 * TCP Socket客户端
 */
public abstract class TcpClient {

    private  String TAG = TcpClient.class.getName();
    private final String ip;
    private final int port;
    public  boolean connect = false;
    private final int category;//客户端标识
    private SocketManager socketManager;

    public TcpClient(String ip,int port,int category) {
        this.ip = ip;
        this.port = port;
        this.category = category;
        TAG += category;
    }

    /**
     * 建立连接
     * <p>
     * 连接的建立将在新线程中进行
     * <p>
     * 连接建立成功，回调{@code onConnect()}
     * <p>
     * 连接建立失败，回调{@code onConnectFailed()}
     */
    public void connect() {
        connect = false;
        run();
    }

    private SocketManager.SocketCallback socketCallback = new SocketManager.SocketCallback() {
        @Override
        public void connect() {
            Log.d(TAG,"connect");
            connect = true;
            onConnect(socketManager);
        }

        @Override
        public void disConnect() {
            Log.d(TAG,"disConnect");
            connect = false;
            onDisconnect(socketManager);
        }

        @Override
        public void sendSuccessed(byte[] bytes) {
            Log.d(TAG,"sendSuccess");

        }

        @Override
        public void sendFailed(byte[] bytes) {
            Log.d(TAG,"sendFailed");
//            SocketService.willSend = bytes;
//            TcpClient.this.connect();
        }

        @Override
        public void receive(byte[] bytes) {
            onReceive(socketManager, bytes);
        }

        @Override
        public void connectFailed() {
            connect = false;
            Log.d(TAG,"connectFailed");
            onConnectFailed();
        }
    };

    public void run() {
        try {
            if (socketManager != null) {
                socketManager.removeSocketCallback();
            }
            if (socketManager == null) {
                SocketManager.Builder builder = new SocketManager.Builder(category);
                socketManager = builder.setIp(ip).setPort(port).build();
            } else {
                socketManager.resetAddress(ip, port);
            }
            socketManager.setOnSocketCallback(socketCallback);
            socketManager.connect();
        } catch (Exception e) {
            e.printStackTrace();
            onConnectFailed();
        }
    }

    /**
     * 断开连接
     * <p>
     * 连接断开，回调{@code onDisconnect()}
     */
    public void disconnect() {
        Log.d(TAG,"disconnect");
        if (socketManager != null) {
            socketManager.removeSocketCallback();
            socketManager.release();
            socketManager=null;
        }
    }

    /**
     * 判断是否连接
     *
     * @return 当前处于连接状态，则返回true
     */
    public boolean isConnected() {
        return connect&&socketManager!=null&&socketManager.isConnected();
    }

    /**
     * 获取Socket收发器
     *
     * @return 未连接则返回null
     */
    public SocketManager getTransceiver() {
        return isConnected() ? socketManager : null;
    }

    /**
     * 连接建立
     *
     * @param transceiver SocketManager对象
     */
    public abstract void onConnect(SocketManager transceiver);

    /**
     * 连接建立失败
     */
    public abstract void onConnectFailed();

    /**
     * 接收到数据
     * <p>
     * 注意：此回调是在新线程中执行的
     *
     * @param transceiver SocketManager对象
     * @param data        字符串
     */
    public abstract void onReceive(SocketManager transceiver, byte[] data);

    /**
     * 连接断开
     * <p>
     * 注意：此回调是在新线程中执行的
     *
     * @param transceiver SocketManager对象
     */
    public abstract void onDisconnect(SocketManager transceiver);
}
