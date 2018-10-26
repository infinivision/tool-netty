package io.aicloud.tools.netty;

import io.aicloud.tools.netty.util.NamedThreadFactory;
import io.netty.channel.EventLoopGroup;
import lombok.Data;

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
@Data
public class ConnectorOptions extends AbstractOptions {
    private boolean allowReconnect = false;
    private int heathCheckInterval = getReadTimeout() / 10;
    private EventLoopGroup group;

    private ConnectHandler connectHandler = new ConnectHandler() {
        @Override
        public void onReconnected(String ip, int port) {
        }

        @Override
        public void onFailed(String ip, int port) {
        }
    };
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("connector"));
}
