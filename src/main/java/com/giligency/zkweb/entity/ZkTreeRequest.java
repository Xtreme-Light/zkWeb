package com.giligency.zkweb.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ZkTreeRequest {
    @NotNull(message = "节点路径不能为空！")
    private String absolutePath;
    @NotNull(message = "zk地址不能为空")
    private String zkAddress;
}
