import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MyClient {

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            while(true) {
                Socket socket = new Socket("127.0.0.1",8899);

                String msg = scanner.next();

                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(msg);

                DataInputStream in = new DataInputStream(socket.getInputStream());
                System.out.println("服务端：" + in.readUTF());

                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
