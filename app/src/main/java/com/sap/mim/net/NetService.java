package com.sap.mim.net;

import com.sap.mim.bean.MessageModel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;

public class NetService {

    private static NetService netService;
    InetSocketAddress remoteAddress = new InetSocketAddress("10.58.80.79", 5000);

    ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;

    private NetService() {
    }

    public static NetService getNetService(){
        if (null == netService){
            synchronized (NetService.class){
                if (null == netService){
                    netService = new NetService();
                    netService.build();
                }
            }
        }
        return netService;
    }

    /**
     * 连接服务器
     *
     */
    public void build() {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        // 客户端辅助启动类 对客户端配置
        Bootstrap strap = new Bootstrap();
        strap.group(group)//
        .channel(NioSocketChannel.class)//
        .option(ChannelOption.TCP_NODELAY, true)//
        .option(ChannelOption.SO_KEEPALIVE, true);

        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(InetSocketAddress key) {
                return new FixedChannelPool(strap.remoteAddress(key), new NettyChannelPoolHandler(), 2);
            }
        };
    }

    public void sendMessageModel(MessageModel messageModel){
        final SimpleChannelPool pool = poolMap.get(remoteAddress);
        Future<Channel> f = pool.acquire();
        f.addListener((FutureListener<Channel>) f1 -> {
            if (f1.isSuccess()) {
                Channel ch = f1.getNow();
                // 获得要发送信息的字节数组
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream       = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(messageModel);
                byte[] content = byteArrayOutputStream.toByteArray();
                SmartSIMProtocol request = new SmartSIMProtocol();
                request.setHead_data(ConstantValue.HEAD_DATA);
                request.setContentLength(content.length);
                request.setContent(content);
                ch.writeAndFlush(request);
                // Release back to pool
                pool.release(ch);
            } else {
                f1.cause().printStackTrace();
            }
        });
    }
}
