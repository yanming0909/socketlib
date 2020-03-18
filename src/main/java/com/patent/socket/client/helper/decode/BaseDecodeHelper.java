package com.patent.socket.client.helper.decode;

import com.patent.socket.client.helper.TargetInfo;
import com.patent.socket.client.helper.TcpConnConfig;


public class BaseDecodeHelper implements AbsDecodeHelper {
    @Override
    public byte[][] execute(byte[] data, TargetInfo targetInfo, TcpConnConfig tcpConnConfig) {
        return new byte[][]{data};
    }
}
