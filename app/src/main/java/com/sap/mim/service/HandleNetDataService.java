package com.sap.mim.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.sap.mim.entity.BusinessPackets;
import com.sap.mim.entity.HeartBeatPackets;
import com.sap.mim.inteface.TCPHandle;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;


/**
 * 描述: 这个服务处理网络数据
 * 客户端推送数据
 * 处理服务端推送来的数据
 */
public class HandleNetDataService extends Service {

    private static final String TAG = "HandleNetDataService";

    private volatile boolean busy;//不是繁忙状态就周期性的推送数据包

    /**
     * 描述:处理IO操作 任务分两大类:读数据|写数据
     */
    private static ThreadPoolExecutor ioThreadPool;

    /**
     * 描述:这个线程池推送心跳包到服务器，以及确认ack
     */
    private static ScheduledExecutorService heartBeatThreadPool = Executors.newSingleThreadScheduledExecutor();

    public HandleNetDataService() {

    }

    private MyBinder iBinder = new MyBinder();

    /**
     * 服务的生命周期方法 --> android系统调用
     * 连接服务端
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // 1 连接服务端
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        try {
            Selector selector = Selector.open();
            SocketChannel clientChannel = SocketChannel.open();
            SocketAddress remoteAddress = new InetSocketAddress("10.58.80.79", 8989);
            clientChannel.configureBlocking(false);
            clientChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            clientChannel.connect(remoteAddress);
            clientChannel.register(selector, SelectionKey.OP_CONNECT);
            BlockingQueue workQueue = new ArrayBlockingQueue(200,true);
            ioThreadPool = new ThreadPoolExecutor(5,10, 10, TimeUnit.MINUTES, workQueue);

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(nioEventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
            bootstrap.handler(new ChannelInitializer<io.netty.channel.socket.SocketChannel>() {
                @Override
                protected void initChannel(io.netty.channel.socket.SocketChannel ch) throws Exception {
                    ChannelPipeline channelPipeline = ch.pipeline();
                    channelPipeline.addLast(new SimpleClientHandler());
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("10.58.80.79", 8989).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            nioEventLoopGroup.shutdownGracefully();
        }

        Notification notification = new Notification();
        // 把服务提升为前台进程,因为activity在后台运行 moveTaskToBack;
        startForeground(1000, notification);
    }

    /**
     * 服务的生命周期方法 --> activity绑定服务时Android系统调用service的 onCreate() onBind()
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    /**
     * 服务的生命周期方法 --> activity解绑服务时Android系统调用service的onUnbind()
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "activity与服务解除绑定");
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


    private class MyBinder extends Binder implements TCPHandle{

        @Override
        public int sendHeartBeatMessage(HeartBeatPackets message) {
            Callable task = ()-> {

                    return null;

            };
            ioThreadPool.submit(task);
            return 0;
        }

        @Override
        public int sengBusinessPackets(BusinessPackets message) {
            return 0;
        }
    }
}
