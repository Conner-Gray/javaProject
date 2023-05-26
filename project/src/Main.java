import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        HashMap<String, String> capitalCities = new HashMap<String, String>();
        capitalCities.put("ENGLAND", "LONDON");
        capitalCities.put("GERMANY", "BERLIN");
        capitalCities.put("NORWAY", "OSLO");
        capitalCities.put("USA", "WASHINGTON DC");

        HashMap<String, String> visitedCountries = new HashMap<String, String>();


        char option = 'x';
        while(option != 'q') {
            option = initialSelect();
            if(option == 'v'){
                viewCapitals(visitedCountries);
            } else if (option == 'l') {
                visitCapital(capitalCities, visitedCountries);
            } else if (option == 'a') {
                viewCapitals(capitalCities);
            }
        }
    }

    private static char initialSelect(){
        Scanner scan = new Scanner(System.in);

        boolean valid = false;
        char option = 'x';
        while(!valid){
            System.out.print("[L]og a capital as visited, [V]iew capitals you've visited, view [A]ll capital cities, [Q]uit: ");
            String select = scan.nextLine();
            option = select.charAt(0);
            valid = initialErrorCheck(option);
        }

        return option;
    }

    private static boolean initialErrorCheck(char option){
        if(Character.toLowerCase(option) == 'v'){
            return true;
        } else if (Character.toLowerCase(option) == 'q') {
            return true;
        } else if (Character.toLowerCase(option) == 'l') {
            return true;
        } else if (Character.toLowerCase(option) == 'a') {
            return true;
        }
        System.out.println("Invalid option!");
        return false;
    }

    private static void viewCapitals(HashMap<String, String> capitalCities){
        if (capitalCities.keySet().size() == 0){
            System.out.println("Nothing here yet!");
        }
        for (String i : capitalCities.keySet()) {
            System.out.println("Country:" + i + ", Capital: " + capitalCities.get(i));
        }

    }

    private static HashMap<String, String> visitCapital(HashMap<String, String> capitalCities, HashMap<String, String> visitedCountries){

        boolean valid = false;
        Scanner scan = new Scanner(System.in);
        char option = 'x';
        while(valid != true){
            System.out.println("Which city?\n(Select by typing the country name as listed.)");
            for (String i : capitalCities.keySet()) {
                System.out.println("Country:" + i + ", Capital: " + capitalCities.get(i));
            }
            String select = scan.nextLine();
            select = select.toUpperCase();
            valid = visitCapitalErrorCheck(select, capitalCities);
            if(valid == true){
                visitedCountries.put(select, capitalCities.get(select));
            }
        }


        return visitedCountries;
    }
    private static boolean visitCapitalErrorCheck(String option, HashMap<String, String> capitalCities){

        if (capitalCities.keySet().contains(option.toUpperCase())){
            return true;
        }
        System.out.println("Invalid entry!");
        return false;
    }
}