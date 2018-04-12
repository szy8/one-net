package com.weyong.onenet.server.context;

import com.weyong.onenet.server.OneNetServer;
import com.weyong.onenet.server.config.OneNetServerContextConfig;
import com.weyong.onenet.server.config.OneNetServerHttpContextConfig;
import com.weyong.onenet.server.handler.InternetChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by hao.li on 4/10/2018.
 */
@Slf4j
public class OneNetServerHttpContext extends OneNetServerContext {
    private static boolean tcp80Initialed = false;
    public OneNetServerHttpContext(OneNetServerHttpContextConfig oneNetServerContextConfig, OneNetServer oneNetServer) {
        super();
        this.setOneNetServerContextConfig(oneNetServerContextConfig);
        this.setOneNetConnectionManager(oneNetServer.getOneNetConnectionManager());
        if(!tcp80Initialed) {
            outsideBootstrap.group(OneNetServer.bossGroup, OneNetServer.workerGroup);
            outsideBootstrap.channel(NioServerSocketChannel.class)
                    .childHandler(new InternetChannelInitializer(this))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            try {
                outsideBootstrap.bind(oneNetServerContextConfig.getInternetPort()).sync();
            } catch (InterruptedException ex) {
                log.error(String.format("Start Context %s failed. ex is : %s",
                        oneNetServerContextConfig.getContextName(),
                        ex.getMessage()));
            }
        }
        log.info(String.format("OneNet Server Http Context: %s started, domain regex: [%s]",
                oneNetServerContextConfig.getContextName(),
                StringUtils.join(oneNetServerContextConfig.getDomainRegExs()),","));
    }
}