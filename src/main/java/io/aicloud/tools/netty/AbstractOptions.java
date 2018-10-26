package io.aicloud.tools.netty;

import lombok.Data;

/**
 * Description:
 * <pre>
 * Date: 2018-10-25
 * Time: 17:03
 * </pre>
 *
 * @author fagongzi
 */
@Data
public abstract class AbstractOptions {
    private boolean debug = false;
    private int socketBacklog = 100;
    private int socketReadBuffer = 1024;
    private int socketWriteBuffer = 1024;
    private int socketTimeout = 10000;
    private int maxBodySize = 10 * 1024 * 1024;
    private int readTimeout = 30;
    private int writeTimeout = 30;
    private int allTimeout = Math.max(getReadTimeout(), getWriteTimeout());
    private Object heartbeat;
    private Codec codec;
    private ChannelAware channelAware;
}
