package com.sap.mim.net;

import com.sap.mim.bean.MessageModel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;

public class NetService2 {

    private static NetService2 netService2;

    private static final InetSocketAddress remoteAddress = new InetSocketAddress("10.58.80.79", 5000);

    private static Channel channel;

    private NetService2() {
    }

    public static NetService2 getNetService2(){
        if (null == netService2){
            synchronized (NetService2.class){
                if (null == netService2){
                    netService2 = new NetService2();
                    netService2.build();
                }
            }
        }
        return netService2;
    }

    private void build(){
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 客户端辅助启动类 对客户端配置
            Bootstrap strap = new Bootstrap();
            strap.group(group)//
                    .channel(NioSocketChannel.class)//
                    .handler(new NetChannelInitializer())
                    .option(ChannelOption.TCP_NODELAY, true)//
                    .option(ChannelOption.SO_KEEPALIVE, true);
            channel = strap.connect(remoteAddress).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void sendMessageModel(MessageModel messageModel){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream       = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(messageModel);
            byte[] content = byteArrayOutputStream.toByteArray();
            SmartSIMProtocol request = new SmartSIMProtocol();
            request.setHead_data(ConstantValue.HEAD_DATA);
            request.setContentLength(content.length);
            request.setContent(content);
            channel.writeAndFlush(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
