package bootstrap.server;

import echo.server.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServerV3 {
    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 연결된 소켓에 대한 I/O를 자식 스레드들의 이벤트를 핸들링한
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new EchoServerV3FirstHandler());
                            p.addLast(new EchoServerV3SecondHandler());
                        }
                    });

            ChannelFuture f = b.bind(8888).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

/*
네티 통신 과정 : 소켓 채널 접속 요청 -> 소켓 채널 이벤트 루프 등록 -> 소켓 채널 활성화 -> 데이터 송수신

// 해당 소켓 채널이 이벤트 루프에 등록되었다는 이벤트를 출력하는 로그이다
9월 28, 2024 2:28:27 오후 io.netty.handler.logging.LoggingHandler channelRegistered
정보: [id: 0x1e57dee2, /0:0:0:0:0:0:0:1:57770 => /0:0:0:0:0:0:0:1:8888] REGISTERED

// 해당 소켓 채널이 활성화되었다는 이벤트를 출력하는 로그이다
9월 28, 2024 2:28:27 오후 io.netty.handler.logging.LoggingHandler channelActive
정보: [id: 0x1e57dee2, /0:0:0:0:0:0:0:1:57770 => /0:0:0:0:0:0:0:1:8888] ACTIVE

// 연결된 클라이언트가 전송한 문자열을 수신했다는 이벤트를 출력하는 로그이다
9월 28, 2024 2:28:29 오후 io.netty.handler.logging.LoggingHandler logMessage
정보: [id: 0x1e57dee2, /0:0:0:0:0:0:0:1:57770 => /0:0:0:0:0:0:0:1:8888] RECEIVED: 7B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 68 65 6c 6c 6f 0d 0a                            |hello..         |
+--------+-------------------------------------------------+----------------+

// EchoServerHandler가 클라이언트로부터 수신한 데이터를 클라이언트에게 전송하기 위한 이벤트 호출
// 데이터를 클라이언트에게 전송하기 위해 데이터를 Buffer에 쓰는 이벤트를 출력하는 로그이다
9월 28, 2024 2:28:29 오후 io.netty.handler.logging.LoggingHandler logMessage
정보: [id: 0x1e57dee2, /0:0:0:0:0:0:0:1:57770 => /0:0:0:0:0:0:0:1:8888] WRITE: 7B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 68 65 6c 6c 6f 0d 0a                            |hello..         |
+--------+-------------------------------------------------+----------------+

// Buffer에 쓰여진 데이터들을 클라이언트에게 전송하기 위한 이벤트를 출력하는 로그이다.
9월 28, 2024 2:28:29 오후 io.netty.handler.logging.LoggingHandler flush
정보: [id: 0x1e57dee2, /0:0:0:0:0:0:0:1:57770 => /0:0:0:0:0:0:0:1:8888] FLUSH
 */