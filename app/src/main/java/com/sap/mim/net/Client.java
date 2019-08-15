package com.sap.mim.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class Client {


    private ClientBizInboundHandler clientBizInboundHandler = new ClientBizInboundHandler();

    /**
     * 连接服务器
     *
     * @param port
     * @param host
     * @throws Exception
     */
    public void connect(String host, int port) throws Exception {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 客户端辅助启动类 对客户端配置
            Bootstrap b = new Bootstrap();
            b.group(group)//
                    .channel(NioSocketChannel.class)//
                    .option(ChannelOption.TCP_NODELAY, true)//
                    .handler(new AppChannelHandler());//
            // 异步链接服务器 同步等待链接成功
            ChannelFuture f = b.connect(host, port).sync();

            // 等待链接关闭
            f.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }

    }

    /**
     * 网络事件处理器
     */
    private class AppChannelHandler extends ChannelInitializer<NioSocketChannel> {
        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            ch.pipeline().addLast(new IdleStateHandler(0,0,5));
            // 添加自定义协议的编解码工具
            ch.pipeline().addLast(new SmartSIMEncoder());
            ch.pipeline().addLast(new SmartSIMDecoder());
            // 处理具体业务数据
            ch.pipeline().addLast(clientBizInboundHandler);
        }

    }

    public static void main(String[] args) throws Exception {
        new Client().connect("10.58.80.79", 5000);
    }

    /**
     * 描述:app 发送数据入口
     * @param msg
     */
    public void snedData(SmartSIMProtocol msg){
        clientBizInboundHandler.writeAndFlush(msg);
    }

}
