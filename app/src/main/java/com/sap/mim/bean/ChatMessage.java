package com.sap.mim.bean;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ChatMessage extends MessageModel {


    private static final long serialVersionUID = -3089815309304650451L;

    private int             senderId;        // 发送方id
    private int             receiverId;      // 接收方id
    private String          sendTime;        // 发送时刻
    private ChatMessageType chatMessageType; // 消息类型:文本消息|语音消息|图片消息
    private byte[]          content;         // 消息内容

    public ChatMessage() {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(senderId);
        out.writeInt(receiverId);
        out.writeUTF(sendTime);
        out.writeInt(chatMessageType.getChatMessageType());
        out.writeObject(content);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        senderId        = in.readInt();
        receiverId      = in.readInt();
        sendTime        = in.readUTF();
        chatMessageType = ChatMessageType.getChatMessageTypeByChatMessageType(in.readInt());
        content         = (byte[]) in.readObject();
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public ChatMessageType getChatMessageType() {
        return chatMessageType;
    }

    public void setChatMessageType(ChatMessageType chatMessageType) {
        this.chatMessageType = chatMessageType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
