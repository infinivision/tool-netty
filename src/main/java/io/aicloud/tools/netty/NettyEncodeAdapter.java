package io.aicloud.tools.netty;

import io.aicloud.tools.netty.util.BytesUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Description:
 * <pre>
 * Date: 2018-10-25
 * Time: 15:51
 * </pre>
 *
 * @author fagongzi
 */
public class NettyEncodeAdapter<T> extends MessageToMessageEncoder<Object> {
    private AbstractOptions<T> options;

    @SuppressWarnings("unchecked")
    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, List<Object> out) throws Exception {
        if (message.getClass() == byte[].class) {
            out.add(message);
        } else {
            CompositeByteBuf buf = ctx.alloc().compositeBuffer();
            int writeIndex = 0;

            ByteBuf data = options.getCodec().encode(ctx.alloc(), (T) message);
            buf.addComponent(data);
            writeIndex += data.readableBytes();
            buf.writerIndex(writeIndex);

            data = ctx.alloc().buffer().writeBytes(BytesUtils.int2Byte(data.readableBytes()));
            buf.addComponent(0, data);
            writeIndex += data.readableBytes();
            buf.writerIndex(writeIndex);

            out.add(buf);
        }
    }

    NettyEncodeAdapter(AbstractOptions<T> options) {
        this.options = options;
    }
}
