import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class GameClient extends JFrame {
    private User user;

    //connection variables
    private BufferedReader in;
    private PrintWriter out;
    private int playerNo;

    //game specific
    private int turnCount;
    private int playerHand1;
    private int playerHand2;
    private int opponentHand1;
    private int opponentHand2;
    private String gameStateLogs;
    private final ImageIcon[] leftHandImages;
    private final ImageIcon[] rightHandImages;
    private final ImageIcon[] opponentLeftHandImages;
    private final ImageIcon[] opponentRighHandImages;
    private JLabel opponentFirstHandImage;
    private JLabel playerFirstHandImage;
    private JLabel opponentSecondHandImage;
    private JLabel playerSecondHandImage;
    private JPanel gamePanel;
    private JLabel playerName;
    private JLabel opponentNameLabel;
    private JRadioButton playerRadioButton1;
    private JRadioButton playerRadioButton2;
    private JRadioButton opponentRadioButton1;
    private JRadioButton opponentRadioButton2;
    private JButton endTurnButton;
    private JButton divideButton;
    private JLabel gameLabel;

    GameClient(User user, String opponentName, int playerNo){
        //socket and server settings
        this.user = user;
        try {
            in = new BufferedReader(new InputStreamReader(GameLobby.socket.getInputStream()));
            out = new PrintWriter(GameLobby.socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.playerNo = playerNo;
        this.opponentNameLabel.setText(opponentName);

        //game specific settings
        turnCount = 0;
        playerHand1 = 1;
        playerHand2 = 1;
        opponentHand1 = 1;
        opponentHand2 = 1;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    GameLobby.socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(0);
            }
        });

        //load images
        leftHandImages = new ImageIcon[5];
        for (int i = 0; i <= 4; i++) {
            leftHandImages[i] = new ImageIcon("images/left/" + i + ".png");
        }

        rightHandImages = new ImageIcon[5];
        for (int i = 0; i <= 4; i++) {
            rightHandImages[i] = new ImageIcon("images/right/" + i + ".png");
        }

        opponentLeftHandImages = new ImageIcon[5];
        for (int i = 0; i <= 4; i++) {
            opponentLeftHandImages[i] = new ImageIcon("images/left-opponent/" + i + ".png");
        }

        opponentRighHandImages = new ImageIcon[5];
        for (int i = 0; i <= 4; i++) {
            opponentRighHandImages[i] = new ImageIcon("images/right-opponent/" + i + ".png");
        }

        //set hand images
        updateHandImages();

        //set buttons
        endTurnButton.addActionListener(e -> {
            //send move values to the server and increase turn count
            try {
                turnCount++;
                out.println("TurnCount=" + turnCount + "/" + makeCurrentMove());
                endTurn();
            } catch (EmptyButtonException ex) {
                throw new RuntimeException(ex);
            }
        });
        divideButton.addActionListener(e -> {
            int newHandValues = (playerHand1 + playerHand2)/2;
            playerHand1 = newHandValues;
            playerHand2 = newHandValues;
            playerRadioButton1.setEnabled(true);
            playerRadioButton2.setEnabled(true);
            playerFirstHandImage.setEnabled(true);
            playerSecondHandImage.setEnabled(true);
            updateHandImages();
        });

        ButtonGroup playerHandButtons = new ButtonGroup();
        playerHandButtons.add(playerRadioButton1);
        playerHandButtons.add(playerRadioButton2);
        playerRadioButton1.setSelected(true);
        ItemListener playerHandButtonsListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // If one is selected, deselect the other
                if (e.getItem() == playerRadioButton1) {
                    playerRadioButton2.setSelected(false);
                } else if (e.getItem() == playerRadioButton2) {
                    playerRadioButton1.setSelected(false);
                }
            }
        };
        playerRadioButton1.addItemListener(playerHandButtonsListener);
        playerRadioButton2.addItemListener(playerHandButtonsListener);

        ButtonGroup opponentHandButtons = new ButtonGroup();
        opponentHandButtons.add(opponentRadioButton1);
        opponentHandButtons.add(opponentRadioButton2);
        opponentRadioButton1.setSelected(true);
        ItemListener opponentHandButtonsListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // If one is selected, deselect the other
                if (e.getItem() == opponentRadioButton1) {
                    opponentRadioButton2.setSelected(false);
                } else if (e.getItem() == opponentRadioButton2) {
                    opponentRadioButton1.setSelected(false);
                }
            }
        };
        opponentRadioButton1.addItemListener(opponentHandButtonsListener);
        opponentRadioButton2.addItemListener(opponentHandButtonsListener);

        //set client gui
        setContentPane(gamePanel);
        setTitle("Game Screen");
        setSize(900, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        playerName.setText(user.getUsername());
        endTurn();
        this.setVisible(true);

        //start game
        inGameProcess();
    }
    private void startTurn(){
        playerRadioButton1.setEnabled(true);
        playerRadioButton2.setEnabled(true);
        opponentRadioButton1.setEnabled(true);
        opponentRadioButton2.setEnabled(true);
        endTurnButton.setEnabled(true);

        playerFirstHandImage.setEnabled(true);
        playerSecondHandImage.setEnabled(true);
        opponentFirstHandImage.setEnabled(true);
        opponentSecondHandImage.setEnabled(true);

        if ((playerHand1 %2==0 && playerHand2 == 0 ||
                playerHand2 %2==0 && playerHand1 == 0) && playerHand1+playerHand2 != 0)
            divideButton.setEnabled(true);

        if (playerHand1==0) {
            playerFirstHandImage.setEnabled(false);
            playerRadioButton1.setEnabled(false);
            playerRadioButton2.setSelected(true);
        }
        if (playerHand2==0){
            playerSecondHandImage.setEnabled(false);
            playerRadioButton2.setEnabled(false);
            playerRadioButton1.setSelected(true);
        }
        if (opponentHand1==0){
            opponentFirstHandImage.setEnabled(false);
            opponentRadioButton1.setEnabled(false);
            opponentRadioButton2.setSelected(true);
        }
        if (opponentHand2==0){
            opponentSecondHandImage.setEnabled(false);
            opponentRadioButton2.setEnabled(false);
            opponentRadioButton1.setSelected(true);
        }

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
    private String makeCurrentMove() throws EmptyButtonException{
        int playerSelectedHand = 0;
        int opponentSelectedHand = 0;

        //EmptyButtonException playerButtonException = new EmptyButtonException("You should select hand for Attack!");
        //EmptyButtonException OpponentButtonException = new EmptyButtonException("You should select hand to Attack!");

        if (playerRadioButton1.isSelected()){
            playerSelectedHand = 1;
        } else if (playerRadioButton2.isSelected()) {
            playerSelectedHand = 2;
        } else {
            //throw playerButtonException;
        }

        if (opponentRadioButton1.isSelected()){
            opponentSelectedHand = 1;
        } else if (opponentRadioButton2.isSelected()) {
            opponentSelectedHand = 2;
        } else {
            //throw OpponentButtonException;
        }

        //save old values before update
        int opponentHand1Old = opponentHand1;
        int opponentHand2Old = opponentHand2;

        //update opponent hand values
        int playerSelectedHandValue = 0;
        int opponentSelectedHandValue = 0;
        if(playerSelectedHand == 1) {
            playerSelectedHandValue = playerHand1;
        } else {
            playerSelectedHandValue = playerHand2;
        }
        if(opponentSelectedHand == 1) {
            opponentSelectedHandValue = opponentHand1;
        } else {
            opponentSelectedHandValue = opponentHand2;
        }
        playerSelectedHandValue += opponentSelectedHandValue;
        playerSelectedHandValue %= 5;
        if (opponentSelectedHand == 1) {
            opponentHand1 = playerSelectedHandValue;
        } else {
            opponentHand2 = playerSelectedHandValue;
        }
        
        updateHandImages();
        
        return "PlayerSelectedHand="+playerSelectedHand+"/"+"OpponentSelectedHand="+opponentSelectedHand+"/"
                +"PlayerHand1="+ playerHand1 +"/"+"PlayerHand2="+ playerHand2 +"/"
                +"OpponentHand1="+ opponentHand1Old +"/"+"OpponentHand2="+ opponentHand2Old +"/";
    }
    private void inGameProcess(){
        while (gameContinues()){
            if (playerNo == 1 && turnCount % 2 == 0){
                //first connected player
                updateHandImages();
                startTurn();
            } else {
                updateClientWithServer();
            }
            if (playerNo == 2 && turnCount % 2 != 0) {
                //second connected player
                updateHandImages();
                startTurn();
            } else {
                updateClientWithServer();
            }
        }

    }
    private void updateClientWithServer(){
        try {
            gameStateLogs = in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] parts = gameStateLogs.split("/");

        int receivedTurnCount = 0;
        for (String part : parts) {
            if (part.startsWith("TurnCount=")) {
                receivedTurnCount = Integer.parseInt(part.split("=")[1].split("/")[0]);
            }
            if (part.startsWith("PlayerHand1=")){
                playerHand1 = Integer.parseInt(part.split("=")[1].split("/")[0]);
            }
            if (part.startsWith("PlayerHand2=")){
                playerHand2 = Integer.parseInt(part.split("=")[1].split("/")[0]);
            }
            if (part.startsWith("OpponentHand1=")){
                opponentHand1 = Integer.parseInt(part.split("=")[1].split("/")[0]);
            }
            if (part.startsWith("OpponentHand2=")){
                opponentHand2 = Integer.parseInt(part.split("=")[1].split("/")[0]);
            }
        }
            turnCount = receivedTurnCount;
    }
    private void updateHandImages(){
        opponentFirstHandImage.setIcon(opponentRighHandImages[opponentHand1]);
        playerFirstHandImage.setIcon(leftHandImages[playerHand1]);
        opponentSecondHandImage.setIcon(opponentLeftHandImages[opponentHand2]);
        playerSecondHandImage.setIcon(rightHandImages[playerHand2]);
    }
    private boolean gameContinues(){
        if (playerHand1 + playerHand2 == 0){
            //player lose
            JOptionPane.showMessageDialog(null, "You Lost! " + user.getUsername(), "Try Again", JOptionPane.INFORMATION_MESSAGE);
            out.println("TurnCount=" + ++turnCount + "/" + "PlayerHand1=0/"+"PlayerHand2=0/"
                    +"PlayerSelectedHand=1/"+"OpponentSelectedHand=1/");
            //close game client
            this.dispose();
            Main.gameLobby.setVisible(true);
            return false;
        }
        else if (opponentHand1 + opponentHand2 == 0){
            //player win
            JOptionPane.showMessageDialog(null, "You Won! " + user.getUsername(), "Congratulations", JOptionPane.INFORMATION_MESSAGE);
            out.println("TurnCount=" + ++turnCount + "/" + "OpponentHand1=0/"+"OpponentHand2=0/"
                    +"PlayerSelectedHand=1/"+"OpponentSelectedHand=1/");
            //close game client
            this.dispose();
            Main.gameLobby.setVisible(true);
            return false;
        }
        else {
            return true;
        }
    }
}