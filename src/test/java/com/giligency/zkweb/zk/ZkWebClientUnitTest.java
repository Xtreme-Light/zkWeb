package com.giligency.zkweb.zk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class ZkWebClientUnitTest {
    /**
     * 测试同步创建节点
     *
     * @throws Exception
     */
    private ZookeeperClient zkWebClient;
    @BeforeEach
    public void init() throws Exception{
        zkWebClient = ZkClientFactory.buildZkClient(new ZookeeperClusterInfo("127.0.0.1:2181"));

    }
    @AfterEach
    public void close() throws Exception {
        zkWebClient.close();
    }
    @Test
    public void test4CreatNode() throws Exception {
        //192.168.3.26
/*        zkWebClient.create("/test",false);
        zkWebClient.create("/test/node1",false);
        zkWebClient.create("/test/node1/node11",false);
        zkWebClient.create("/test/node1/node12",false);
        zkWebClient.create("/test/node1/node13",false);
        zkWebClient.create("/test/node2",false);
        zkWebClient.create("/test/node2/node21",false);
        zkWebClient.create("/test/node2/node22",false);*/
        zkWebClient.create("/test/node2/node23", false);

    }

    @Test
    public void test4CreatRecursionNode() throws Exception {
        zkWebClient.create("/test/node3/node32/node321", false);

    }

    @Test
    void test4Exist() {
        boolean b = zkWebClient.checkExists("/test/node3/node32/node321");
        System.out.println("========================" + b);
    }

    @Test
    public void test4ListNodes() throws Exception {
/*        final List<String> childrenWithWatcher = zkWebClient.getChildrenWithWatcher("/");
        System.out.println("==========================" + childrenWithWatcher);
        final List<String> childrenWithWatcher1 = zkWebClient.getChildrenWithWatcher("/test");
        System.out.println("==========================" + childrenWithWatcher1);*/
    }

}
