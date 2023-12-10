import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class GameServer {
    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.connectPlayer();
    }

    int turnCount = 0;
    int player1Hand1 = 1;
    int player1Hand2 = 1;
    int player2Hand1 = 1;
    int player2Hand2 = 1;

    private void inGameProcess(Player player1, Player player2) throws IOException {

        PrintWriter player1Out;
        PrintWriter player2Out;

        player1Out = new PrintWriter(player1.getPlayerSocket().getOutputStream(), true);
        player1Out.println("PlayerNo=1/" + "OpponentName=" + player2.getNickName() + "/");

        player2Out = new PrintWriter(player2.getPlayerSocket().getOutputStream(), true);
        player2Out.println("PlayerNo=2/" + "OpponentName=" + player1.getNickName() +"/");

        BufferedReader player1In = new BufferedReader(new InputStreamReader(player1.getPlayerSocket().getInputStream()));
        BufferedReader player2In = new BufferedReader(new InputStreamReader(player2.getPlayerSocket().getInputStream()));

        while (gameContinues(player1, player2)) {

            if ( turnCount % 2 == 0) {
                //First Connected Player
                String player1Message = player1In.readLine();
                String[] player1Move = player1Message.split("/");
                int receivedTurnCount1 = 0;
                int receivedSelectedPlayerHand = 0;
                int receivedSelectedOpponentHand = 0;

                for (String part : player1Move) {
                    if (part.startsWith("TurnCount=")) {
                        receivedTurnCount1 = Integer.parseInt(part.split("=")[1].split("/")[0]);
                    }
                    if (part.startsWith("PlayerSelectedHand=")){
                        receivedSelectedPlayerHand = Integer.parseInt(part.split("=")[1].split("/")[0]);
                    }
                    if (part.startsWith("OpponentSelectedHand=")){
                        receivedSelectedOpponentHand = Integer.parseInt(part.split("=")[1].split("/")[0]);
                    }
                    if (part.startsWith("PlayerHand1=")){
                        player1Hand1 = Integer.parseInt(part.split("=")[1].split("/")[0]);
                    }
                    if (part.startsWith("PlayerHand2=")){
                        player1Hand2 = Integer.parseInt(part.split("=")[1].split("/")[0]);
                    }
                    if (part.startsWith("OpponentHand1=")){
                        player2Hand1 = Integer.parseInt(part.split("=")[1].split("/")[0]);
                    }
                    if (part.startsWith("OpponentHand2=")){
                        player2Hand2 = Integer.parseInt(part.split("=")[1].split("/")[0]);
                    }
                }

                attackOpponentHands(1,receivedSelectedPlayerHand,receivedSelectedOpponentHand);
                turnCount = receivedTurnCount1;
                player2Out.println("TurnCount=" + turnCount + "/"
                        + "PlayerHand1="+player2Hand1+"/"+"PlayerHand2="+player2Hand2+"/"
                        + "OpponentHand1="+player1Hand1+"/"+"OpponentHand2="+player1Hand2+"/");
            }

            if(turnCount % 2 != 0) {
                //Second Connected Player
                String player2Message = player2In.readLine();
                String[] player2Move = player2Message.split("/");
                int receivedTurnCount2 = 0;
                int receivedSelectedPlayerHand = 0;
                int receivedSelectedOpponentHand = 0;

                for (String part : player2Move) {
                    if (part.startsWith("TurnCount=")) {
                        receivedTurnCount2 = Integer.parseInt(part.split("=")[1].split("/")[0]);
                    }
                    if (part.startsWith("PlayerSelectedHand=")){
                        receivedSelectedPlayerHand = Integer.parseInt(part.split("=")[1].split("/")[0]);
                    }
                    if (part.startsWith("OpponentSelectedHand=")){
                        receivedSelectedOpponentHand = Integer.parseInt(part.split("=")[1].split("/")[0]);
                    }
                    if (part.startsWith("PlayerHand1=")){
                        player2Hand1 = Integer.parseInt(part.split("=")[1].split("/")[0]);
                    }
                    if (part.startsWith("PlayerHand2=")){
                        player2Hand2 = Integer.parseInt(part.split("=")[1].split("/")[0]);
                    }
                    if (part.startsWith("OpponentHand1=")){
                        player1Hand1 = Integer.parseInt(part.split("=")[1].split("/")[0]);
                    }
                    if (part.startsWith("OpponentHand2=")){
                        player1Hand2 = Integer.parseInt(part.split("=")[1].split("/")[0]);
                    }
                }

                attackOpponentHands(2,receivedSelectedPlayerHand,receivedSelectedOpponentHand);
                turnCount = receivedTurnCount2;
                player1Out.println("TurnCount=" + turnCount + "/"
                        + "PlayerHand1="+player1Hand1+"/"+"PlayerHand2="+player1Hand2+"/"
                        + "OpponentHand1="+player2Hand1+"/"+"OpponentHand2="+player2Hand2+"/");
            }
        }
    }

    private void attackOpponentHands(int playerNo, int selectedPlayerHand, int selectedOpponentHand) {
        int playerSelectedHandValue;
        int opponentSelectedHandValue;

        if (playerNo == 1) {
            if (selectedPlayerHand == 1) {
                playerSelectedHandValue = player1Hand1;
            } else if (selectedPlayerHand == 2) {
                playerSelectedHandValue = player1Hand2;
            } else {
                System.out.println("Invalid selection for Player 1");
                return;
            }

            if (selectedOpponentHand == 1) {
                opponentSelectedHandValue = player2Hand1;
            } else if (selectedOpponentHand == 2) {
                opponentSelectedHandValue = player2Hand2;
            } else {
                System.out.println("Invalid selection for Player 2");
                return;
            }

            //update opponent hand
            playerSelectedHandValue += opponentSelectedHandValue;
            playerSelectedHandValue %= 5;

            if (selectedOpponentHand == 1) {
                player2Hand1 = playerSelectedHandValue;
            } else {
                player2Hand2 = playerSelectedHandValue;
            }
        }
        else if (playerNo == 2) {
            if (selectedPlayerHand == 1) {
                playerSelectedHandValue = player2Hand1;
            } else if (selectedPlayerHand == 2) {
                playerSelectedHandValue = player2Hand2;
            } else {
                System.out.println("Invalid selection for Player 2");
                return;
            }

            if (selectedOpponentHand == 1) {
                opponentSelectedHandValue = player1Hand1;
            } else if (selectedOpponentHand == 2) {
                opponentSelectedHandValue = player1Hand2;
            } else {
                System.out.println("Invalid selection for Player 1");
                return;
            }

            //update opponent hand
            playerSelectedHandValue += opponentSelectedHandValue;
            playerSelectedHandValue %= 5;

            if (selectedOpponentHand == 1) {
                player1Hand1 = playerSelectedHandValue;
            } else {
                player1Hand2 = playerSelectedHandValue;
            }
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
    private boolean gameContinues(Player player1, Player player2){
        if (player1Hand1 + player1Hand2 == 0){
            //player2 win
            System.out.println("Player " + player2.getNickName() + " won!");
            return false;
        }
        else if (player2Hand1 + player2Hand2 == 0){
            //player1 win
            System.out.println("Player " + player1.getNickName() + " won!");
            return false;
        }
        else {
            return true;
        }
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
