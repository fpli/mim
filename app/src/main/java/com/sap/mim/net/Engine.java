package com.sap.mim.net;

import android.os.Handler;
import android.os.Message;
import com.sap.mim.base.MimApplication;
import com.sap.mim.bean.*;
import com.sap.mim.entity.MessageInfo;
import com.sap.mim.util.Constants;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 描述:UIThread负责把数据发到引擎解耦,数据编组，序列化，发送
 * 数据接收，反序列化，数据解析，通知UIThread
 */
public class Engine {

    private static ConcurrentMap<Long, MessageInfo> pendingSendQueue = new ConcurrentHashMap<>();
    private static LoginMessage loginMessage;
    private static Map<Integer, List<MessageInfo>> mChatMessagesMap;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void receiveMessageInfo(MessageInfo messageInfo) {
        pendingSendQueue.putIfAbsent(messageInfo.getMsgId(), messageInfo);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMsgId(messageInfo.getMsgId());
        chatMessage.setMessageType(MessageType.C2S);
        chatMessage.setSenderId(loginMessage.getAccount().getId());
        chatMessage.setReceiverId(messageInfo.getReceiverId());
        chatMessage.setSendTime(simpleDateFormat.format(new Date()));
        chatMessage.setChatMessageType(ChatMessageType.getChatMessageTypeByChatMessageType(messageInfo.getContentType()));
        byte[] content = null;
        switch (chatMessage.getChatMessageType()) {
            case TEXT_MESSAGE:
                content = messageInfo.getContent().getBytes();
                break;
            case PHOTO_MESSAGE:
                content = new byte[1024];
                break;
            case VOICE_MESSAGE:
                content = messageInfo.getContent().getBytes();
                break;
            default:
                content = new byte[1024];
        }
        chatMessage.setContent(content);
        NetService.getNetService().sendMessageModel(chatMessage);
        List<MessageTabEntity> messageTabEntityList = MimApplication.getInstance().getMessageTabEntityList();
        AtomicBoolean hasCurrent = new AtomicBoolean(false);
        messageTabEntityList.forEach(messageTabEntity -> {
            if (messageTabEntity.getReceiverId() == chatMessage.getReceiverId()) {
                hasCurrent.set(true);
            }
        });
        if (!hasCurrent.get()) {
            MessageTabEntity messageTab = new MessageTabEntity();
            messageTab.setSenderId(chatMessage.getSenderId());
            messageTab.setReceiverId(chatMessage.getReceiverId());
            messageTab.setMessageType(MessageTabEntity.FRIEND_MESSAGE);
            for (Account account : MimApplication.getInstance().getmAccount().getFriendList()) {
                if (account.getId() == chatMessage.getReceiverId()) {
                    messageTab.setName(account.getUserName());
                    break;
                }
            }
            messageTab.setSendTime(chatMessage.getSendTime());
            messageTab.setUnReadCount(0);
            messageTabEntityList.add(messageTab);
            List<MessageInfo> messageInfoList = new LinkedList<>();
            mChatMessagesMap.put(chatMessage.getSenderId(), messageInfoList);
            Handler messageHandler = MimApplication.getInstance().getMessageHandler();
            if (messageHandler != null) {
                Message message = new Message();
                message.what = 1;
                messageHandler.sendMessage(message);
            }
        }
    }

    public static void receiveAckMessage(Long msgId) {
        if (pendingSendQueue.containsKey(msgId)) {
            MessageInfo messageInfo = pendingSendQueue.get(msgId);
            messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
            pendingSendQueue.remove(msgId);
        }
    }

    public static void receiveChatMessage(ChatMessage chatMessage) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setMsgId(chatMessage.getMsgId());
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_LEFT);
        messageInfo.setSenderId(chatMessage.getSenderId());
        messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
        messageInfo.setReceiverId(chatMessage.getReceiverId());
        messageInfo.setContentType(chatMessage.getChatMessageType().getChatMessageType());
        messageInfo.setContent(new String(chatMessage.getContent()));// 暂时只处理文本内容
        List<MessageTabEntity> messageTabEntityList = MimApplication.getInstance().getMessageTabEntityList();
        if (!mChatMessagesMap.containsKey(chatMessage.getSenderId())) {
            List<MessageInfo> messageInfoList = new LinkedList<>();
            mChatMessagesMap.put(chatMessage.getSenderId(), messageInfoList);
            MessageTabEntity messageTab = new MessageTabEntity();
            messageTab.setSenderId(chatMessage.getReceiverId());
            messageTab.setReceiverId(chatMessage.getSenderId());
            messageTab.setMessageType(MessageTabEntity.FRIEND_MESSAGE);
            messageTab.setName("未知名称");
            for (Account account : MimApplication.getInstance().getmAccount().getFriendList()) {
                if (account.getId() == chatMessage.getSenderId()) {
                    messageTab.setName(account.getUserName());
                    break;
                }
            }
            messageTab.setSendTime(chatMessage.getSendTime());
            messageTab.setUnReadCount(1);
            messageTabEntityList.add(messageTab);
        } else {
            messageTabEntityList.forEach(messageTabEntity -> {
                if (messageTabEntity.getReceiverId() == chatMessage.getSenderId()) {
                    messageTabEntity.setUnReadCount(messageTabEntity.getUnReadCount() + 1);
                }
            });
        }
        mChatMessagesMap.get(chatMessage.getSenderId()).add(messageInfo);
        Handler messageHandler = MimApplication.getInstance().getMessageHandler();
        if (messageHandler != null) {
            Message message = new Message();
            message.what = 1;
            messageHandler.sendMessage(message);
        }
        Handler chatMessageHandler = MimApplication.getInstance().getChatMessageHandler();
        if (chatMessageHandler != null) {
            Message message = new Message();
            message.what = 1;
            chatMessageHandler.sendMessage(message);
        }
    }

    public static void receiveSearchFriendResultMessage(SearchFriendResultMessage searchFriendResultMessage) {

    }

    public static void receiveLoginResultMessage(LoginResultMessage loginResultMessage) {
        if (loginResultMessage.getCode() == 0) {
            loginMessage.setAccount(loginResultMessage.getAccount());
            MimApplication.getInstance().setmAccount(loginResultMessage.getAccount());
            if (null == mChatMessagesMap) {
                mChatMessagesMap = new HashMap<>();
            }
        }
        synchronized (loginMessage) {
            loginMessage.notify();
        }
    }

    public static LoginMessage getLoginMessage() {
        return loginMessage;
    }

    public static void setLoginMessage(LoginMessage loginMessage) {
        Engine.loginMessage = loginMessage;
    }

    public static Map<Integer, List<MessageInfo>> getmChatMessagesMap() {
        return mChatMessagesMap;
    }
}
