package buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class OrderedByteBufferTest {
    static final String source = "Hello world";

    public static void pooledHeapBufferTest() {
        ByteBuf buf = Unpooled.buffer();

        // deprecated case
        assert ByteOrder.BIG_ENDIAN == buf.order();

        buf.writeShort(1);

        buf.markReaderIndex();
        assert 1 == buf.readShort();

        buf.resetReaderIndex();

        ByteBuf lettleEndianBuf = buf.order(ByteOrder.LITTLE_ENDIAN);
        assert 256 == lettleEndianBuf.readShort();

        // correct case
        buf.writeShort(1);

        buf.markReaderIndex();
        assert 1 == buf.readShort();

        buf.resetReaderIndex();
        assert 1 == buf.readShort();

        buf.resetReaderIndex();

        assert 256 == buf.readShortLE();
    }

    public static void convertNettyBufferToJavaBuffer() {
        ByteBuf buf = Unpooled.buffer(11);

        buf.writeBytes(source.getBytes());
        assert source.equals(buf.toString(Charset.defaultCharset()));

        ByteBuffer nioByteBuffer = buf.nioBuffer();
        assert null != nioByteBuffer;
        assert source.equals(new String(nioByteBuffer.array(),
                                        nioByteBuffer.arrayOffset(), nioByteBuffer.remaining()));
    }

    public static void convertJavaBufferToNettyBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(source.getBytes());
        ByteBuf nettyBuffer = Unpooled.wrappedBuffer(byteBuffer);

        assert source.equals(nettyBuffer.toString(Charset.defaultCharset()));
    }

    public static void main(String[] args) {
        pooledHeapBufferTest();
        convertJavaBufferToNettyBuffer();
        convertNettyBufferToJavaBuffer();
    }
}
