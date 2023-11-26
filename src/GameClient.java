import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class GameClient extends JFrame {
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

    GameClient(){

        connectServer();

        setContentPane(gamePanel);
        setTitle("Game Screen");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        opponentFirst.setIcon(one);
        playerFirst.setIcon(five);
        opponentSecond.setIcon(two);
        playerSecond.setIcon(four);
    }

    private void connectServer(){
        try {
            Socket socket = new Socket("localhost", 12345);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Connected to server.");
    }

}
