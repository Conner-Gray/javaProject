import java.sql.*;

public class UserInfo{

    int ID = -1;
    String name;
    String password;
    String email;

    String state;

    public UserInfo(String state) {
        this.state = state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
    public int getID() {
        return ID;
    }

    public String getName(){
        return name;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void authUser(String username, String password, String url) {
        String query = "select * from users where name = ? and password = ?;";
        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.setID(rs.getInt("id"));
                this.setName(rs.getString("name"));
                this.setPassword(rs.getString("password"));
                this.setEmail(rs.getString("email"));

            } else {
                System.out.println("Invalid username or password! Please try again.");
                this.setID(-1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Fatal error! Please file a bug report.");
            this.setID(-1);
        }
    }

    public void addUser(String url){
        String query = "INSERT INTO users (name, password, email) VALUES (?, ?, ?);";
        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, this.name);
            stmt.setString(2, this.password);
            stmt.setString(3, this.email);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("\nUsername " + this.name + " already exists! Please select a unique username.\n");
        }
    }

    @Override
    public String toString() {
        return "Your information is: " +
                "\nName: " + name +
                "\nPassword: " + password +
                "\nEmail: " + email;
    }
}