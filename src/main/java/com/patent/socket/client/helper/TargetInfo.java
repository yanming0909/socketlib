package com.patent.socket.client.helper;

import android.support.annotation.NonNull;

import com.patent.common.StringValidationUtils;

/**
 *
 */
public class TargetInfo {
    private String ip;
    private int port;

    public TargetInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
        check();
    }

    private void check() {
        if (!StringValidationUtils.validateRegex(port + "", StringValidationUtils.RegexPort)) {
            throw new IllegalStateException("port 格式不合法");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TargetInfo that = (TargetInfo) o;

        if (port != that.port) return false;
        return ip != null ? ip.equals(that.ip) : that.ip == null;

    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "TargetInfo{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                '}';
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
