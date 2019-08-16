package com.sap.mim.net;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyChannelPoolHandler implements ChannelPoolHandler {

    @Override
    public void channelReleased(Channel ch) throws Exception {
        System.out.println("channelReleased. Channel ID: " + ch.id());
    }

    @Override
    public void channelAcquired(Channel ch) throws Exception {
        System.out.println("channelAcquired. Channel ID: " + ch.id());
    }

    @Override
    public void channelCreated(Channel ch) throws Exception {
        System.out.println("channelCreated. Channel ID: " + ch.id());
        NioSocketChannel nioSocketChannel = (NioSocketChannel) ch;
        ClientBizInboundHandler clientBizInboundHandler = new ClientBizInboundHandler();
        nioSocketChannel.pipeline().addLast(new IdleStateHandler(0,0,5));
        // 添加自定义协议的编解码工具
        nioSocketChannel.pipeline().addLast(new SmartSIMEncoder());
        nioSocketChannel.pipeline().addLast(new SmartSIMDecoder());
        // 处理具体业务数据
        nioSocketChannel.pipeline().addLast(clientBizInboundHandler);
    }
}
