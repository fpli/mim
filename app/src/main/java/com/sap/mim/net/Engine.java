package com.sap.mim.net;

import com.sap.mim.base.MimApplication;
import com.sap.mim.bean.*;
import com.sap.mim.entity.MessageInfo;
import com.sap.mim.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 描述:UIThread负责把数据发到引擎解耦,数据编组，序列化，发送
 * 数据接收，反序列化，数据解析，通知UIThread
 */
public class Engine {

    private static ConcurrentMap<Long, MessageInfo> pendingSendQueue = new ConcurrentHashMap<>();
    private static LoginMessage loginMessage;
    private static Map<Integer, List<MessageInfo>> mChatMessagesMap;

    public static void receiveMessageInfo(MessageInfo messageInfo){
        pendingSendQueue.putIfAbsent(messageInfo.getMsgId(), messageInfo);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageType(MessageType.C2S);
        chatMessage.setSendTime(messageInfo.getTime());
        // ....
        NetService.getNetService().sendMessageModel(chatMessage);
    }

    public static void receiveAckMessage(Long msgId){
        if (pendingSendQueue.containsKey(msgId)){
            MessageInfo messageInfo = pendingSendQueue.get(msgId);
            messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
            pendingSendQueue.remove(msgId);
        }
    }

    public static void receiveChatMessage(ChatMessage chatMessage){
        // 由handler处理这个消息
        System.out.println(chatMessage);
        //handler.sendMessage(null);
        if (null == mChatMessagesMap){
            mChatMessagesMap = new HashMap<>();
        }
        if (mChatMessagesMap.containsKey(chatMessage.getSenderId())){
            MessageInfo messageInfo = new MessageInfo();

            mChatMessagesMap.get(chatMessage.getReceiverId()).add(messageInfo);
        }
    }

    public static void receiveSearchFriendResultMessage(SearchFriendResultMessage searchFriendResultMessage){

    }

    public static void receiveLoginResultMessage(LoginResultMessage loginResultMessage){
        System.out.println("登录消息到达---->");
        if (loginResultMessage.getCode() == 0){
            loginMessage.setAccount(loginResultMessage.getAccount());
            MimApplication.getInstance().setmAccount(loginResultMessage.getAccount());
        }
        synchronized (loginMessage){
            loginMessage.notify();
        }
    }

    public static LoginMessage getLoginMessage() {
        return loginMessage;
    }

    public static void setLoginMessage(LoginMessage loginMessage) {
        Engine.loginMessage = loginMessage;
    }
}
