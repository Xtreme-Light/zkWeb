package com.giligency.zkweb.util;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageHelper<E> implements Serializable {
    private E list;
    private int totalElements;
    private int pageNumer;
    private int pageSize;
}
