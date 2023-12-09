import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    private void inGameProcess(Socket player1Socket, Socket player2Socket) throws IOException {
        PrintWriter player1Out = null;
        PrintWriter player2Out = null;

        player1Out = new PrintWriter(player1Socket.getOutputStream(), true);
        player1Out.println("PlayerNo=1/" + "OpponentName=" + "Player 2 Name/");
        player1Out = new PrintWriter(player2Socket.getOutputStream(), true);
        player1Out.println("PlayerNo=2/" + "OpponentName=" + "Player 1 Name/");

        while (true) {

            //oyun ici

        }
    }

    private void connectPlayer(){
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

            inGameProcess(player1Socket, player2Socket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
       GameServer server = new GameServer();
       server.connectPlayer();
    }
}
