package com.patent.socket.client.helper.stickpackage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * 最简单的做法，不处理粘包，直接读取返回，最大长度maxLen
 */
public class BaseStickPackageHelper implements AbsStickPackageHelper {
    private static final String TAG = BaseStickPackageHelper.class.getName();
    private int maxLen = 2048;//最大长度
    private int len;
    private volatile byte[] bytes;
    private volatile boolean isRead = false;
//    private byte[] result;

    public BaseStickPackageHelper() {
    }

    public BaseStickPackageHelper(int maxLen) {
        if (maxLen > 0) {
            this.maxLen = maxLen;
        }
    }

    /**
     * 该方法在while函数执行  synchronized
     * 不能不断创建数组
     * 只在初始化 or 成功接收完一次 后
     * 重新赋值数组
     * */
    public synchronized byte[] execute(InputStream is) {
        if (bytes == null)
            bytes = new byte[maxLen];
        if (isRead) {
            isRead = false;
            bytes = new byte[maxLen];
        }
        try {
            if ((len = is.read(bytes)) != -1) {
//                result = new byte[len];
//                for (int i = 0; i < result.length; i++) {
//                    result[i] = bytes[i];
//                }
//                Log.d(TAG, "result: " + Arrays.toString(bytes));
                isRead = true;
                return Arrays.copyOf(bytes, len);
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return null;
    }
}
