package echo.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    // 데이터 수신 이벤트 처리 메서드이다.
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());

        System.out.println("수신한 문자열 [" + readMessage + ']');

        // 채널 파이프라인에 대한 이벤트를 처리한다.
        ctx.write(msg);
    }

    @Override
    // channelRead의 이벤트 처리가 끝나면 호출되는 메서드이다.
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // 채널 파이프라인에 저장된 버퍼를 전송한다.
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
