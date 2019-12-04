package com.giligency.zkweb.zk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ZkClientFactory {
    private static Map<String, ZookeeperClient> map = new ConcurrentHashMap<>();

    private ZkClientFactory() {

    }

    public static ZookeeperClient buildZkClient(ZookeeperClusterInfo info) {
        ZookeeperClient zkClient = map.get(info.getAddress());
        if (zkClient == null) {
            OfficialZookeeperClient officialZookeeperClient = new OfficialZookeeperClient(info);
            map.put(info.getAddress(), officialZookeeperClient);
            return officialZookeeperClient;
        } else {
            if (zkClient.getZookeeperClusterInfo().equals(info)) {
                return zkClient;
            } else {
                zkClient.close();
                OfficialZookeeperClient officialZookeeperClient = new OfficialZookeeperClient(info);
                map.put(info.getAddress(), officialZookeeperClient);
                return officialZookeeperClient;
            }
        }
    }

}
