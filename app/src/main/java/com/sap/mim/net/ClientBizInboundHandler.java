package com.sap.mim.net;

import com.sap.mim.bean.*;
import com.sap.mim.util.MessageIdGenerator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * 描述:客户端业务数据接收处理器
 */
public class ClientBizInboundHandler extends SimpleChannelInboundHandler<SmartSIMProtocol> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ACKMessage ackMessage = new ACKMessage();
        ackMessage.setMessageType(MessageType.ACK);
        ackMessage.setMsgId(MessageIdGenerator.getMsgId());
        NetService.getNetService().sendMessageModel(ackMessage);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, SmartSIMProtocol msg) throws Exception {
        byte[] data = msg.getContent();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        ObjectInputStream objectInputStream       = new ObjectInputStream(byteArrayInputStream);
        Object message                            = objectInputStream.readObject();
        if (message instanceof ACKMessage){
            ACKMessage ackMessage = (ACKMessage) message;
            Long msgId            = ackMessage.getMsgId();
            // 处理客户端已发送的消息

        }

        if (message instanceof ChatMessage){
            ChatMessage chatMessage = (ChatMessage) message;

        }

        if (message instanceof LoginResultMessage){
            LoginResultMessage loginResultMessage = (LoginResultMessage)message;
            int code = loginResultMessage.getCode();
            if (code == 0){
                Account account = loginResultMessage.getAccount();
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}