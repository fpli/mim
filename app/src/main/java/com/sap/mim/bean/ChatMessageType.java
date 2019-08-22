package com.sap.mim.bean;

public enum ChatMessageType {

    TEXT_MESSAGE(1, "TEXT_MESSAGE"),
    VOICE_MESSAGE(2, "VOICE_MESSAGE"),
    PHOTO_MESSAGE(3,"PHOTO_MESSAGE")
    ;
    private Integer chatMessageType;

    private String  description;

    ChatMessageType(Integer chatMessageType, String description) {
        this.chatMessageType = chatMessageType;
        this.description = description;
    }

    public static ChatMessageType getChatMessageTypeByChatMessageType(Integer chatMessageType){
        for (ChatMessageType chatMessageTypeItem : values()){
            if (chatMessageTypeItem.chatMessageType.equals(chatMessageType)){
                return chatMessageTypeItem;
            }
        }
        return null;
    }

    public Integer getChatMessageType() {
        return chatMessageType;
    }

    public void setChatMessageType(Integer chatMessageType) {
        this.chatMessageType = chatMessageType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
