package io.aicloud.tools.netty;

import lombok.Getter;
import lombok.Setter;

/**
 * Description:
 * <pre>
 * Date: 2018-10-25
 * Time: 14:36
 * </pre>
 *
 * @author fagongzi
 */
@Getter
@Setter
public class Options extends AbstractOptions {
    private int ioThreads = Runtime.getRuntime().availableProcessors();
    private boolean allowTimeout = true;
    private TimeoutHandler timeoutHandler;
}
