package io.aicloud.tools.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Description:
 * <pre>
 * Date: 2018-10-25
 * Time: 15:43
 * </pre>
 *
 * @author fagongzi
 */
public interface Codec<T> {
    /**
     * decoder
     *
     * @param value bytes value
     * @return T value
     */
    T decode(byte[] value);

    /**
     * encoder
     *
     * @param allocator byte buf allocator
     * @param value     T value
     * @return bytes value
     */
    ByteBuf encode(ByteBufAllocator allocator, T value);

}
