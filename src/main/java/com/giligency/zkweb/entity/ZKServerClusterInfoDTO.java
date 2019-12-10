package com.giligency.zkweb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ZKServerClusterInfoDTO implements Serializable {
    private String name;

    private String zkAddress;

    private String authority;

    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
