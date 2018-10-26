package io.aicloud.tools.netty;

import lombok.Getter;

/**
 * 服务
 *
 * @author zhangxu
 */
@Getter
public class Server {
    private String ip;
    private int port;

    public Server(String server) {
        this(server.split(":"));
    }

    public Server(String[] server) {
        this(server[0], Integer.parseInt(server[1]));
    }

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}
