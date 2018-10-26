package io.aicloud.tools.netty;

import io.netty.channel.Channel;

/**
 * Description:
 * <pre>
 * Date: 2018-10-25
 * Time: 15:02
 * </pre>
 *
 * @author fagongzi
 */
public interface TimeoutHandler {
    /**
     * read timeout
     *
     * @param channel channel
     */
    void onReadTimeout(Channel channel);

    /**
     * write timeout
     *
     * @param channel channel
     */
    void onWriteTimeout(Channel channel);

    /**
     * read and write all timeout
     *
     * @param channel channel
     */
    void onAllTimeout(Channel channel);
}
