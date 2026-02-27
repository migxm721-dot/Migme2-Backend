package net.migxchat.gateway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TcpSocketServer {
    private static final Logger log = LoggerFactory.getLogger(TcpSocketServer.class);

    @Value("${fusion.tcp.port:9119}")
    private int port;

    @Value("${fusion.tcp.boss-threads:2}")
    private int bossThreads;

    @Value("${fusion.tcp.worker-threads:8}")
    private int workerThreads;

    @Value("${fusion.tcp.max-frame-size:1048576}")
    private int maxFrameSize;

    @Autowired
    private NettyServerHandler nettyServerHandler;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    @PostConstruct
    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(bossThreads);
        workerGroup = new NioEventLoopGroup(workerThreads);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                new LengthFieldBasedFrameDecoder(maxFrameSize, 1, 4, 0, 0),
                                nettyServerHandler
                        );
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);

        ChannelFuture future = bootstrap.bind(port).sync();
        serverChannel = future.channel();
        log.info("TCP Socket Server started on port {}", port);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down TCP Socket Server...");
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (workerGroup != null) workerGroup.shutdownGracefully();
        if (bossGroup != null) bossGroup.shutdownGracefully();
    }
}
