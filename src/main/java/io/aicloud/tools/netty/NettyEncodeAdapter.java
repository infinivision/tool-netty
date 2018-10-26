package io.aicloud.tools.netty;

import io.aicloud.tools.netty.util.BytesUtils;
import io.netty.buffer.Unpooled;
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
public class NettyEncodeAdapter extends MessageToMessageEncoder<Object> {
    private AbstractOptions options;

    public NettyEncodeAdapter(AbstractOptions options) {
        this.options = options;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, List<Object> out) throws Exception {
        if (message.getClass() == byte[].class) {
            out.add(Unpooled.wrappedBuffer((byte[]) message));
        } else {
            byte[] data = options.getCodec().encode(message);
            byte[] length = BytesUtils.int2Byte(data.length);
            out.add(Unpooled.wrappedBuffer(2, length, data));
        }
    }
}
