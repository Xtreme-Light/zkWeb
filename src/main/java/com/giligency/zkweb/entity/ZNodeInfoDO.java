package com.giligency.zkweb.entity;

import lombok.Data;
import org.apache.zookeeper.data.ACL;

import java.util.List;

/**
 * zookeeper 的节点领域对象
 */
@Data
public class ZNodeInfoDO {
    private String path;
    private String params;
    private String data;
    private long czxid;
    private long mzxid;
    private long ctime;
    private long mtime;
    private int version;
    private int cversion;
    private int aversion;
    private long ephemeralOwner;
    private int dataLength;
    private int numChildren;
    private long pzxid;
    private List<ACL> aclList;
}
