package com.sap.mim.bean;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class MessageModel implements Externalizable {

    private Long        msgId;
    private MessageType messageType;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(msgId);
        out.writeInt(messageType.getType());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        msgId       = in.readLong();
        messageType = MessageType.getMessageTypeById(in.readInt());
    }

    public MessageModel() {
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }
}
