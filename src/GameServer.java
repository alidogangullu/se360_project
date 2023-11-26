import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server is running. Waiting for players...");

            // Wait for player 1
            Socket player1Socket = serverSocket.accept();
            System.out.println("Player 1 connected.");

            // Inform player 1 that they are connected
            ObjectOutputStream player1Out = new ObjectOutputStream(player1Socket.getOutputStream());
            player1Out.writeObject("You are player 1. Waiting for player 2.");

            // Wait for player 2
            Socket player2Socket = serverSocket.accept();
            System.out.println("Player 2 connected.");

            // Inform player 2 that they are connected
            ObjectOutputStream player2Out = new ObjectOutputStream(player2Socket.getOutputStream());
            player2Out.writeObject("You are player 2. Game starting.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
