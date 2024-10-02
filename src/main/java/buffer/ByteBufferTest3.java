package buffer;

import java.nio.ByteBuffer;

public class ByteBufferTest3 {

    // 데이터를 조회 시 put 한 1을 출력할 것으로 예상
    // 의도한 대로 동작하지 않음
    public static void test1() {
        ByteBuffer firstBuffer = ByteBuffer.allocate(11);
        System.out.println("초기 상태 : " + firstBuffer);

        firstBuffer.put((byte) 1);
        System.out.println(firstBuffer.get());
        System.out.println(firstBuffer);
    }

    public static void test2() {
        ByteBuffer firstBuffer = ByteBuffer.allocate(11);
        System.out.println("초기 상태 : " + firstBuffer);

        firstBuffer.put((byte) 1);
        firstBuffer.put((byte) 2);
        assert 2 == firstBuffer.position();

        firstBuffer.rewind();
        assert 0 == firstBuffer.position();

        assert 1 == firstBuffer.get();
        assert 1 == firstBuffer.position();

        System.out.println(firstBuffer);
    }

    public static void main(String[] args) {
        test1();
        test2();
    }
}
