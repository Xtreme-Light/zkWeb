package com.giligency.zkweb.entity;

import lombok.Data;

import java.util.List;

@Data
public class ZNodeDTO {
    private String parentName;
    private String nodeName;
    private String absolutePath;
    private String zkAddress;
    private ZNodeInfoVO zNodeInfoVO;
    private List<ZNodeDTO> children;
    private List<String> childrenName;
}
