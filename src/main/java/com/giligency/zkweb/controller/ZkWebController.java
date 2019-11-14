package com.giligency.zkweb.controller;

import com.giligency.zkweb.entity.RetJson;
import com.giligency.zkweb.entity.ZkTreeRequest;
import com.giligency.zkweb.zk.ZkClientFactory;
import com.giligency.zkweb.zk.ZkWebClient;
import org.apache.zookeeper.KeeperException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ZkWebController {


    @PostMapping("/zookeeper/node/getTargetNodeChildren")
    public RetJson getTargetNodeChildren(@RequestBody ZkTreeRequest zkTreeRequest) {

        final ZkWebClient zkWebClient = ZkClientFactory.buildZkClient(zkTreeRequest.getZkAddress(), "");
        try {
            final List<String> childrenWithWatcher = zkWebClient.getChildrenWithWatcher(zkTreeRequest.getAbsolutePath());
            return RetJson.returnSuccess(RetJson.success_message, childrenWithWatcher);
        } catch (KeeperException | InterruptedException e) {
            return RetJson.returnFailure(e.getMessage());
        }

    }

}
