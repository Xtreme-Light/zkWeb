package com.giligency.zkweb.zk;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractZookeeperClient implements ZookeeperClient {

    protected final int DEFAULT_CONNECTION_TIMEOUT_MS = 5 * 1000;
    protected final int DEFAULT_SESSION_TIMEOUT_MS = 60 * 1000;
    private final ZookeeperClusterInfo info;
    private volatile boolean closed = false;

    public AbstractZookeeperClient(ZookeeperClusterInfo info) {
        this.info = info;
    }

    @Override
    public void create(String path, boolean ephemeral) {
        int i = path.lastIndexOf('/');
        if (i > 0) {
            create(path.substring(0, i), false);
        }
        if (ephemeral) {
            createEphemeral(path);
        } else {
            createPersistent(path);
        }
    }

    @Override
    public void create(String path, String content, boolean ephemeral) {
        if (checkExists(path)) {
            delete(path);
        }
        int i = path.lastIndexOf('/');
        if (i > 0) {
            create(path.substring(0, i), false);
        }
        if (ephemeral) {
            createEphemeral(path, content);
        } else {
            createPersistent(path, content);
        }
    }


    @Override
    public void delete(String path) {
        deletePath(path);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            doClose();
        } catch (Throwable t) {
            log.warn(t.getMessage(), t);
        }
    }

    public String getContent(String path) {
        if (!checkExists(path)) {
            return null;
        }
        return doGetContent(path);
    }

    protected abstract String doGetContent(String path);

    protected abstract void deletePath(String path);

    protected abstract void createEphemeral(String path);

    protected abstract void createPersistent(String path, String data);

    protected abstract void createPersistent(String path);

    protected abstract void doClose();

    protected abstract void createEphemeral(String path, String data);

    public ZookeeperClusterInfo getZookeeperClusterInfo() {
        return info;
    }


}
