package com.sap.mim.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class NetChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pi = ch.pipeline();
        pi.addLast(new IdleStateHandler(0,0,5));
        // 添加自定义协议的编解码工具
        pi.addLast(new SmartSIMEncoder());
        pi.addLast(new SmartSIMDecoder());
        pi.addLast(new ClientBizInboundHandler());
    }
}
