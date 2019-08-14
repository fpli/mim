package com.sap.mim.net;

import com.sap.mim.bean.ACKMessage;
import com.sap.mim.bean.ChatMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 描述:客户端业务数据接收处理器
 */
public class ClientBizInboundHandler extends SimpleChannelInboundHandler<SmartSIMProtocol> {

    private ChannelHandlerContext ctx;

    // 客户端与服务端，连接成功的售后
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        // 发送SmartCar协议的消息
        // 要发送的信息
        String data = "I am client ...";
        // 获得要发送信息的字节数组
        byte[] content = data.getBytes();
        // 要发送信息的长度
        int contentLength = content.length;

        SmartSIMProtocol protocol = new SmartSIMProtocol();
        protocol.setContentLength(contentLength);
        protocol.setContent(content);

        ctx.writeAndFlush(protocol);
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

        }
        System.out.println("Client接受的客户端的信息 :" + msg.toString());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    public void writeAndFlush(SmartSIMProtocol msg){
        ctx.writeAndFlush(msg);
    }

}