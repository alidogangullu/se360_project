import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    static ServerSocket inGameServerSocket;
    static {
        try {
            inGameServerSocket = new ServerSocket(12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean playersFound = true;
    public static void main(String[] args) {

        new Thread(new LoginHandler()).start();

        while (true) {
            InGameHandler server = new InGameHandler();
            if(playersFound) {
                playersFound = false;
                new Thread(server).start();
            }
        }

    }
}

class InGameHandler extends Thread {

    int turnCount = 0;
    int player1Hand1 = 1;
    int player1Hand2 = 1;
    int player2Hand1 = 1;
    int player2Hand2 = 1;


    private void inGameProcess(ConnectedPlayer connectedPlayer1, ConnectedPlayer connectedPlayer2) throws IOException {

        PrintWriter player1Out;
        PrintWriter player2Out;

        player1Out = new PrintWriter(connectedPlayer1.getPlayerSocket().getOutputStream(), true);
        player1Out.println("PlayerNo=1/" + "OpponentName=" + connectedPlayer2.getNickName() + "/"+ "OpponentRating="+ connectedPlayer2.getRating());

        player2Out = new PrintWriter(connectedPlayer2.getPlayerSocket().getOutputStream(), true);
        player2Out.println("PlayerNo=2/" + "OpponentName=" + connectedPlayer1.getNickName() + "/"+ "OpponentRating="+ connectedPlayer1.getRating());

        BufferedReader player1In = new BufferedReader(new InputStreamReader(connectedPlayer1.getPlayerSocket().getInputStream()));
        BufferedReader player2In = new BufferedReader(new InputStreamReader(connectedPlayer2.getPlayerSocket().getInputStream()));

        while (gameContinues(connectedPlayer1, connectedPlayer2)) {

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
        player2Out.println("TurnCount=" + turnCount + "/"
                + "PlayerHand1="+player2Hand1+"/"+"PlayerHand2="+player2Hand2+"/"
                + "OpponentHand1="+player1Hand1+"/"+"OpponentHand2="+player1Hand2+"/");

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
            System.out.println("Waiting for players...");

            // Wait for player 1
            Socket player1Socket = ServerMain.inGameServerSocket.accept();
            ConnectedPlayer connectedPlayer1 = new ConnectedPlayer(player1Socket);
            System.out.println("Player 1 connected.");

            ObjectOutputStream player1Out = new ObjectOutputStream(player1Socket.getOutputStream());
            player1Out.writeObject("Waiting Player 2.");

            // Wait for player 2
            ConnectedPlayer connectedPlayer2;
            Socket player2Socket;
            do {
                player2Socket = ServerMain.inGameServerSocket.accept();
                connectedPlayer2 = new ConnectedPlayer(player2Socket);

                if(!connectedPlayer1.fairMatch(connectedPlayer2.getRating())) player2Socket.close();

            } while(!connectedPlayer1.fairMatch(connectedPlayer2.getRating()));
            System.out.println("Player 2 connected.");

            // Inform players
            ObjectOutputStream player2Out = new ObjectOutputStream(player2Socket.getOutputStream());

            player2Out.writeObject("Game starting.");


            ServerMain.playersFound = true;

            //Start Game
            inGameProcess(connectedPlayer1, connectedPlayer2);
            player1Out.close();
            player2Out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean gameContinues(ConnectedPlayer connectedPlayer1, ConnectedPlayer connectedPlayer2){
        DatabaseOperations db = new DatabaseOperations();
        if (player1Hand1 + player1Hand2 == 0){
            //player2 win
            System.out.println("Player " + connectedPlayer2.getNickName() + " won!");
            db.setUserRatings(connectedPlayer2.getNickName(),connectedPlayer1.getNickName());
            return false;
        }
        else if (player2Hand1 + player2Hand2 == 0){
            //player1 win
            System.out.println("Player " + connectedPlayer1.getNickName() + " won!");
            db.setUserRatings(connectedPlayer1.getNickName(),connectedPlayer2.getNickName());
            return false;
        }
        else {
            return true;
        }
    }
    public void run()
    {
        connectPlayer();
    }
}


class LoginHandler extends Thread {
    ServerSocket loginServerSocket;
    {
        try {
            loginServerSocket = new ServerSocket(1234);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    DatabaseOperations db = new DatabaseOperations();

    public void run() {
        try {

            while (true) {
                System.out.println("Waiting for player login...");
                Socket playerSocket = loginServerSocket.accept();
                handleLogin(playerSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogin(Socket playerSocket) throws IOException {
        boolean isSuccessful = false;
        User user = null;

        BufferedReader playerDataReader = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
        String playerData = playerDataReader.readLine();

        String[] playerDataParts = playerData.split("/");

        if (playerDataParts[0].contains("LoginPlayerUsername=")){
            //login process
            String loginPlayerUsername = "";
            String loginPlayerPassword = "";

            for (String part : playerDataParts) {
                if (part.startsWith("LoginPlayerUsername=")) {
                    loginPlayerUsername = part.split("=")[1].split("/")[0];
                }
                if (part.startsWith("LoginPlayerPassword=")) {
                    loginPlayerPassword = part.split("=")[1].split("/")[0];
                }
            }
            User tempUser = db.getUser(loginPlayerUsername);

            if (tempUser!=null&&loginPlayerUsername.equals(tempUser.getUsername())&&loginPlayerPassword.equals(tempUser.getPassword())) {
                isSuccessful = true;
                user = tempUser;
            }

        } else if (playerDataParts[0].contains("SignupPlayerUsername=")) {
            //signup process
            String signupPlayerUsername = null;
            String signupPlayerPassword = null;

            for (String part : playerDataParts) {
                if (part.startsWith("SignupPlayerUsername=")) {
                    signupPlayerUsername = part.split("=")[1].split("/")[0];
                }
                if (part.startsWith("SignupPlayerPassword=")) {
                    signupPlayerPassword = part.split("=")[1].split("/")[0];
                }
            }
            user = new User(signupPlayerUsername,signupPlayerPassword,1000);
            isSuccessful = db.saveUser(user);

        }

        ObjectOutputStream playerOut= new ObjectOutputStream(playerSocket.getOutputStream());
        if (isSuccessful){
            playerOut.writeObject(user);
        } else {
            playerOut.writeObject(null);
        }

    }
}

class ConnectedPlayer {
    private final Socket playerSocket;
    private final String nickName;
    private  int rating;

    public int getRating() {
        return rating;
    }

    public ConnectedPlayer(Socket playerSocket) {
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

        String nickname = "";

        for (String part : playerInfo) {
            if (part.startsWith("PlayerNickname=")) {
                nickname = part.split("=")[1].trim();
            }
            if(part.startsWith("PlayerRating=")) {
                //rating = Integer.parseInt(part.split("=")[1].trim());
                DatabaseOperations db = new DatabaseOperations();
                rating = db.getUserRating(nickname);
            }

        }
        return nickname;
    }

    public boolean fairMatch(int opponentRating) {
        if( 200 > Math.abs(rating - opponentRating) ) {
            return true;
        }
        return false;
    }

}