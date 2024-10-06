package security;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.File;

public class HttpSnoopServer {
    private static final int PORT = 8443;

    public static void main(String[] args) throws Exception {
        SslContext sslCtx = null;

        try {
            File certChainFile = new File("netty.crt");
            File keyFile = new File("privatekey.pem");

            sslCtx = SslContextBuilder.forServer(certChainFile, keyFile, "qwer").build();
        } catch (SSLException e) {
            e.printStackTrace();
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpSnoopServerInitializer(sslCtx));

            Channel ch = b.bind(PORT).sync().channel();

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
