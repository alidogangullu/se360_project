import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    private void inGameProcess(Player player1, Player player2) throws IOException {

        PrintWriter player1Out = null;
        PrintWriter player2Out = null;

        player1Out = new PrintWriter(player1.getPlayerSocket().getOutputStream(), true);
        player1Out.println("PlayerNo=1/" + "OpponentName=" + player2.getNickName() + "/");

        player2Out = new PrintWriter(player2.getPlayerSocket().getOutputStream(), true);
        player2Out.println("PlayerNo=2/" + "OpponentName=" + player1.getNickName() +"/");

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
            Player player1 = new Player(player1Socket);
            System.out.println("Player 1 connected.");
            // Inform player 1
            ObjectOutputStream player1Out = new ObjectOutputStream(player1Socket.getOutputStream());
            player1Out.writeObject("You are player 1. Waiting for player 2.");


            // Wait for player 2
            Socket player2Socket = serverSocket.accept();
            Player player2 = new Player(player2Socket);
            System.out.println("Player 2 connected.");
            // Inform player 2
            ObjectOutputStream player2Out = new ObjectOutputStream(player2Socket.getOutputStream());
            player2Out.writeObject("You are player 2. Game starting.");

            //Start Game
            inGameProcess(player1, player2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
       GameServer server = new GameServer();
       server.connectPlayer();
    }
}

class Player{
    private Socket playerSocket;
    private String nickName;

    public Player(Socket playerSocket) {
        this.playerSocket = playerSocket;
        try {
            nickName = handlePlayerInfo();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getPlayerSocket() {
        return playerSocket;
    }

    public String getNickName() {
        return nickName;
    }

    private String handlePlayerInfo() throws IOException {
        BufferedReader playerIn = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
        String playerMessage = playerIn.readLine();
        String[] playerInfo = playerMessage.split("/");

        String opponentName = "";
        for (String part : playerInfo) {
            if (part.startsWith("PlayerNickname=")) {
                opponentName = part.split("=")[1].trim();
                break;
            }
        }
        return opponentName;
    }
}
