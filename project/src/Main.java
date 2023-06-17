import java.sql.*;
import java.util.Scanner;

public class Main {

    static final String url = "jdbc:postgresql:postgres?user=postgres&password=postgres";

    public static void main(String[] args) {

        System.out.println("Welcome to Your Travel Planner");

        String state = "LOGGED_OUT";

        int ID = -1;
        char option;
        while(!state.equals("QUIT")) {
            if(state.equals("LOGGED_OUT")){
                option = logSelect();
                if(option == '1'){
                    ID = userLogIn();
                    if(ID != -1){
                        state = "LOGGED_IN";
                    }
                } else if (option == '0') {
                    state = "QUIT";
                } else{
                    System.out.print("Invalid option, please try again.");
                }

            } else if (state.equals("LOGGED_IN")) {
                option = initialSelect();
                if(option == '1'){
                    System.out.println("View visited info");
                } else if (option == '2') {
                    System.out.println("View like to visit info");
                } else if (option == '3') {
                    System.out.println("View detailed info");
                } else if (option == '4') {
                    System.out.println("View all cities in country");
                } else if (option == '5') {
                    System.out.println("View popular");
                    viewPopular();
                } else if (option == '0'){
                    state = "QUIT";
                }
            }

        }

        System.out.println("Goodbye!");
    }

    private static int userLogIn(){
        Scanner scan = new Scanner(System.in);
        System.out.print("Please enter your username: ");
        String uName = scan.nextLine();
        // if uName not in DB, error
        System.out.print("Please enter your password: ");
        String uPass = scan.nextLine();
        // if uPass not in DB, error

        int ID = authUser(uName, uPass);

        return ID;
    }

    private static Integer authUser(String username, String password) {
        String query = "select id from users where name = ? and password = ?;";
        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                System.out.println("Invalid username or password! Please try again.");
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Fatal error! Please file a bug report.");
            return -1;
        }
    }

    private static char logSelect(){
        Scanner scan = new Scanner(System.in);
        System.out.print("Press 0 to quit, or 1 to log in: ");
        String select = scan.nextLine();
        return select.charAt(0);
    }

    private static char initialSelect(){
        Scanner scan = new Scanner(System.in);

        boolean valid = false;
        char option = 'x';
        while(!valid){
            System.out.print(
                    """
                            Press 0 to quit
                            Press 1 to view all cities you've visited
                            Press 2 to view information on countries you'd like to visit
                            Press 3 to view a city's detailed information
                            Press 4 to view all cities in a country
                            Press 5 to view most popular cities
                            \s""");
            String select = scan.nextLine();
            option = select.charAt(0);
            valid = initialErrorCheck(option);
        }

        return option;
    }

    private static boolean initialErrorCheck(char option){
        if(option != '0' && option != '1' && option != '2' && option != '3' && option != '4' && option != '5'){
            System.out.println("Invalid entry! Please try again.");
            return false;
        }
        return true;
    }

    private static void viewPopular(){
        String popQuery = "select  ci.name, count(*) from cities ci  join user_cities uc on uc.city_id = ci.id  where uc.has_visited = true group by ci.name;";
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(popQuery);
            while (rs.next()) {
                System.out.print("Name: " + rs.getString("name"));
                System.out.print("; Visitor count: " + rs.getString("count"));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Scanner scan = new Scanner(System.in);
        System.out.print("Press enter to return to menu...");
        scan.nextLine();
    }
}