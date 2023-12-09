import java.io.IOException;
import java.net.Socket;

public class App {
    public static void main(String[] args) {

        User adg = new User("ADG");
        User hediye = new User("ArmAras");

        Socket socket;
        try {
            socket = new Socket("localhost", 12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Connected to server.");

        //GameClient gameClient = new GameClient(adg,socket);
        GameClient gameClient = new GameClient(hediye, socket);

    }
}
