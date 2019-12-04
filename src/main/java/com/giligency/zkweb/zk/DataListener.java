package com.giligency.zkweb.zk;


public interface DataListener {
    void dataChanged(String path, Object value, EventType eventType);
}
