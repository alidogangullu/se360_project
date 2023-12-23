import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameLobby extends JFrame{
    public static Socket socket;
    private JPanel lobbyPanel;
    private JButton gameSearchButton;
    private JLabel userInfoLabel;

    BufferedReader in;
    PrintWriter out;

    GameLobby(){
        setContentPane(lobbyPanel);
        setTitle("Finger Game");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        userInfoLabel.setText("Welcome " + Main.user.getUsername() + ", Rank Point: " + Main.user.getRating());
        this.setVisible(true);

        gameSearchButton.addActionListener(e -> {
            searchGame();
            userInfoLabel.setText("Welcome " + Main.user.getUsername() + ", Rank Point: " + Main.user.getRating());
        });
    }

    private void searchGame(){
        try {
            socket = new Socket("localhost", 12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Connected to server.");

        String gameConnectionLogs;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            //send player info
            out.println("PlayerNickname=" + Main.user.getUsername() + "/" + "PlayerRating=" + Main.user.getRating() + "/");

            gameConnectionLogs = in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int playerNo = 0;
        int opponentRating = 0;
        String opponentNameText = "";

        String[] parts = gameConnectionLogs.split("/");

        for (String part : parts) {
            if (part.contains("PlayerNo")) {
                playerNo = Integer.parseInt(part.split("=")[1].trim());
            } else if (part.contains("OpponentName")) {
                opponentNameText = part.split("=")[1].trim();

            }
            else if (part.contains("OpponentRating")) {
                opponentRating = Integer.parseInt(part.split("=")[1].trim());

            }
        }
        this.setVisible(false);

        String finalOpponentNameText = opponentNameText;
        int finalPlayerNo = playerNo;
        int finalOpponentRating = opponentRating;
        new Thread(() -> {
            GameClient gameClient = new GameClient(finalOpponentNameText, finalPlayerNo, finalOpponentRating);
        }).start();
    }
}
