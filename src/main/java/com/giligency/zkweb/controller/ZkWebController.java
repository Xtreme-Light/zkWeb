package com.giligency.zkweb.controller;

import com.giligency.zkweb.entity.RetJson;
import com.giligency.zkweb.entity.ZKServerClusterInfoDTO;
import com.giligency.zkweb.entity.ZkTreeRequest;
import com.giligency.zkweb.exception.NerException;
import com.giligency.zkweb.util.PageHelper;
import com.giligency.zkweb.util.mapper.BeanMapper;
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
        try {
            final List<String> childrenWithWatcher = zookeeperClient.getChildren(zkTreeRequest.getAbsolutePath());
            return RetJson.retBean(RetJson.success, childrenWithWatcher);
        } catch (NerException e) {
            return RetJson.retBean(RetJson.failure, e.getMessage());
        }

    }

    @DeleteMapping("/zookeeper/node/delete")
    public RetJson<?> deleteTargetNode(@RequestBody ZkTreeRequest zkTreeRequest) {
        ZookeeperClient zookeeperClient = ZkClientFactory.getZkClient(zkTreeRequest.getZkAddress());
        try {
            zookeeperClient.delete(zkTreeRequest.getAbsolutePath());
            return RetJson.Success.getSuccess();
        } catch (NerException e) {
            return RetJson.retErrorBeanWithStack(e.getMessage(), e);

        }
    }

    @PutMapping("zookeeper/node/create")
    public RetJson<?> createNodeByTargetPath(@RequestBody ZkTreeRequest zkTreeRequest) {
        ZookeeperClient zookeeperClient = ZkClientFactory.getZkClient(zkTreeRequest.getZkAddress());
        try {
            zookeeperClient.create(zkTreeRequest.getAbsolutePath(), false);
            return RetJson.Success.getSuccess();
        } catch (NerException e) {
            return RetJson.retErrorBeanWithStack(e.getMessage(), e);
        }

    }

    @PostMapping("/zookeeper/server/add")
    public RetJson<?> createZKServer(ZKServerClusterInfoDTO info) {
        ZookeeperClusterInfo map = BeanMapper.map(info, ZookeeperClusterInfo.class);
        ZkClientFactory.buildZkClient(map);
        return RetJson.Success.getSuccess();
    }

    @GetMapping("/zookeeper/server/list")
    public RetJson<?> getZKServerList() {
        PageHelper pageHelper = new PageHelper();
        pageHelper.setList(ZkClientFactory.getInfos());
        pageHelper.setTotalElements(ZkClientFactory.getInfos().size());
        return RetJson.retBean(RetJson.success, pageHelper);
    }
}
