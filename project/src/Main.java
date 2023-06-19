import java.sql.*;
import java.util.Scanner;

public class Main {

    static final String url = "jdbc:postgresql:postgres?user=postgres&password=postgres";

    public static void main(String[] args) {

        System.out.println("Welcome to Your Travel Planner");

        UserInfo user = new UserInfo("LOGGED_OUT");

        char option;
        while(!user.getState().equals("QUIT")) {
            if(user.getState().equals("LOGGED_OUT")){
                option = logSelect();
                if(option == '1'){
                    userLogIn(user);
                    if(user.getID() != -1){
                        user.setState("LOGGED_IN");
                    }
                } else if (option == '0') {
                    user.setState("QUIT");
                } else{
                    System.out.println("Invalid option, please try again.");
                }

            } else if (user.getState().equals("LOGGED_IN")) {
                System.out.println("Welcome, " + user.getName());
                option = initialSelect();
                if(option == '1'){
                    viewAllVisited(user.getID());
                } else if (option == '2') {
                    viewLikeToVisit(user.getID());
                } else if (option == '3') {
                    citySelect();
                } else if (option == '4') {
                    citiesInCountry();
                } else if (option == '5') {
                    viewPopular();
                } else if (option == '6') {
                    viewUserInfo(user);
                } else if (option == '0'){
                    user.setState("QUIT");
                }
            }

        }

        System.out.println("Goodbye!");
    }

    private static void userLogIn(UserInfo user){
        Scanner scan = new Scanner(System.in);
        System.out.print("Please enter your username: ");
        String uName = scan.nextLine();
        System.out.print("Please enter your password: ");
        String uPass = scan.nextLine();
        user.authUser(uName, uPass, url);
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
                            Press 6 to view your user info
                            > \s""");
            String select = scan.nextLine();
            option = select.charAt(0);
            valid = initialErrorCheck(option);
        }

        return option;
    }

    private static boolean initialErrorCheck(char option){
        if(option != '0' && option != '1' && option != '2' && option != '3' && option != '4' && option != '5' && option != '6'){
            System.out.println("Invalid entry! Please try again.");
            return false;
        }
        return true;
    }

    private static void viewAllVisited(int ID){
        String query = """
                select ci.name, co.name as country_name
                from user_cities uc
                join cities ci on uc.city_id = ci.id
                join countries co on co.id = ci.country_id
                where user_id = ?""";
        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()){
                System.out.println("Nowhere yet!");
            }
            while (rs.next()){
                System.out.print(rs.getString("name"));
                System.out.print(", " + rs.getString("country_name"));
                System.out.print("; ");
            }
            System.out.println();
            Scanner scan = new Scanner(System.in);
            System.out.print("Press enter to return to menu...");
            scan.nextLine();

        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void viewLikeToVisit(int ID){
        String query = "select ci.name, co.name as country_name from cities ci join countries co on ci.country_id = co.id join user_cities uc on uc.city_id = ci.id where uc.wants_to_visit = true and uc.user_id = ?";

        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()){
                System.out.println("Nowhere yet!");
            }
            while (rs.next()){
                System.out.print("Name: " + rs.getString("name"));
                System.out.print(", ");
                System.out.print("Country: " + rs.getString("country_name"));
                System.out.println();
            }
            Scanner scan = new Scanner(System.in);
            System.out.print("Press enter to return to menu...");
            scan.nextLine();

        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void citySelect(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Which city would you like to view information for?");
        System.out.println("(NOTE: type the city's name as it appears in our service. If you are unsure how it appears, use option 4 on our home menu to search manually)");
        System.out.print("> ");
        String chosenCity = scan.nextLine();


        String query = "select ci.id, ci.name, ci.population, ci.area, co.name as country_name, co.continent, co.government from cities ci join countries co on ci.country_id = co.id where ci.name = ?";

        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, chosenCity);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()){
                System.out.println("City not found!");
            }
            System.out.println("Name: " + rs.getString("name"));
            System.out.println("Populaton: " + rs.getInt("population"));
            System.out.println("Area: " + rs.getDouble("area"));
            System.out.println("Country: " + rs.getString("country_name"));
            System.out.println("Continent: " + rs.getString("continent"));
            System.out.println("Government: " + rs.getString("government"));

            System.out.print("Press enter to return to menu...");
            scan.nextLine();

        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void citiesInCountry(){
        String chosenCountry = countryChoice();
        int countryID = getCountryID(chosenCountry);
        String query = "select ci.name from cities ci where ci.country_id = ?";
        if(countryID != -1){
            try {
                Connection conn = DriverManager.getConnection(url);
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, countryID);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    System.out.print(rs.getString("name")+ ", ");
                }
                Scanner scan = new Scanner(System.in);
                System.out.print("Press enter to return to menu...");
                scan.nextLine();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("City not found! Please check our spelling and try again!");
        }
    }

    private static String countryChoice(){
        Scanner scan = new Scanner(System.in);
        String query = "select name from countries";
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                System.out.print(rs.getString("name"));
                if(rs.next()){
                    System.out.print(", ");
                }else{
                    System.out.println();
                }
            }
            System.out.println("Select country from above list: ");
            return scan.nextLine();
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static int getCountryID(String name){
        String query = "select id from countries where name = ?";
        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getInt("id");
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

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

    private static void viewUserInfo(UserInfo user){
        System.out.println(user);
        Scanner scan = new Scanner(System.in);
        System.out.print("Press enter to return to menu...");
        scan.nextLine();
    }
}

class UserInfo{

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

    @Override
    public String toString() {
        return "Your information is: " +
                "\nName: " + name +
                "\nPassword: " + password +
                "\nEmail: " + email;
    }
}