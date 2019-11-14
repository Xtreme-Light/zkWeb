package com.giligency.zkweb.unit;

import com.giligency.zkweb.zk.ZkClientFactory;
import com.giligency.zkweb.zk.ZkWebClient;
import org.apache.zookeeper.KeeperException;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UnitTest {
    private static String path = "/test/node1/node12";

    public static void main(String[] args) {
        test4Split(path);
    }

    @Test
    public static void test4Split(String path) {
        final int i = path.lastIndexOf("/");
        final String substring = path.substring(0, i);
        if (substring.length() > 0) {
            System.out.println(substring);
            test4Split(substring);
        }
    }

    @Test
    public void test4RetJson() throws KeeperException, InterruptedException {
        final ZkWebClient zkWebClient = ZkClientFactory.buildZkClient("20.26.25.44:2182,20.26.25.45:2182,20.26.25.46:2182",
                "amber:amber123!");
        zkWebClient.traverseZkTree("/amber");
/*        final RetJson retSuccess = RetJson.retSuccess;
        System.out.println(retSuccess);
        retSuccess.setMessage("1111");
        System.out.println(retSuccess);*/
        final List<String> errorPaths = ZkWebClient.errorPaths;
        System.out.println("::::::::::::::::::::::::::::" + errorPaths);
    }

    @Test
    public void test44() {

    }

}
