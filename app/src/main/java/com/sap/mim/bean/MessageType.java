package com.sap.mim.bean;

public enum MessageType {

    C2S(1,"C2S"),
    S2C(2,"S2C"),
    ACK(3,"ACK");

    private Integer type;

    private String  description;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    MessageType(Integer type, String description){
        this.type        = type;
        this.description = description;
    }

    public static MessageType getMessageTypeById(Integer type){
        for (MessageType messageType : values()){
            if (messageType.type.equals(type)){
                return  messageType;
            }
        }
        return null;
    }
}
