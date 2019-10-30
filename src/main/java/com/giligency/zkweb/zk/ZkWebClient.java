package com.giligency.zkweb.zk;

import com.giligency.zkweb.entity.ZNodeDTO;
import com.giligency.zkweb.entity.ZNodeInfoDO;
import com.giligency.zkweb.exception.NerException;
import com.giligency.zkweb.util.mapper.BeanMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * zkweb
 */
@Slf4j
@NoArgsConstructor
public class ZkWebClient implements Watcher {
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private String zkServerList;
    private int defaultServiceTimeout;
    private ZooKeeper zooKeeper;
    private String zkAuthInfo;
    private Stat stat = new Stat();

    /**
     * 标准使用的构造器，传入zk集群的String串，传入认证信息（该认证信息只会用户zk初始化连接时使用）和超时时间
     *
     * @param zkServerList   zk集群的String串
     * @param zkAuthInfo     认证信息（digest认证）
     * @param serviceTimeout 超时时间
     */
    public ZkWebClient(String zkServerList, String zkAuthInfo, int serviceTimeout) {
        if (StringUtils.isEmpty(zkServerList)) {
            return;
        }
        this.zkServerList = zkServerList;
        this.zkAuthInfo = zkAuthInfo;
        if (serviceTimeout == 0) this.defaultServiceTimeout = 3000;
        else this.defaultServiceTimeout = serviceTimeout;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        final Event.KeeperState state = watchedEvent.getState();
        if (state == Event.KeeperState.SyncConnected) {
            countDownLatch.countDown();
        } else if (state == Event.KeeperState.Disconnected) {
            try {
                connect();
            } catch (IOException | InterruptedException e) {
                log.error("zk重连失败", e);
                NerException.throwException("重新连接zk集群" + zkServerList + "失败！！！", e);
            }
        }
    }

    public ZkWebClient updateInfo(String zkServerList, String zkAuthInfo, int serviceTimeout) {

        if ((!zkServerList.equals(this.zkServerList) || !zkAuthInfo.equals(this.zkAuthInfo) || this.defaultServiceTimeout != (serviceTimeout == 0 ? 3000 : serviceTimeout))) {
            close();
            this.zkServerList = zkServerList;
            this.zkAuthInfo = zkAuthInfo;
            this.defaultServiceTimeout = (serviceTimeout == 0 ? 3000 : serviceTimeout);
            try {
                this.zooKeeper = new ZooKeeper(this.zkServerList, this.defaultServiceTimeout, this);
            } catch (IOException e) {
                NerException.throwException("根据zk集群地址" + zkServerList + "无法连接zookeeper", e);
            }
        }
        return this;
    }
    /**
     * 获取指定路径下的子节点
     *
     * @param path 指定路径
     * @return 子节点的String集合
     * @throws KeeperException      KeeperException
     * @throws InterruptedException InterruptedException
     */
    public List<String> getChildrenWithWatcher(String path) throws KeeperException, InterruptedException {

        return zooKeeper.getChildren(path, true);
    }

    /**
     * 初始化zk连接，请在创建本类实例后，先行执行该方法，再调用对应方法操作zk
     *
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public void connect() throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper(zkServerList, defaultServiceTimeout, this);
        if (!StringUtils.isEmpty(zkAuthInfo)) {
            zooKeeper.addAuthInfo("digest", zkAuthInfo.getBytes());
        }
        countDownLatch.await();
    }

    /**
     * 同步创建zk节点，如果父节点不存在会出现父节点不存在的异常
     *
     * @param path    创建的路径
     * @param context 节点内容
     * @throws KeeperException      KeeperException
     * @throws InterruptedException InterruptedException
     */
    public void createOpenACLPersistentNodeSync(String path, String context) throws KeeperException, InterruptedException {
        if (zooKeeper.exists(path, true) == null) {
            log.error("new zkPath :{}", path);
            zooKeeper.create(path, context == null ? null : context.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } else {
            throw new RuntimeException("路径" + path + "已经存在！！！！");
        }
    }

    public void getDataSync(String path) throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        // getData的返回值是该节点的数据值，节点的状态信息会赋值给stat对象
        byte[] data = zooKeeper.getData(path, true, stat);
        log.info("获取路径节点{}信息为{}", path, new String(data));
    }

