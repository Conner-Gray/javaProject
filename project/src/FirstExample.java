import java.sql.*;
import java.util.Properties;

public class FirstExample {

    static final String url = "jdbc:postgresql:postgres?user=postgres&password=postgres";
    static final String QUERY_USERS = "SELECT * FROM users";
    static final String QUERY_COUNTRIES = "SELECT * FROM countries";

    private static void viewPopular(){
        String popQuery = "select  ci.name, count(*) from cities ci  join user_cities uc on uc.city_id = ci.id  where uc.has_visited = true group by ci.name;";
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(popQuery);
            while (rs.next()) {
                System.out.print("Name: " + rs.getString("name"));
                System.out.print(", count: " + rs.getString("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        viewPopular();
    }
}