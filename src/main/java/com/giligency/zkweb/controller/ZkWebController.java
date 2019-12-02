package com.giligency.zkweb.controller;

import com.giligency.zkweb.entity.RetJson;
import com.giligency.zkweb.entity.ZkTreeRequest;
import com.giligency.zkweb.zk.ZkClientFactory;
import com.giligency.zkweb.zk.ZkWebClient;
import org.apache.zookeeper.KeeperException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ZkWebController {


    @PostMapping("/zookeeper/node/getTargetNodeChildren")
    public RetJson<?> getTargetNodeChildren(@RequestBody ZkTreeRequest zkTreeRequest) {
        final ZkWebClient zkWebClient = ZkClientFactory.buildZkClient(zkTreeRequest.getZkAddress(), "");
        try {
            final List<String> childrenWithWatcher = zkWebClient.getChildrenWithWatcher(zkTreeRequest.getAbsolutePath());
            return RetJson.retBean(RetJson.success, childrenWithWatcher);
        } catch (KeeperException | InterruptedException e) {
            return RetJson.retBean(RetJson.failure, e.getMessage());
        }

    }

    @DeleteMapping("/zookeeper/node/delete")
    public RetJson<?> deleteTargetNode(@RequestBody ZkTreeRequest zkTreeRequest) {
        final ZkWebClient zkWebClient = ZkClientFactory.buildZkClient(zkTreeRequest.getZkAddress(), "");
        try {
            zkWebClient.deleteTargetPathNodeWhateverWhichVersion(zkTreeRequest.getAbsolutePath());
            return RetJson.Success.getSuccess();
        } catch (KeeperException | InterruptedException e) {
            if (e instanceof KeeperException.NotEmptyException) {
                return RetJson.retErrorBeanWithStack("删除的节点" + zkTreeRequest.getAbsolutePath() + "不为空", e);
            } else if (e instanceof KeeperException.NoNodeException) {
                return RetJson.retErrorBeanWithStack("删除的节点" + zkTreeRequest.getAbsolutePath() + "不为空", e);
            }
            return RetJson.retErrorBeanWithStack("删除节点出现异常", e);

        }
    }
}
