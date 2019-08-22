package com.sap.mim.net;

import android.os.Handler;
import com.sap.mim.bean.ChatMessage;
import com.sap.mim.bean.MessageType;
import com.sap.mim.entity.MessageInfo;
import com.sap.mim.util.Constants;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 描述:UIThread负责把数据发到引擎解耦,数据编组，序列化，发送
 * 数据接收，反序列化，数据解析，通知UIThread
 */
public class Engine {

    private static ConcurrentMap<Long, MessageInfo> pendingSendQueue = new ConcurrentHashMap<>();
    private static Handler handler; // 处理服务端推送的数据
    /**
     * UIThread 发送消息
     * @param messageInfo
     */
    public static void receiveMessageInfo(MessageInfo messageInfo){
        pendingSendQueue.putIfAbsent(messageInfo.getMsgId(), messageInfo);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageType(MessageType.C2S);
        chatMessage.setSendTime(messageInfo.getTime());
        // ....
        //NetService.getNetService().sendMessageModel(chatMessage);
        NetService2.getNetService2().sendMessageModel(chatMessage);
    }

    public static void removeMessageInfo(Long msgId){
        if (pendingSendQueue.containsKey(msgId)){
            MessageInfo messageInfo = pendingSendQueue.get(msgId);
            messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
            pendingSendQueue.remove(msgId);
        }
    }

    public static void receiveChatMessage(ChatMessage chatMessage){
        // 由handler处理这个消息
        handler.sendMessage(null);
    }

}