    public ZNodeDTO getTargetZKNodeInfo(String absolutePath) throws KeeperException, InterruptedException {
        final Stat exists = zooKeeper.exists(absolutePath, true);
        if (exists != null) {
            final ZNodeDTO zNodeDTO = new ZNodeDTO();
            zNodeDTO.setAbsolutePath(absolutePath);
            if (absolutePath.equals("/")) {
                zNodeDTO.setNodeName("/");
                zNodeDTO.setParentName("/");
            } else {
                final String[] split = absolutePath.split("/");
                zNodeDTO.setNodeName(split[split.length - 1]);
                zNodeDTO.setParentName(split[split.length - 2]);
            }
            final ZNodeInfoDO zNodeInfoDO = BeanMapper.map(exists, ZNodeInfoDO.class);
            zNodeInfoDO.setPath(absolutePath);
            //zNodeInfoVO.setParams(zk.get);
            final byte[] data = zooKeeper.getData(absolutePath, true, stat);
            if (Objects.nonNull(data) && data.length != 0) {
                zNodeInfoDO.setData(new String(data));
            } else {
                zNodeInfoDO.setData("");
            }
            final List<ACL> acl = zooKeeper.getACL(absolutePath, stat);
            zNodeInfoDO.setAclList(acl);
            zNodeDTO.setZNodeInfoDO(zNodeInfoDO);
            final List<String> children = zooKeeper.getChildren(absolutePath, false);
            zNodeDTO.setChildrenName(children);
            return zNodeDTO;
        }
        return null;
    }

    /**
     * 资源释放
     */
    public void close() {
        if (null != zooKeeper) {
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            } finally {
                zooKeeper = null;
            }
        }
    }

    /**
     * 官方的删除指定节点，如果存在
     * @param path 指定路径
     * @param version 节点版本
     * @throws KeeperException
     *  KeeperException.NotEmpty 该节点非空
     *  KeeperException.BadVersion 错误，不存在的版本
     *  KeeperException.NoNode 节点不存在
     * @throws InterruptedException InterruptedException
     */
    public void deleteTargetPathNode(String path,int version) throws KeeperException, InterruptedException {
        zooKeeper.delete(path,version);
    }


    /**
     * 删除节点，不关心版本，删除该节点
     * @param path 指定路径
     * @throws KeeperException
     *  KeeperException.NotEmpty 该节点非空
     *  KeeperException.BadVersion 错误，不存在的版本
     *  KeeperException.NoNode 节点不存在
     * @throws InterruptedException InterruptedException
     */
    public void deleteTargetPathNodeWhateverWhichVersion(String path) throws KeeperException, InterruptedException {
        deleteTargetPathNode(path, -1);
    }
    /**
     * 递归的创建节点（即没有父节点时自动创建没有内容的父节点）
     *
     * @param path    需要创建的节点路径
     * @param context 该节点下的数据内容
     */
    public void createOpenACLPersistentRecursionNodeSync(String path, String context) throws KeeperException, InterruptedException {
        checkParentNodeAndCreateTargetNodeIfItNotExistWithOpenACLAndPersistentState(path);
        log.error("开始创建目标节点{}内容{}",path,context);
        if (zooKeeper.exists(path,true) == null) {
            zooKeeper.create(path, context == null ? null : context.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    /**
     * 递归创建目标路径的父路径
     * /test/node1/node11/node111,
     * 会检查/test /test/node1 /test/node1/node11 三个父节点是否存在，不存在时直接创建无内容的持久话，开放权限的节点
     * @param path 目标路径
     * @throws KeeperException KeeperException
     * @throws InterruptedException InterruptedException
     */
    private void checkParentNodeAndCreateTargetNodeIfItNotExistWithOpenACLAndPersistentState(String path) throws KeeperException, InterruptedException {
        //获取父节点
        String parentPath = path.substring(0, path.lastIndexOf("/"));
        //判断父节点是否存在，存在则先创建，再创建子节点
        if (parentPath.length() > 0) {
            checkParentNodeAndCreateTargetNodeIfItNotExistWithOpenACLAndPersistentState(parentPath);
            log.error("监测节点：：：{}是否存在",parentPath);
            if (zooKeeper.exists(parentPath,true) == null) {
                zooKeeper.create(parentPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }
    }
}
