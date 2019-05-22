package com.sap.mim.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


/**
 * 描述: 这个服务处理网络数据
 * 客户端推送数据
 * 处理服务端推送来的数据
 */
public class HandleNetDataService extends Service {

    public HandleNetDataService() {

    }

    /**
     * 服务的生命周期方法 --> 创建服务实例
     * 连接服务端
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // 1 连接服务端
        try {
            Selector selector = Selector.open();
            SocketChannel clientChannel = SocketChannel.open();
            SocketAddress remoteAddress = new InetSocketAddress("10.58.80.79", 8989);
            clientChannel.configureBlocking(false);
            clientChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            clientChannel.connect(remoteAddress);
            clientChannel.register(selector, SelectionKey.OP_CONNECT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务的生命周期方法 --> 绑定服务
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 服务的生命周期方法 --> 解绑服务
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    /**
     * 服务的生命周期方法 --> 销毁服务实例
     * 断开与服务端的连接
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
