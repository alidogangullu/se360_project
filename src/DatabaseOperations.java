import java.io.File;
import java.io.IOException;
import java.sql.*;

public class DatabaseOperations {
    private String url;
    public DatabaseOperations() {
        url = "jdbc:sqlite:game.db";
        connectToDB();
    }
    private void connectToDB(){
        try {

            Class.forName("org.sqlite.JDBC");

            File dbFile = new File("database");
            if(!dbFile.exists()){
                dbFile.createNewFile();
            }

            String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                    + " username text PRIMARY KEY,\n"
                    + " password text NOT NULL,\n"
                    + " rating integer\n"
                    + ");";

            try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Table created or already exists.");
            }

            System.out.println("connected to db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean saveUser(User user){
        String sqlCheck = "SELECT COUNT(*) FROM users WHERE username = ?";
        String sqlInsert = "INSERT INTO users(username, password, rating) VALUES(?,?,?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck);
             PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {

            // Check if the username already exists
            pstmtCheck.setString(1, user.getUsername());
            ResultSet rs = pstmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return false;
            }

            // Insert the new user
            pstmtInsert.setString(1, user.getUsername());
            pstmtInsert.setString(2, user.getPassword());
            pstmtInsert.setInt(3, user.getRating());
            pstmtInsert.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    public User getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs  = pstmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getString("username"),rs.getString("password"),rs.getInt("rating"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
