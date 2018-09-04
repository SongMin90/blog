import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer implements Runnable {

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8899);
            while(true) {
                Socket socket = serverSocket.accept();

                // 接收客户端消息
                DataInputStream in = new DataInputStream(socket.getInputStream());
                String msg = in.readUTF();
                System.out.println("客户端：" + msg);

                // 发送客户端消息
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(msg);

                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MyServer().run();
    }

}
