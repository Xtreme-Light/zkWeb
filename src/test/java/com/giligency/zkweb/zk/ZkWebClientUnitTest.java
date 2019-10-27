package com.giligency.zkweb.zk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class ZkWebClientUnitTest {
    /**
     * 测试同步创建节点
     *
     * @throws Exception
     */
    private ZkWebClient zkWebClient;
    @BeforeEach
    public void init() throws Exception{
        zkWebClient = new ZkWebClient("192.168.3.26",null,30000);
        zkWebClient.connect();

    }
    @AfterEach
    public void close() throws Exception {
        zkWebClient.close();
    }
    @Test
    public void test4CreatNode() throws Exception{
        //192.168.3.26
        zkWebClient.createOpenACLPersistentNodeSync("/test",null);
        zkWebClient.createOpenACLPersistentNodeSync("/test/node1",null);
        zkWebClient.createOpenACLPersistentNodeSync("/test/node1/node11",null);
        zkWebClient.createOpenACLPersistentNodeSync("/test/node1/node12",null);
        zkWebClient.createOpenACLPersistentNodeSync("/test/node1/node13",null);
        zkWebClient.createOpenACLPersistentNodeSync("/test/node2",null);
        zkWebClient.createOpenACLPersistentNodeSync("/test/node2/node21",null);
        zkWebClient.createOpenACLPersistentNodeSync("/test/node2/node22",null);
        zkWebClient.createOpenACLPersistentNodeSync("/test/node2/node23",null);

    }
    @Test
    public void test4CreatRecursionNode() throws Exception{
        zkWebClient.createOpenACLPersistentRecursionNodeSync("/test/node3/node32/node321",null);

    }

        @Test
    public void test4ListNodes() throws Exception{
        final List<String> childrenWithWatcher = zkWebClient.getChildrenWithWatcher("/");
        System.out.println("==========================" + childrenWithWatcher);
        final List<String> childrenWithWatcher1 = zkWebClient.getChildrenWithWatcher("/test");
        System.out.println("==========================" + childrenWithWatcher1);
    }

}
