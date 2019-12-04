package com.giligency.zkweb.zk;

import java.util.List;

public interface ZookeeperClient {
    void create(String path, boolean ephemeral);

    void delete(String path);

    List<String> getChildren(String path);

    List<String> addChildListener(String path, ChildListener listener);

    /**
     * @param path:    directory. All of child of path will be listened.
     * @param listener
     */
    void addDataListener(String path, DataListener listener);

    void close();

    void create(String path, String content, boolean ephemeral);

    String getContent(String path);

    ZookeeperClusterInfo getZookeeperClusterInfo();

    boolean checkExists(String path);
}
