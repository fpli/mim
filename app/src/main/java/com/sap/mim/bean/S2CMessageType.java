package com.sap.mim.bean;

/**
 * 描述:S2C（消息推送，c2c, g2c, ack）
 */
public enum S2CMessageType {

    ;
    private Integer s2cMessageType;

    private String  description;

    S2CMessageType(Integer s2cMessageType, String description) {
        this.s2cMessageType = s2cMessageType;
        this.description = description;
    }

    public static S2CMessageType getS2CMessageTypeByType(Integer s2cMessageType){
        for (S2CMessageType s2CMessageType : values()){
            if (s2CMessageType.s2cMessageType.equals(s2cMessageType)){
                return s2CMessageType;
            }
        }
        return null;
    }
}
