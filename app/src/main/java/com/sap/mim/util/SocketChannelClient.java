package com.sap.mim.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SocketChannelClient {



    public void init(){
        try {
            Selector selector = Selector.open();
            SocketChannel clientChannel = SocketChannel.open();
            SocketAddress remoteAddress = new InetSocketAddress("10.58.80.79", 8989);
            clientChannel.configureBlocking(false);
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
                                clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                                selector.wakeup();
                            }

                        }
                    }

                    if (selectionKey.isReadable()){
                        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        clientChannel.read(byteBuffer);
                        System.out.println(Thread.currentThread().getName() + " client read data: " + new String(byteBuffer.array()));
                        byteBuffer.flip();
                        byteBuffer.clear();
                        byteBuffer.put("客户端发送数据使用NIO".getBytes());
                        clientChannel.register(selector, SelectionKey.OP_WRITE, byteBuffer);
                    }

                    if (selectionKey.isWritable()){
                        ByteBuffer src = (ByteBuffer)selectionKey.attachment();
                        if (src == null) continue;
                        clientChannel.write(src);
                        clientChannel.register(selector, SelectionKey.OP_READ);
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

    }

    /**
     * 处理服务推送来的业务数据
     * @param response
     */
    public void handleBizData(Object response){

    }


}
