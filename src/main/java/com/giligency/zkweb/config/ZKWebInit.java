package com.giligency.zkweb.config;


import com.alibaba.fastjson.JSONObject;
import com.giligency.zkweb.util.json.JSONUtil;
import com.giligency.zkweb.zk.ZkClientFactory;
import com.giligency.zkweb.zk.ZookeeperClusterInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class ZKWebInit {
    private static String DEFAULT_JSON_CONFIG_FILE = "./zkServersConfigInfo.json";


    @PostConstruct
    public void initJsonConfigFile() {
        File file = new File(DEFAULT_JSON_CONFIG_FILE);
        if (file.exists() && file.isFile()) {
            log.info("配置文件{}已存在", DEFAULT_JSON_CONFIG_FILE);
            log.info("start to init ZKServer Info");
            String jsonString = JSONUtil.readJsonFile(file);
            if (!StringUtils.isBlank(jsonString)) {
                List<ZookeeperClusterInfo> zookeeperClusterInfos = JSONObject.parseArray(jsonString, ZookeeperClusterInfo.class);
                ZkClientFactory.initZookeeperClient(zookeeperClusterInfos);
            }
        } else {
            log.info("配置文件不存在，无需进行配置装载");
            log.info("配置文件信息为空，初始化一个默认的本地zk信息");
            List<ZookeeperClusterInfo> zookeeperClusterInfos = Collections.singletonList(new ZookeeperClusterInfo("localZK","127.0.0.1:2181"));
            ZkClientFactory.initZookeeperClient(zookeeperClusterInfos);
        }
    }

    @PreDestroy
    public void preDestory() {
        Set<ZookeeperClusterInfo> infos = ZkClientFactory.getInfos();
        if (infos.size() == 0) {
            log.info("已存的ZK信息为空，无需保存");
            return;
        }

        JSONUtil.createJsonFile(infos, DEFAULT_JSON_CONFIG_FILE);
    }
}
