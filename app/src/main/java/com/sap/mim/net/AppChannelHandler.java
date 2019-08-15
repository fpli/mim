package com.sap.mim.net;

import android.util.Log;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.IOException;

/**
 * 网络事件处理器
 */
public class AppChannelHandler extends ChannelInitializer<NioSocketChannel> {

    private static final String Tag = "AppChannelHandler";

    private ClientBizInboundHandler clientBizInboundHandler = new ClientBizInboundHandler();
    private Channel channel;

    @Override
    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
        nioSocketChannel.pipeline().addLast(new IdleStateHandler(0,0,5));
        // 添加自定义协议的编解码工具
        nioSocketChannel.pipeline().addLast(new SmartSIMEncoder());
        nioSocketChannel.pipeline().addLast(new SmartSIMDecoder());
        // 处理具体业务数据
        nioSocketChannel.pipeline().addLast(clientBizInboundHandler);
        this.channel = nioSocketChannel;
    }

    public void sentSmartSIMProtocol(SmartSIMProtocol request) throws IOException {
        try {
            channel.writeAndFlush(request).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(Tag, "---->发送完毕----->");
    }

}
