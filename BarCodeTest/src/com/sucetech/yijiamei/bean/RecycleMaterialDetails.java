package com.sucetech.yijiamei.bean;


public class RecycleMaterialDetails {

    /**
     * 自增Id
     */
    public Integer id;

    /**
     * 回收Id 可以不传
     */
    public Integer refId;

    /**
     * 物料类型
     */
    public Byte type;

    /**
     * 重量
     */
    public Float weight;

    /**
     * 体积
     */
    public String volume;

    /**
     * 流程Id可以不传
     */
    public Byte flowType;

    boolean isOK;
}