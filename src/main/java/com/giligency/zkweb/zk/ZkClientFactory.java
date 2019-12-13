package com.giligency.zkweb.zk;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ZkClientFactory {
    private static Map<String, ZookeeperClient> map = new ConcurrentHashMap<>();
    private static Set<ZookeeperClusterInfo> infos = new HashSet<>();
    private static boolean isInit = false;

    private ZkClientFactory() {

    }

    public static Set<ZookeeperClusterInfo> getInfos() {
        return infos;
    }

    public static void removeZookeeperClient(String zkAddress) {
        Optional.ofNullable(map.get(zkAddress)).ifPresent(ZookeeperClient::close);
        infos.removeIf(V -> V.getZkAddress().equals(zkAddress));
        map.remove(zkAddress);
    }

    /**
     * 创建并连接对应zk
     *
     * @param info 对应的zk
     * @return 处于连接的客户端
     */
    public static ZookeeperClient buildZkClient(ZookeeperClusterInfo info) {
        ZookeeperClient zkClient = map.get(info.getZkAddress());
        if (zkClient == null) {
            OfficialZookeeperClient officialZookeeperClient = new OfficialZookeeperClient(info);
            officialZookeeperClient.connect2Server();
            map.put(info.getZkAddress(), officialZookeeperClient);
            infos.add(info);
            return officialZookeeperClient;
        } else {
            if (zkClient.getZookeeperClusterInfo().equals(info)) {
                return zkClient;
            } else {
                zkClient.close();
                OfficialZookeeperClient officialZookeeperClient = new OfficialZookeeperClient(info);
                map.put(info.getZkAddress(), officialZookeeperClient);
                infos.add(info);
                officialZookeeperClient.connect2Server();
                return officialZookeeperClient;
            }
        }
    }

    public static ZookeeperClient getZkClient(String zkAddress) {
        return Optional.ofNullable(map.get(zkAddress)).orElse(buildZkClient(new ZookeeperClusterInfo(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now()), zkAddress)));
    }

    public static void initZookeeperClient(List<ZookeeperClusterInfo> zookeeperClusterInfoList) {
        if (!isInit) {
            infos.addAll(zookeeperClusterInfoList);
            isInit = true;
        } else {
            log.error("ZookeeperClient already init!");
        }
    }

}
