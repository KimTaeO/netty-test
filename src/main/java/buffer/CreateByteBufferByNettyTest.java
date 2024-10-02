package buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;

public class CreateByteBufferByNettyTest {
    public static void createUnpooledHeapBufferTest() {
        ByteBuf buf = Unpooled.buffer(11);

        testBuffer(buf, false);
    }

    public static void createUnpooledDirectBufferTest() {
        ByteBuf buf = Unpooled.directBuffer(11);

        testBuffer(buf, true);
    }

    public static void createPooledHeapBufferTest() {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.heapBuffer(11);

        testBuffer(buf, false);
    }

    public static void createPooledDirectBufferTest() {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer(11);

        testBuffer(buf, true);
    }

    private static void testBuffer(ByteBuf buf, boolean isDirect) {
        assert 11 == buf.capacity();

        assert isDirect == buf.isDirect();

        assert 0 == buf.readableBytes();
        assert 11 == buf.writableBytes();
    }

    public static void main(String[] args) {
        createUnpooledHeapBufferTest();
        createUnpooledDirectBufferTest();
        createPooledHeapBufferTest();
        createPooledDirectBufferTest();
    }
}
