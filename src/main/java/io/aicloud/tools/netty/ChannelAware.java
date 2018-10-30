package io.aicloud.tools.netty;

import io.netty.channel.Channel;

/**
 * Description:
 * <pre>
 * Date: 2018-10-25
 * Time: 15:06
 * </pre>
 *
 * @author fagongzi
 */
public interface ChannelAware<T> {
    /**
     * recv a message
     *
     * @param channel channel
     * @param message message
     */
    void messageReceived(Channel channel, T message);

    /**
     * uncaught exception
     *
     * @param channel channel
     * @param cause   exception
     */
    void onChannelException(Channel channel, Throwable cause);

    /**
     * channel closed
     *
     * @param channel channel
     */
    void onChannelClosed(Channel channel);

    /**
     * channel connected
     *
     * @param channel channel
     */
    void onChannelConnected(Channel channel);
}
