import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class GameClient extends JFrame {

    Socket socket;

    ImageIcon one = new ImageIcon("images/1.png");
    ImageIcon two = new ImageIcon("images/2.png");
    ImageIcon three = new ImageIcon("images/3.png");
    ImageIcon four = new ImageIcon("images/4.png");
    ImageIcon five = new ImageIcon("images/5.png");

    //hands
    private JLabel opponentFirst;
    private JLabel playerFirst;
    private JLabel opponentSecond;
    private JLabel playerSecond;
    private JPanel gamePanel;

    //info labels
    private JLabel playerName;
    private JLabel opponentName;

    //hand selectors
    private JRadioButton playerRadioButton1;
    private JRadioButton playerRadioButton2;
    private JRadioButton opponentRadioButton1;
    private JRadioButton opponentRadioButton2;

    //buttons
    private JButton endTurnButton;
    private JButton divideButton;
    private JLabel gameLabel;

    GameClient(){
        setContentPane(gamePanel);
        setTitle("Game Screen");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        opponentFirst.setIcon(one);
        playerFirst.setIcon(one);
        opponentSecond.setIcon(one);
        playerSecond.setIcon(one);

        endTurn();

        this.setVisible(true);

        connectServer();
    }

    private void startTurn(){
        playerRadioButton1.setEnabled(true);
        playerRadioButton2.setEnabled(true);
        opponentRadioButton1.setEnabled(true);
        opponentRadioButton2.setEnabled(true);
        endTurnButton.setEnabled(true);
        divideButton.setEnabled(true);

        gameLabel.setText("Your Turn");
    }

    private void endTurn(){
        playerRadioButton1.setEnabled(false);
        playerRadioButton2.setEnabled(false);
        opponentRadioButton1.setEnabled(false);
        opponentRadioButton2.setEnabled(false);
        endTurnButton.setEnabled(false);
        divideButton.setEnabled(false);

        gameLabel.setText("Waiting for opponent");
    }

    private void connectServer(){
        try {
            socket = new Socket("localhost", 12345);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Connected to server.");

        try {
            inGameProcess();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void inGameProcess() throws IOException {

        BufferedReader in = null;
        String gameStateLogs = null;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        gameStateLogs = in.readLine();

        int playerNo = 0;
        String opponentNameText = "";

        String[] parts = gameStateLogs.split("/");

        for (String part : parts) {
            if (part.contains("PlayerNo")) {
                playerNo = Integer.parseInt(part.split("=")[1].trim());
            } else if (part.contains("OpponentName")) {
                opponentNameText = part.split("=")[1].trim();
                opponentName.setText("Opponent: " + opponentNameText);
            }
        }

        if (playerNo==1)
            startTurn();

        while (true){


            if (endTurnButton.isEnabled()){

            }

            else {

            }

        }

    }

}
