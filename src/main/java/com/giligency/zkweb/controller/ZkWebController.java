package com.giligency.zkweb.controller;

import com.giligency.zkweb.entity.RetJson;
import com.giligency.zkweb.entity.ZNodeDTO;
import com.giligency.zkweb.zk.ZkClientFactory;
import com.giligency.zkweb.zk.ZkWebClient;
import org.apache.zookeeper.KeeperException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZkWebController {


    @GetMapping("/zookeeper/node/getTargetNodeChildren")
    public RetJson getTargetNodeChildren(String zkServerList, String path) {

        final ZkWebClient zkWebClient = ZkClientFactory.buildZkClient(zkServerList, null);
        try {
            final ZNodeDTO targetZKNodeInfo = zkWebClient.getTargetZKNodeInfo(path);
            return RetJson.returnSuccess("", targetZKNodeInfo);
        } catch (KeeperException | InterruptedException e) {
            return RetJson.returnFailure(e.getMessage());
        }

    }
}
