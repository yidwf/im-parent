package com.yesido.im.ptoto.encoder;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.yesido.im.ptoto.netty.NettyMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Protostuff编码器
 * 
 * @author yesido
 * @date 2018年9月1日
 */
public class ProtostuffEncoder extends MessageToByteEncoder<NettyMessage> {
    private RuntimeSchema<NettyMessage> schema = RuntimeSchema
            .createFrom(NettyMessage.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage message,
            ByteBuf out) throws Exception {
        byte[] bytes = ProtostuffIOUtil.toByteArray(message, schema,
                LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        out.writeBytes(bytes);
    }

}
