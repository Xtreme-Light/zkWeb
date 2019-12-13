package com.giligency.zkweb.controller;

import com.giligency.zkweb.entity.RetJson;
import com.giligency.zkweb.entity.ZKNodeDTO;
import com.giligency.zkweb.entity.ZKServerClusterInfoDTO;
import com.giligency.zkweb.entity.ZkTreeRequest;
import com.giligency.zkweb.util.PageHelper;
import com.giligency.zkweb.util.mapper.BeanMapper;
import com.giligency.zkweb.zk.OfficialZookeeperClient;
import com.giligency.zkweb.zk.ZkClientFactory;
import com.giligency.zkweb.zk.ZookeeperClient;
import com.giligency.zkweb.zk.ZookeeperClusterInfo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ZkWebController {


    @PostMapping("/zookeeper/node/getTargetNodeChildren")
    public RetJson<?> getTargetNodeChildren(@RequestBody ZkTreeRequest zkTreeRequest) {
        ZookeeperClient zookeeperClient = ZkClientFactory.getZkClient(zkTreeRequest.getZkAddress());

        final List<String> childrenWithWatcher = zookeeperClient.getChildren(zkTreeRequest.getAbsolutePath());
        return RetJson.retBean(RetJson.success, childrenWithWatcher);


    }

    @DeleteMapping("/zookeeper/node/delete")
    public RetJson<?> deleteTargetNode(@RequestBody ZkTreeRequest zkTreeRequest) {
        ZookeeperClient zookeeperClient = ZkClientFactory.getZkClient(zkTreeRequest.getZkAddress());
        zookeeperClient.delete(zkTreeRequest.getAbsolutePath());
        return RetJson.Success.getSuccess();

    }

    @PutMapping("zookeeper/node/create")
    public RetJson<?> createNodeByTargetPath(@RequestBody ZkTreeRequest zkTreeRequest) {
        ZookeeperClient zookeeperClient = ZkClientFactory.getZkClient(zkTreeRequest.getZkAddress());

        zookeeperClient.create(zkTreeRequest.getAbsolutePath(), false);
        return RetJson.Success.getSuccess();


    }

    /**
     * 想要实时的增加zk到Set中，但是不去连接zk，避免增加的耗时过长
     *
     * @param info zk
     * @return zk实例
     */
    @PostMapping("/zookeeper/server/add")
    public RetJson<?> createZKServer(ZKServerClusterInfoDTO info) {
        ZookeeperClusterInfo map = BeanMapper.map(info, ZookeeperClusterInfo.class);
        ZkClientFactory.getInfos().add(map);
        return RetJson.Success.getSuccess();
    }

    @PostMapping("/zookeeper/server/delete")
    public RetJson<?> deleteZKServer(@RequestBody ZKServerClusterInfoDTO info) {
        ZkClientFactory.removeZookeeperClient(info.getZkAddress());
        return RetJson.Success.getSuccess();
    }

    @GetMapping("/zookeeper/server/list")
    public RetJson<?> getZKServerList() {
        PageHelper pageHelper = new PageHelper();
        pageHelper.setList(ZkClientFactory.getInfos());
        pageHelper.setTotalElements(ZkClientFactory.getInfos().size());
        return RetJson.retBean(RetJson.success, pageHelper);
    }

    @PostMapping("/zookeeper/node/detail")
    public RetJson<?> getDetailNodeInfo(@RequestBody ZkTreeRequest zkTreeRequest) {
        OfficialZookeeperClient zookeeperClient = (OfficialZookeeperClient) ZkClientFactory.getZkClient(zkTreeRequest.getZkAddress());
        ZKNodeDTO targetNodeDetailInfo = zookeeperClient.getTargetNodeDetailInfo(zkTreeRequest.getAbsolutePath());
        return RetJson.retBean(RetJson.success, RetJson.success_message, targetNodeDetailInfo);

    }
}
