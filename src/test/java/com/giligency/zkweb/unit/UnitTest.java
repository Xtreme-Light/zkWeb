package com.giligency.zkweb.unit;

import com.giligency.zkweb.entity.RetJson;
import lombok.Data;
import org.apache.zookeeper.KeeperException;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
/*        final ZkWebClient zkWebClient = ZkClientFactory.buildZkClient("20.26.25.44:2182,20.26.25.45:2182,20.26.25.46:2182",
                "amber:amber123!");
        zkWebClient.traverseZkTree("/amber");
*//*        final RetJson retSuccess = RetJson.retSuccess;
        System.out.println(retSuccess);
        retSuccess.setMessage("1111");
        System.out.println(retSuccess);*//*
        final List<String> errorPaths = ZkWebClient.errorPaths;
        System.out.println("::::::::::::::::::::::::::::" + errorPaths);*/
    }

    @Test
    void test45() throws KeeperException, InterruptedException {
        /*final ZkWebClient zkWebClient = ZkClientFactory.buildZkClient("20.26.25.37:2183,20.26.25.38:2183,20.26.25.39:2183",
                null);
        final List<String> childrenWithWatcher = zkWebClient.getChildrenWithWatcher("/busidomain/csf/centers/demo/policies/clients");
        System.out.println(childrenWithWatcher.size());
        childrenWithWatcher.forEach(
                V -> {
                    try {
                        zkWebClient.deleteTargetPathNodeWhateverWhichVersion("/busidomain/csf/centers/demo/policies/clients/" + V);
                    } catch (KeeperException | InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
        );*/
    }

    @Test
    public void test44() throws KeeperException, InterruptedException {
       /* //	20.26.25.37:2183,20.26.25.38:2183,20.26.25.39:2183
        final ZkWebClient zkWebClient = ZkClientFactory.buildZkClient("20.26.25.37:2183,20.26.25.38:2183,20.26.25.39:2183",
                null);
        final List<String> childrenWithWatcher = zkWebClient.getChildrenWithWatcher("/busidomain/csf/centers/demo/policies/clients");
        final String rootPath = "/busidomain/csf/centers";
        final List<String> centers = zkWebClient.getChildrenWithWatcher(rootPath);
        centers.forEach(
                V -> {
                    String newPath = rootPath + "/" + V + "/policies/clients";
                    try {
                        final List<String> childrenWithWatcher1 = zkWebClient.getChildrenWithWatcher(newPath);
                        childrenWithWatcher1.forEach(
                                E -> {
                                    try {
                                        zkWebClient.deleteTargetPathNodeWhateverWhichVersion(newPath + "/" + E);
                                    } catch (KeeperException | InterruptedException e) {
                                        if (e instanceof KeeperException.NoNodeException) {
                                            System.err.println(newPath + "/" + E + "不存在");
                                        }
                                        System.err.println(e.getMessage());
                                    }
                                }
                        );
                    } catch (KeeperException | InterruptedException e) {
                        System.err.println(e.getMessage());
                    }
                }
        );
        System.out.println(childrenWithWatcher.size() + "==================");
*/
    }

    @Test
    void test4StaticInnerClass() {
        RetJson<?> success = Test4StaticInnerClass.getSuccess();

        System.out.println(success.getMessage());
    }

    @Test
    void testException() {

        //378
        //372
        try {
            throw new Exception("aaaa");
        } catch (Exception e) {
            if (e instanceof KeeperException.NotEmptyException) {
                System.out.println(RetJson.retDefaultErrorBean("1"));
            } else if (e instanceof KeeperException.NoNodeException) {
                System.out.println(RetJson.retDefaultErrorBean("2"));
            } else if (e instanceof Exception) {
                System.out.println(RetJson.retDefaultErrorBean("3"));
            }

        }

    }

    @Test
    void test4Regular() {
        String format = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now());
        String format2 = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now());
        System.out.println(format);
        System.out.println(format2);
    }

    @Test
    void testException2() {


        //401
        //377
        try {
            throw new Exception("aaaa");
        } catch (KeeperException.NotEmptyException e) {
            System.out.println(RetJson.retDefaultErrorBean("1"));

        } catch (KeeperException.NoNodeException e) {
            System.out.println(RetJson.retDefaultErrorBean("2"));

        } catch (Exception e) {
            System.out.println(RetJson.retDefaultErrorBean("3"));

        }

    }

    @Data
    public static final class Test4StaticInnerClass<T> extends RetJson<T> implements Serializable {
        private static final Test4StaticInnerClass<?> success = new Test4StaticInnerClass<>();
        private final String code = RetJson.success;
        private final String message = RetJson.success_message;

        private Test4StaticInnerClass() {

        }

        public static RetJson<?> getSuccess() {
            return success;
        }

    }
}
