import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NonBlockingServer {
    private final Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
    private final ByteBuffer buffer = ByteBuffer.allocate(2 * 1024);

    private void startEchoServer() throws IOException {
        try(
                // 자바의 NIO 컴푸넌트 중 하나인 Selector는 자신에게 등록된 채널에 변경 사항이 발생했는지 검사하고 변경 사항이 발생한 채널에 대한 접근을 가능하게 해준다
                Selector selector = Selector.open();

                // 블로킹 소켓과 대응되는 논블로킹 서버 소켓 채널을 생성한다 블로킹 소켓과는 다르게 채널을 생성한 후 포트를 바인딩한다
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()
        ) {

            if(serverSocketChannel.isOpen() && selector.isOpen()) { // 생성한 Selector와 Channel이 생성이 잘 되었는지 확인한다
                // 소켓 채널의 모드는 기본이 블로킹이기 때문에 메서드를 호출해 논블로킹 모드로 변경해주어야 한다
                serverSocketChannel.configureBlocking(false);

                // 클라이언트의 연결을 받을 포트를 지정하고 채널에 할당한다
                serverSocketChannel.bind(new InetSocketAddress(8888));

                // ServerSocketChannel 객체를 Selector에 등록한다. Selector가 감지할 이벤트는 연결 요청에 해당하는 SelectionKey.OP_ACCEPT이다
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                System.out.println("접속 대기중");

                while(true) {
                    // Selector에 등록된 채널에서 변경 사항이 발생했는지 검사한다. Selector에 아무런 I/O 이벤트가 발생하지 않는다면 스레드는 이 부분에서 블로킹된다. 만약에 블로킹을 피하고 싶다면 selectNow() 메서드를 사용하자.
                    selector.select();
                    // Selector에 등록된 채널 중에서 I/O 이벤트가 발생한 채널들의 목록을 조회한다
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                    while(keys.hasNext()) {
                        SelectionKey key = (SelectionKey) keys.next();
                        // 조회된 I/O 이벤트들 중 동일한 이벤트가 감지되는 것을 방지하기 위해서 조회된 목록에서 제거한다
                        keys.remove();

                        if(!key.isValid()) {
                            continue;
                        }

                        // 발생한 이벤트의 종류가 연결 요청인지 확인하고 맞다면 연결 요청을 처리하는 메서드를 호출한다
                        if(key.isAcceptable()) {
                            acceptOP(key, selector);
                        }
                        // 발생한 이벤트의 종류가 데이터 수신 요청인지 확인하고 맞다면 데이터 수신을 처리하는 메서드를 호출한다
                        else if (key.isReadable()) {
                            readOP(key);
                        }
                        // 발생한 이벤트의 종류가 데이터 송신인지 확인하고 맞다면 데이터를 송신하는 메서드를 호출한다
                        else if (key.isWritable()) {
                            writeOP(key);
                        } else {
                            System.err.println("서버 소켓 생성 실패.");
                        }
                    }
                }
            }
        } catch(IOException ex) {
            System.err.println(ex);
        }
    }

    private void acceptOP(SelectionKey key, Selector selector) throws IOException {
        // 연결 요청 이벤트가 발생한 채널은 항상 ServerSocketChannel이므로 이벤트가 발생한 채널을 ServerSocketChannel로 캐스팅한다
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        // ServerSocketChannel을 사용하여 클라이언트의 연결을 수락하고 연결된 소켓 채널을 가져온다
        SocketChannel socketChannel = serverChannel.accept();
        // 연결된 클라이언트 소켓 채널을 논블로킹 모드로 설정한다
        socketChannel.configureBlocking(false);

        System.out.println("클라이언트 연결됨 : " + socketChannel.getRemoteAddress());

        keepDataTrack.put(socketChannel, new ArrayList<byte[]>());
        // 클라이언트 소켓 채널을 Selector에 등록하여 I/O 이벤트를 감시한
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void readOP(SelectionKey key) {
        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            buffer.clear();
            int numRead = -1;
            try {
                numRead = socketChannel.read(buffer);
            } catch (IOException e) {
                System.err.println("데이터 읽기 에러!");
            }

            if (numRead == -1) {
                keepDataTrack.remove(socketChannel);
                System.out.println("클라이언트 연결 종료 : " + socketChannel.getRemoteAddress());
                socketChannel.close();
                key.cancel();
                return;
            }
            byte[] data = new byte[numRead];
            System.arraycopy(buffer.array(), 0, data, 0, numRead);
            System.out.println(Arrays.toString(data));

            doEchoJob(key, data);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private void writeOP(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        List<byte[]> channelData = keepDataTrack.get(socketChannel);
        Iterator<byte[]> its = channelData.iterator();

        while(its.hasNext()) {
            byte[] it = its.next();
            its.remove();
            socketChannel.write(ByteBuffer.wrap(it));
        }

        key.interestOps(SelectionKey.OP_READ);
    }

    private void doEchoJob(SelectionKey key, byte[] data) {
        SocketChannel socketChannel= (SocketChannel) key.channel();
        List<byte[]> channelData = keepDataTrack.get(socketChannel);
        channelData.add(data);

        key.interestOps(SelectionKey.OP_WRITE);
    }

    public static void main(String[] args) throws IOException {
        NonBlockingServer main = new NonBlockingServer();
        main.startEchoServer();
    }
}
