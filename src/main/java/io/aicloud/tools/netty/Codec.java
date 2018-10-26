package io.aicloud.tools.netty;

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
     * @param value T value
     * @return bytes value
     */
    byte[] encode(T value);

}
