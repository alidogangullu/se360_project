import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private int rating;

    public  User(String username, String password, int rating) {
        this.username = username;
        this.password = password;
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
