package io.aicloud.tools.netty;

/**
 * Description:
 * <pre>
 * Date: 2018-10-26
 * Time: 8:37
 * </pre>
 *
 * @author fagongzi
 */
public interface IOSession<T> {
    boolean write(T value);

    boolean writeAndFlush(T value);

    boolean flush();

    boolean isConnected();

    void close();
}
