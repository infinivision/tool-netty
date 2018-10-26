package io.aicloud.tools.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Description:
 * <pre>
 * Date: 2018-10-25
 * Time: 15:45
 * </pre>
 *
 * @author fagongzi
 */
public class NettyDecodeAdapter extends MessageToMessageDecoder<ByteBuf> {
    private AbstractOptions options;

    public NettyDecodeAdapter(AbstractOptions options) {
        this.options = options;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (0 == in.readableBytes()) {
            ctx.channel().writeAndFlush(options.getHeartbeat());
            return;
        }

        byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);

        out.add(options.getCodec().decode(data));
    }
}