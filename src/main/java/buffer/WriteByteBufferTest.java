package buffer;

import java.nio.ByteBuffer;

public class WriteByteBufferTest {
    public static void writeTest() {
        ByteBuffer firstBuffer = ByteBuffer.allocateDirect(11);
        assert 0 == firstBuffer.position();
        assert 11 == firstBuffer.limit();

        firstBuffer.put((byte) 1);
        firstBuffer.put((byte) 2);
        firstBuffer.put((byte) 3);
        firstBuffer.put((byte) 4);
        assert 4 == firstBuffer.position();
        assert 11 == firstBuffer.limit();

        firstBuffer.flip();
        assert 0 == firstBuffer.position();
        assert 4 == firstBuffer.limit();
    }

    public static void main(String[] args) {
        writeTest();
    }
}
