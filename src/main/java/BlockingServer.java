import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingServer {
    public static void main(String[] args) throws Exception {
        BlockingServer server = new BlockingServer();
        server.run();
    }

    private void run() throws IOException {
        ServerSocket server = new ServerSocket(8888);
        System.out.println("접속 대기중");

        while(true) {
            // 블로킹 발생 구간
            Socket sock = server.accept();
            System.out.println("클라이언트 연결됨");

            OutputStream out = sock.getOutputStream();
            InputStream in = sock.getInputStream();

            while (true) {
                try {
                    // 블로킹 발생 구간
                    int request = in.read();
                    // 운영체제의 송신 버퍼에 전송할 데이터를 기록하는 메서드
                    // 송신 버퍼의 잔여 공간 송신할 데이터보다 적다면 공간이 생길 때까지 블로킹된다
                    out.write(request);
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
}
