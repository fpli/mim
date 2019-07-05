package com.sap.mim.service;

import com.sap.mim.entity.MessageInfo;
import com.sap.mim.util.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.greenrobot.eventbus.EventBus;

/**
 * 描述:通道入站数据处理器
 */
public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("SimpleClientHandler.channelRead");
        if (msg instanceof  ByteBuf){
            ByteBuf result = (ByteBuf) msg;
            byte[] result1 = new byte[result.readableBytes()];
            result.readBytes(result1);
            System.out.println("Server said:" + new String(result1));
            result.release();
            String content = new String(result1,"UTF-8");
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setContent(content);
            messageInfo.setType(Constants.CHAT_ITEM_TYPE_LEFT);
            EventBus.getDefault().post(messageInfo);
            ctx.fireChannelRead(new Object());// 把解码后数据传递给下一个通道处理器处理,进行数据传递
        } else {

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
        //ctx.fireExceptionCaught(cause);
    }


    // 通道激活事件
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf buf = Unpooled.buffer();
        String msg = "hello Server!";
        buf.writeBytes(msg.getBytes());
        ctx.write(buf);
        ctx.flush();
    }

}
