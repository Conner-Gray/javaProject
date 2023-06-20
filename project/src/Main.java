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
                } else if (option == '2') {
                    createNewUser(user);
                    user.addUser(url);
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
                } else if (option == '7') {
                    visitCity(user.getID());
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
        System.out.print("Press 0 to quit, 1 to log in, or 2 to create new account: ");
        String select = scan.nextLine();
        return select.charAt(0);
    }

    private static boolean createNewUser(UserInfo user){
        Scanner scan = new Scanner(System.in);
        System.out.print("Type desired username: ");
        user.setName(scan.nextLine());
        System.out.print("Type desired password: ");
        user.setPassword(scan.nextLine());
        System.out.print("Type your email address: ");
        user.setEmail(scan.nextLine());
        boolean valid = false;
        while(!valid){
            System.out.println("\n" + user);
            System.out.print("Is this information correct? Type 1 for yes, 0 for no: ");
            String choice = scan.nextLine();
            if (choice.equals("1")) {
                valid = true;
            } else if (!choice.equals("0")) {
                System.out.println("Invalid input!");
            } else{
                valid = createNewUser(user);
            }
        }
        return true;
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
                            Press 7 to mark a city as visited/unvisited
                            > \s""");
            String select = scan.nextLine();
            option = select.charAt(0);
            valid = initialErrorCheck(option);
        }

        return option;
    }

    private static boolean initialErrorCheck(char option){
        if(option != '0' && option != '1' && option != '2' && option != '3' && option != '4' && option != '5' && option != '6' && option != '7'){
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
                where user_id = ? and has_visited = true""";
        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();
            if(!rs.isBeforeFirst()){
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
            else{
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Populaton: " + rs.getInt("population"));
                System.out.println("Area: " + rs.getDouble("area"));
                System.out.println("Country: " + rs.getString("country_name"));
                System.out.println("Continent: " + rs.getString("continent"));
                System.out.println("Government: " + rs.getString("government"));
            }

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

    private static void visitCity(int id){
        Scanner scan = new Scanner(System.in);
        System.out.println("Which city would you like to change the 'visited' flag for?");
        System.out.println("(NOTE: type the city's name as it appears in our service. If you are unsure how it appears, use option 4 on our home menu to search manually)");
        System.out.print("> ");
        char confirm = confirmVisit(scan.nextLine(), id);
        if(confirm != 0){
            System.out.println("Updated!");
            System.out.print("Press enter to return to menu...");
            scan.nextLine();
        }
    }

    private static char confirmVisit(String name, int id){
        Scanner scan = new Scanner(System.in);

        String query = """
                select ci.id,\s
                ci.name,\s
                co.name as country_name,
                uc.has_visited
                from cities ci\s
                join countries co\s
                join user_cities uc on uc.user_id = ?
                on uc.city_id = ci.id\s
                where ci.name = ?;""";

        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()){
                System.out.println("City not found!");
                return 0;
            }
            else{

                int cityID = rs.getInt("id");
                boolean hasVisited = rs.getBoolean("has_visited");
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Country: " + rs.getString("country_name"));

                if(hasVisited){
                    System.out.println("You have visited this city! Mark as unvisited?");
                }
                else{
                    System.out.println("You have not visited this city! Mark as visited?");
                }
                System.out.println("(type 0 for no, 1 for yes)");
                System.out.print("> ");
                char choice = scan.nextLine().charAt(0);
                choice = visitConfirmInputCheck(choice);
                if(choice == '0') {
                    return 0;
                } else {
                    changeHasVisited(cityID, hasVisited, id);
                    return 1;
                }
            }

        }catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Fatal error! Please file a bug report.");
            return 0;
        }
    }

    private static char visitConfirmInputCheck(char choice){
        Scanner scan = new Scanner(System.in);
        while(choice != '1' && choice != '0'){
            System.out.println("Invalid input! Enter 0 or 1");
            choice = scan.nextLine().charAt(0);
        }
        return choice;
    }

    private static void changeHasVisited(int cityID, boolean hasVisited, int userID) {
        String query = """
                UPDATE user_cities
                SET has_visited = ?
                WHERE user_id = ? and city_id = ?;""";

        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setBoolean(1, !hasVisited);
            stmt.setInt(2, userID);
            stmt.setInt(3, cityID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

