package com.sap.mim.constant;

/**
 * 描述:消息类型常量
 */
public enum MessageType {

    MESSAGE_CONNECTION,//客户端连接事件

    MESSAGE_LOGIN, // 登录事件

    MESSAGE_EVENT, // 心跳事件

    MESSAGE_CLOSE;// 客户端断开连接事件


}
