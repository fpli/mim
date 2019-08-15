package com.sap.mim.net;

import com.sap.mim.bean.MessageModel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class NetService {

    private static NetService netService;
    private AppChannelHandler appChannelHandler = new AppChannelHandler();

    private NetService() {
    }

    public static NetService getNetService(){
        if (null == netService){
            synchronized (NetService.class){
                if (null == netService){
                    try {
                        netService = new NetService();
                        netService.connect("10.58.80.79", 5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        netService = null;
                    }
                }
            }
        }
        return netService;
    }

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
            .option(ChannelOption.SO_KEEPALIVE, true)
            .handler(appChannelHandler);//
            // 异步链接服务器 同步等待链接成功
            ChannelFuture f = b.connect(host, port).sync();
            // 等待链接关闭
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }

    }

    public void sendMessageModel(MessageModel messageModel){
        try {
            // 获得要发送信息的字节数组
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream       = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(messageModel);
            byte[] content = byteArrayOutputStream.toByteArray();
            SmartSIMProtocol request = new SmartSIMProtocol();
            request.setHead_data(ConstantValue.HEAD_DATA);
            request.setContentLength(content.length);
            request.setContent(content);
            appChannelHandler.sentSmartSIMProtocol(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
