package com.giligency.zkweb.zk;


import com.giligency.zkweb.exception.NerException;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;


@Data
public class ZookeeperClusterInfo implements Serializable {
    private String name;
    //like 129.0.0.1:2181,129.0.0.2:2181
    private String zkAddress;

    private int timeout;

    private int sessionExpireMs;

    private String authority;

    private String description;

    private Date createTime;

    private ZookeeperClusterInfo() {

    }
    public ZookeeperClusterInfo(String name, String zkAddress) {
        init(name, zkAddress, 0, 0, null, null);

    }

    public ZookeeperClusterInfo(String name, String zkAddress, int timeout, int sessionExpireMs) {
        init(name, zkAddress, timeout, sessionExpireMs, null, null);


    }

    public ZookeeperClusterInfo(String name, String zkAddress, int timeout, int sessionExpireMs, String authority, String description) {
        init(name, zkAddress, timeout, sessionExpireMs, authority, description);
    }

    public ZookeeperClusterInfo(String name, String zkAddress, String authority) {
        init(name, zkAddress, timeout, sessionExpireMs, authority, null);
    }

    private void init(String name, String zkAddress, int timeout, int sessionExpireMs, String authority, String description) {
        if (StringUtils.isBlank(zkAddress)) {
            NerException.throwException("zkaddress不能为空！！！");
        }
        this.name = Optional.ofNullable(name).orElse("");
        this.timeout = (timeout <= 0 ? 30000 : timeout);
        this.sessionExpireMs = (sessionExpireMs <= 0 ? 30000 : sessionExpireMs);
        this.authority = Optional.ofNullable(authority).orElse("");
        this.description = Optional.ofNullable(description).orElse("");
        this.zkAddress = zkAddress;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {

            return true;
        }
        if (o == null || getClass() != o.getClass()) {

            return false;
        }
        ZookeeperClusterInfo that = (ZookeeperClusterInfo) o;
        if (that.hashCode() != this.hashCode()) {
            return false;
        }
        return timeout == that.timeout &&
                sessionExpireMs == that.sessionExpireMs &&
                zkAddress == null ? null == that.zkAddress : zkAddress.equals(that.zkAddress) &&
                authority == null ? null == that.authority : authority.equals(that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zkAddress, timeout, sessionExpireMs, authority);
    }
}
