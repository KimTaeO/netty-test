package buffer;

import java.nio.ByteBuffer;

public class ReadByteBufferTest {
    public static void readTest() {
        byte[] tempArray = { 1, 2, 3, 4, 5, 0, 0, 0, 0, 0, 0 };
        ByteBuffer firstBuffer = ByteBuffer.wrap(tempArray);
        assert 0 == firstBuffer.position();
        assert 11 == firstBuffer.limit();

        assert 1 == firstBuffer.get();
        assert 2 == firstBuffer.get();
        assert 3 == firstBuffer.get();
        assert 4 == firstBuffer.get();
        assert 4 == firstBuffer.position();
        assert 11 == firstBuffer.limit();

        firstBuffer.flip();
        assert 0 == firstBuffer.position();
        assert 4 == firstBuffer.limit();

        firstBuffer.get(3);
        assert 0 == firstBuffer.position();
    }

    public static void main(String[] args) {
        readTest();
    }
}
