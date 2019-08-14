package com.sap.mim.bean;

/**
 * 描述:app 上报消息(登录，心跳检测，聊天(c2c,c2g))
 */
public enum C2SMessageType {

    C_2_S_MESSAGE(1, "C_2_S_MESSAGE"),
    C_2_S_HEARTBEAT(2, "C_2_S_HEARTBEAT");

    private Integer c2sMessageType;

    private String  description;

    C2SMessageType(Integer c2sMessageType, String description) {
        this.c2sMessageType = c2sMessageType;
        this.description = description;
    }

    public static C2SMessageType getC2SMessageTypeById(Integer c2sMessageType){
        for (C2SMessageType c2SMessageType : values()){
            if (c2SMessageType.c2sMessageType.equals(c2sMessageType)){
                return c2SMessageType;
            }
        }
        return null;
    }

    public Integer getC2sMessageType() {
        return c2sMessageType;
    }

    public void setC2sMessageType(Integer c2sMessageType) {
        this.c2sMessageType = c2sMessageType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
