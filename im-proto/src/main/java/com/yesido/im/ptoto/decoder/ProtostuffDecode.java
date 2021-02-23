package com.yesido.im.ptoto.decoder;

import java.util.List;

import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.yesido.im.ptoto.netty.NettyMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * Protostuff解码器
 * 
 * @author yesido
 * @date 2018年9月1日
 */
public class ProtostuffDecode extends MessageToMessageDecoder<ByteBuf> {

    private RuntimeSchema<NettyMessage> schema = RuntimeSchema
            .createFrom(NettyMessage.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf,
            List<Object> paramList) throws Exception {
        final int length = byteBuf.readableBytes();
        byte[] array = new byte[length];
        byteBuf.getBytes(byteBuf.readerIndex(), array, 0, length);

        NettyMessage message = schema.newMessage(); // 创建空对象
        ProtostuffIOUtil.mergeFrom(array, message, schema); // 反序列化，protocol被赋值
        paramList.add(message);
    }

}
