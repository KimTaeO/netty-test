package buffer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;

public class CreateByteBufferTest {
    public static void createTest() {
        CharBuffer heapBuffer = CharBuffer.allocate(11);
        assert 11 == heapBuffer.capacity();
        assert !heapBuffer.isDirect();

        ByteBuffer directBuffer = ByteBuffer.allocateDirect(11);
        assert 11 == directBuffer.capacity();
        assert directBuffer.isDirect();

        int[] array = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0 };
        IntBuffer intHeapBuffer = IntBuffer.wrap(array);
        assert 11 == intHeapBuffer.capacity();
        assert !intHeapBuffer.isDirect();
    }

    public static void main(String[] args) {
        createTest();
    }
}
