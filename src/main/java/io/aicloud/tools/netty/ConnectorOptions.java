package io.aicloud.tools.netty;

import io.aicloud.tools.netty.util.NamedThreadFactory;
import io.netty.channel.EventLoopGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Description:
 * <pre>
 * Date: 2018-10-25
 * Time: 17:04
 * </pre>
 *
 * @author fagongzi
 */
@Getter
@Setter
class ConnectorOptions<T> extends AbstractOptions<T> {
    private boolean allowReconnect = false;
    private int heathCheckInterval = getReadTimeout() / 10;
    private EventLoopGroup group;

    private ConnectHandler connectHandler = new ConnectHandler() {
        @Override
        public void onReconnected(Connector connector, String ip, int port) {
        }

        @Override
        public void onFailed(Connector connector, String ip, int port) {
        }
    };
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("connector"));
}
