package com.giligency.zkweb.zk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ZkClientFactory {
    private static Map<String, ZkWebClient> map = new ConcurrentHashMap<>();

    private ZkClientFactory() {

    }

    public static ZkWebClient buildZkClient(String zkServerList, String zkAuthInfo, int timeout) {
        if (map.containsKey(zkServerList)) {
            final ZkWebClient zkWebClient = map.get(zkServerList);
            zkWebClient.updateInfo(zkServerList, zkAuthInfo, timeout);
            return zkWebClient;
        } else {
            final ZkWebClient zkWebClient = new ZkWebClient(zkServerList, zkAuthInfo, timeout);
            map.put(zkServerList, zkWebClient);
            return zkWebClient;
        }

    }

    public static ZkWebClient buildZkClient(String zkServerList, String zkAuthInfo) {
        return buildZkClient(zkServerList, zkAuthInfo, 0);
    }

}
