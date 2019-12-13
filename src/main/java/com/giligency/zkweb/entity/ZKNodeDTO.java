package com.giligency.zkweb.entity;

import lombok.Data;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.Serializable;
import java.util.List;

@Data
public class ZKNodeDTO implements Serializable {
    private Stat stat;
    private String path;
    private String data;
    private List<ACL> acl;
}
