package com.sap.mim.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

//用于读取客户端发来的信息
public class ClientBizInboundHandler extends SimpleChannelInboundHandler<SmartSIMProtocol> {

    // 客户端与服务端，连接成功的售后
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
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

            System.out.println("Client接受的客户端的信息 :" + msg.toString());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}