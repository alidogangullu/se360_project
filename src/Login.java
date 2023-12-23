import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Login extends JFrame{
    private JTextField usernameText;
    private JTextField passwordText;
    private JButton loginButton;
    private JButton signupButton;
    private JLabel username;
    private JLabel password;
    private JLabel warningLabel;
    private JPanel loginPanel;

    Socket socket;
    PrintWriter out;
    ObjectInputStream in;

    Login(){
        //set client gui
        setContentPane(loginPanel);
        setTitle("Battle of Hands");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        warningLabel.setVisible(false);
        this.setVisible(true);

        loginButton.addActionListener(event -> {
            try {
                socket = new Socket("localhost", 1234);
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("LoginPlayerUsername=" + usernameText.getText() + "/" + "LoginPlayerPassword=" + passwordText.getText() + "/");

                in  = new ObjectInputStream(socket.getInputStream());
                User user = (User) in.readObject();
                if (user!=null){
                    openGameLobby(user);
                    this.dispose();
                } else {
                    warningLabel.setText("Login Error, username and password do not match!");
                    warningLabel.setVisible(true);
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        signupButton.addActionListener(event -> {
            try {
                socket = new Socket("localhost", 1234);
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("SignupPlayerUsername=" + usernameText.getText() + "/" + "SignupPlayerPassword=" + passwordText.getText() + "/");

                in  = new ObjectInputStream(socket.getInputStream());
                User user = (User) in.readObject();
                if (user!=null){
                    openGameLobby(user);
                    this.dispose();
                } else {
                    warningLabel.setText("Signup Error, username already exist!");
                    warningLabel.setVisible(true);
                }

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private void openGameLobby(User user){
        new Thread(() -> {
            Main.gameLobby = new GameLobby(user);
        }).start();
    }

}
