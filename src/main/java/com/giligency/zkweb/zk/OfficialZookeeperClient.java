package com.giligency.zkweb.zk;

import com.giligency.zkweb.exception.NerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * 基于
 * <dependency>
 * <groupId>org.apache.zookeeper</groupId>
 * <artifactId>zookeeper</artifactId>
 * <version>3.5.6</version>
 * </dependency>
 */
@Slf4j
public class OfficialZookeeperClient extends AbstractZookeeperClient implements Watcher {

    static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Stat stat = new Stat();
    private final ZookeeperClusterInfo info;
    private ZooKeeper zk;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public OfficialZookeeperClient(ZookeeperClusterInfo info) {
        super(info);
        this.info = info;
        int timeout = info.getTimeout();
        int sessionTimeoutMs = info.getSessionExpiresMs();
        connect2Server(info, timeout);
    }

    private void connect2Server(ZookeeperClusterInfo info, int timeout) {
        try {
            zk = new ZooKeeper(info.getZkAddress(), timeout, this);
            String authority = info.getAuthority();
            if (StringUtils.isNotEmpty(authority)) {
                zk.addAuthInfo("digest", authority.getBytes());
            }
            countDownLatch.await();
        } catch (IOException | InterruptedException e) {
            NerException.throwException("连接" + info.getZkAddress() + "失败！！！", e);
        }
    }

    @Override
    public boolean checkExists(String path) {

        try {
            Stat exists = zk.exists(path, false);
            return exists != null;
        } catch (KeeperException | InterruptedException e) {
            NerException.throwException("判断节点" + path + "是否存在时出现异常", e);
        }
        return false;
    }

    @Override
    protected void deletePath(String path) {
        try {
            zk.delete(path, -1);
        } catch (KeeperException | InterruptedException e) {
            NerException.throwException("删除" + path + "出现异常！！", e);
        }
    }

    @Override
    protected void createEphemeral(String path) {
        try {
            zk.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (KeeperException | InterruptedException e) {
            NerException.throwException("创建节点异常！！！");
        }
    }

    @Override
    protected void createPersistent(String path, String data) {
        try {
            zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException | InterruptedException e) {
            NerException.throwException("创建节点异常！！！");
        }
    }

    @Override
    protected void createPersistent(String path) {
        try {
            if (!checkExists(path)) {
                zk.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

            }
        } catch (KeeperException | InterruptedException e) {
            NerException.throwException("创建节点异常！！！", e);
        }
    }

    @Override
    protected void doClose() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            NerException.throwException("关闭zk失败", e);
        }
    }

    @Override
    protected void createEphemeral(String path, String data) {
        try {
            zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (KeeperException | InterruptedException e) {
            NerException.throwException("创建节点异常！！！");
        }
    }

    @Override
    public List<String> getChildren(String path) {
        try {
            return zk.getChildren(path, this);
        } catch (KeeperException | InterruptedException e) {
            NerException.throwException("获取" + path + "子节点集出现异常", e);
        }
        return null;
    }

    @Override
    public List<String> addChildListener(String path, ChildListener listener) {
        return null;
    }

    @Override
    public void addDataListener(String path, DataListener listener) {

    }

    @Override
    public String doGetContent(String path) {

        try {
            byte[] data = zk.getData(path, false, stat);
            return (data == null || data.length == 0) ? null : new String(data, CHARSET);
        } catch (KeeperException | InterruptedException e) {
            NerException.throwException("获取数据出现异常！", e);
        }
        return null;
    }

    /**
     * 在这个监听事件中，ZK的状态和发生的事件类型是联系在一起的，
     * 虽然逻辑上，应该是状态 -> 事件，
     * 但是实际上，将所有的状态改变都归为了事件类型为NONE的事件。
     *
     * @param event 发生事件
     */
    @Override
    public void process(WatchedEvent event) {
        final Event.KeeperState state = event.getState();
        String path = event.getPath();
        Event.EventType type = event.getType();

        if (type == Event.EventType.None) {
            if (state == Event.KeeperState.SyncConnected) {
                countDownLatch.countDown();
                log.error("连接事件！！！");
            } else if (state == Event.KeeperState.Disconnected) {
                log.error("失去连接事件！！！");
            } else if (state == Event.KeeperState.Expired) {
                //发生了这个时间需要去重新建立zookeeper客户端
                countDownLatch = new CountDownLatch(1);
                int timeout = info.getTimeout();
                int sessionTimeoutMs = info.getSessionExpiresMs();
                connect2Server(info, timeout);
            } else if (state == Event.KeeperState.AuthFailed) {
                NerException.throwException("zookeeper认证失败！请检查对应的认证信息！！！");
            }
        }
        log.debug("当前zookeeper状态为{}发生的事件类型为：{},产生事件的路径为：{}", state, type.toString(), path);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfficialZookeeperClient that = (OfficialZookeeperClient) o;
        if (that.hashCode() != o.hashCode()) {
            return false;
        }
        return Objects.equals(zk, that.zk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zk);
    }
}
