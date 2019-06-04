package com.sap.mim.util;

import com.sap.mim.entity.MessageInfo;
import com.sap.mim.service.SimpleClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class SocketChannelClient {

    private SocketChannel clientChannel;
    private Selector selector;

    public void init(){
        try {
            selector = Selector.open();
            clientChannel = SocketChannel.open();
            SocketAddress remoteAddress = new InetSocketAddress("10.58.80.79", 8989);
            clientChannel.configureBlocking(false);//设置为非阻塞模式
            clientChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            clientChannel.connect(remoteAddress);
            clientChannel.register(selector, SelectionKey.OP_CONNECT);
            while (true){
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (selectionKey.isValid() && selectionKey.isConnectable()){
                        if (clientChannel.isConnectionPending()){
                            if (clientChannel.finishConnect()){
                                String msg = "客户端发送数据使用NIO" + Math.random();
                                clientChannel.write(ByteBuffer.wrap(msg.getBytes()));
                                clientChannel.register(selector, SelectionKey.OP_READ);
                            }

                        }
                    }

                    if (selectionKey.isReadable()){
                        try {
                            final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                            clientChannel.read(byteBuffer);
                            // 判断读到的数据是不是协议报文头部
                            byteBuffer.flip();
                            byteBuffer.clear();
                            String content = new String(byteBuffer.array(),"UTF-8");
                            MessageInfo messageInfo = new MessageInfo();
                            messageInfo.setContent(content);
                            messageInfo.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                            EventBus.getDefault().post(messageInfo);
                            // 读取协议报文body部分
                            //int length = byteBuffer.getInt(12);
                            //ByteBuffer dataBuffer = ByteBuffer.allocate(length);
                            clientChannel.register(selector, selectionKey.interestOps() | SelectionKey.OP_READ);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (selectionKey.isWritable() && selectionKey.isValid()){
                        ByteBuffer src = (ByteBuffer)selectionKey.attachment();
                        if (src == null) continue;
                        clientChannel.write(src);
                        clientChannel.register(selector, selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
                    }


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }


    }

    /**
     * 推送数据到服务器
     * @param request
     */
    public void sendBizData(Object request){
        if (clientChannel.isConnected()){
            try {
                if (request instanceof String){//各种类型判断
                    String className = request.getClass().getName();
                    ByteBuffer att = ByteBuffer.wrap(((String)request).getBytes());
                    clientChannel.register(selector, clientChannel.keyFor(selector).interestOps() | SelectionKey.OP_WRITE, att);
                }
            } catch (ClosedChannelException e) {
                try {
                    clientChannel.close();
                } catch (IOException ex) {
                    // ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 处理服务推送来的业务数据
     * @param response
     */
    public void handleBizData(Object response){

    }

    public void initNettyClient(){
        // 1 连接服务端
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(nioEventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.handler(new ChannelInitializer<io.netty.channel.socket.nio.NioSocketChannel>() {
                @Override
                protected void initChannel(io.netty.channel.socket.nio.NioSocketChannel ch) throws Exception {
                    ChannelPipeline channelPipeline = ch.pipeline();
                    channelPipeline.addLast(new SimpleClientHandler());

                }
            });
            ChannelFuture channelFuture = bootstrap.connect("10.58.80.79", 8989).sync();
            if (channelFuture.isSuccess()){
                System.out.println("----------<-------->-----------");
            }
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            nioEventLoopGroup.shutdownGracefully();
        }
    }
}
