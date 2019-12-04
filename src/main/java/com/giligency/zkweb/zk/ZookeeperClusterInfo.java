package com.giligency.zkweb.zk;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ZookeeperClusterInfo implements Serializable {
    //like 129.0.0.1:2181,129.0.0.2:2181
    private String address;

    private int timeout;

    private int sessionExpireMs;

    private String username;

    private String password;

    public ZookeeperClusterInfo(String address) {
        this.address = address;

    }

    public ZookeeperClusterInfo(String address, int timeout, int sessionExpireMs) {
        this.address = address;
        this.timeout = timeout;
        this.sessionExpireMs = sessionExpireMs;
    }

    public ZookeeperClusterInfo(String address, String username, String password) {
        this.address = address;
        this.username = username;
        this.password = password;
    }

    public String getAuthority() {
        if (StringUtils.isBlank(username) && StringUtils.isBlank(password)) {
            return null;
        }
        return (username == null ? "" : username)
                + ":" + (password == null ? "" : password);
    }

    public int getTimeout(int defaultValue) {
        if (timeout == 0 | timeout < 0) {
            return defaultValue;
        }
        return timeout;
    }

    public int getSessionExpiresMs(int defaultValue) {
        if (sessionExpireMs == 0 | sessionExpireMs < 0) {
            return sessionExpireMs;
        }
        return defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZookeeperClusterInfo that = (ZookeeperClusterInfo) o;
        if (that.hashCode() != this.hashCode()) {
            return false;
        }
        return timeout == that.timeout &&
                sessionExpireMs == that.sessionExpireMs &&
                address == null ? null == that.address : address.equals(that.address) &&
                username == null ? null == that.username : username.equals(that.username) &&
                password == null ? null == that.password : password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, timeout, sessionExpireMs, username, password);
    }
}
