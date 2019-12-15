package MulticastRMIServers;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.*;

public class RMIClient implements CallBack, Serializable {
    private String RMIServerIP1;
    private int RMIServerPort;
    private int RMIClientPort;
    private int maxRecursion;

    private  ServerInterface ci = null;

    private boolean isLoggedIn = false;
    private boolean isAdmin = false;
    private boolean hasNewNotifications = false;

    public String username = null, password;

    private static Scanner sc = new Scanner(System.in);

    CallBack callBackInterface = null;


    private RMIClient()throws RemoteException{
        readProperties();

        connectInterface();

        homeMenu();
    }


    public static void main(String[] args) throws RemoteException{
        new RMIClient();
    }


    private void homeMenu(){
        int choice;

        ArrayList<String> menu;

        do {
            menu = menuBuilder();
            printMenu(menu, "[0] Exit");

            choice = chooser(0, menu.size(), "\nChoice: ");

            if (!isLoggedIn)
                switch (choice){
                    case 1:
                        login();
                        break;

                    case 2:
                        register();
                        break;

                    case 3:
                        search();
                        break;
                }
            else
                switch (choice){
                    case 1:
                        search();
                        break;

                    case 2:
                        indexNewURL();
                        break;

                    case 3:
                        searchHistory();
                        break;

                    case 4:
                        notifications();
                        break;

                    case 5:
                        administrationPage();
                        break;

                    case 6:
                        grantPrivileges();
                        break;
                }
        }while(choice != 0);
    }


    private void login(){
        HashMap<String, Object> serverResponse;

        username = askMandatoryInfo("Username");

        password = askMandatoryInfo("Password");

        try{
            serverResponse = ci.login(username, password);

            if (serverResponse.containsKey("Error")){
                System.out.println((String)serverResponse.get("Error"));
            }
            else{
                System.out.println("Login successfull!");
                isLoggedIn = true;

                hasNewNotifications = (boolean) serverResponse.get("hasNewNotifications");

                isAdmin = (boolean) serverResponse.get("isAdmin");

                LocateRegistry.createRegistry(RMIClientPort).rebind(username, this);
                ci.addMe(username, (CallBack) this);
            }


        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void register(){
        HashMap<String, Object> serverResponse;

        username = askMandatoryInfo("Username");

        password = askMandatoryInfo("Password");

        try {
            serverResponse = ci.register(username, password);

            if (serverResponse.containsKey("Error")){
                System.out.println((String)serverResponse.get("Error"));
            }
            else
                System.out.println("Registered successfully!");

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void search(){
        HashMap<String, Object> serverResponse;

        ArrayList<String> searchedTitles;
        ArrayList<String> searchedURLs;
        ArrayList<String> searchedCitations;

        ArrayList<String> connectedTitles;
        ArrayList<String> connectedURLs;
        ArrayList<String> connectedCitations;

        int searchSize, choice;

        String searchTarget = askMandatoryInfo("Search for");

        try {
            serverResponse = ci.search(searchTarget, username);

            searchedTitles = (ArrayList<String>) serverResponse.get("SearchedTitles");
            searchedURLs = (ArrayList<String>) serverResponse.get("SearchedURLs");
            searchedCitations = (ArrayList<String>) serverResponse.get("SearchedCitations");

            if (searchedTitles.isEmpty() || searchedURLs.isEmpty() || searchedCitations.isEmpty()){
                System.out.println("We found nothing!");
            }
            else{
                searchSize = searchedTitles.size();

                System.out.println("Results for \"" + searchTarget + "\":\n\n");

                for (int i = 0; i < searchSize; i++){
                    System.out.println("[" + (i+1) + "]\t" + searchedTitles.get(i));
                    System.out.println("\t" + searchedURLs.get(i));
                    System.out.println("\t" + searchedCitations.get(i) + "\n\n");
                }

                System.out.println("[0] Back\n");

                choice = chooser(0, searchSize, "\nOpen: ");

                if (choice != 0){
                    serverResponse = ci.connectedPages(searchedURLs.get(choice - 1));

                    connectedTitles = (ArrayList<String>) serverResponse.get("ConnectedTitles");
                    connectedURLs = (ArrayList<String>) serverResponse.get("ConnectedURLs");
                    connectedCitations = (ArrayList<String>) serverResponse.get("ConnectedCitations");

                    System.out.println("\nPages connected to " + searchedTitles.get(choice - 1) + "\n");

                    searchSize = connectedTitles.size();

                    for (int i = 0; i < searchSize; i++){
                        System.out.println(connectedTitles.get(i));
                        System.out.println(connectedURLs.get(i));
                        System.out.println(connectedCitations.get(i) + "\n");
                    }
                }
            }


        } catch (RemoteException e) {
            System.out.println("Something went wrong while searching");
            e.printStackTrace();
        }
    }


    private void indexNewURL(){
        HashMap<String, Object> serverResponse;


        String newURL = askMandatoryInfo("URL");
        int recursionLevels = chooser(1, maxRecursion, "\nRecursion Levels: ");

        try {
            System.out.println("Waiting for server indexation...");
            serverResponse = ci.indexNewURL(newURL, recursionLevels);
            System.out.println("IndexationDone!!!");

        } catch (RemoteException e) {
            System.out.println("Connection to RMIServer lost");
            e.printStackTrace();
        }
    }


    private void searchHistory(){
        HashMap<String, Object> serverResponse;

        ArrayList<String> searchHistory = new ArrayList<>();

        try {
            serverResponse = ci.getSearchHistory(username);

            if (serverResponse.containsKey("Error")){
                System.out.println(serverResponse.get("Error") + "\n");
            }
            else {
                searchHistory.addAll((ArrayList<String>) serverResponse.get("SearchHistory"));

                System.out.println("Search history: \n\n");

                for (String temp : searchHistory){
                    System.out.println(temp);
                }
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }


    private void notifications(){
        HashMap<String, Object> serverResponse;

        ArrayList<String> newNotifications;
        ArrayList<String> notifications;

        try {
            serverResponse = ci.getNotifications(username);

            if (serverResponse.containsKey("Error")){
                System.out.println(serverResponse.get("Error") + "\n");
            }
            else {
                newNotifications = (ArrayList<String>) serverResponse.get("NewNotifications");
                notifications = (ArrayList<String>) serverResponse.get("Notifications");
                for (String temp : newNotifications){
                    System.out.println("(new)" + temp);
                }
                System.out.println("\n");

                for (String temp : notifications){
                    System.out.println(temp);
                }

                hasNewNotifications = false;
            }

        }catch (RemoteException e){
            e.printStackTrace();
        }
    }


    private void administrationPage(){
        HashMap<String, Object> serverResponse;

        ArrayList<String> importantPages;
        ArrayList<String> commonSearches;
        ArrayList<String> multicastIPs;
        ArrayList<Integer> multicastPorts;

        try {
            serverResponse = ci.getAdministrationPage();

            importantPages = (ArrayList<String>) serverResponse.get("ImportantPages");
            commonSearches = (ArrayList<String>) serverResponse.get("CommonSearches");
            multicastIPs = (ArrayList<String>) serverResponse.get("MuticastIPs");
            multicastPorts = (ArrayList<Integer>) serverResponse.get("MulticastPorts");

            if (importantPages.isEmpty() || importantPages == null)
                System.out.println("No pages receieed from the database");
            else {
                System.out.println("Top 10 most important pages:");
                for (int i = 0; i < importantPages.size(); i++)
                    System.out.println("(" + (i+1) + ") " + importantPages.get(i));
            }

            System.out.println("\n");

            if (commonSearches.isEmpty() || commonSearches == null)
                System.out.println("No common searches received from the database");
            else {
                System.out.println("Top 10 most common searches:");
                for (int i = 0; i < commonSearches.size(); i++)
                    System.out.println("(" + (i+1) + ") " + commonSearches.get(i));
            }

            System.out.println("\n");

            if (multicastIPs.isEmpty() || multicastPorts.isEmpty() || multicastIPs == null || multicastPorts == null)
                System.out.println("No information about the multicast servers was received");
            else {
                System.out.println("Active multicast servers:");
                for (int i = 0; i < multicastIPs.size(); i++) {
                    System.out.println("(" + (i+1) + ") IP: " + multicastIPs.get(i) + "\t\tPort: " + multicastPorts.get(i));
                }
            }


        }catch (RemoteException e){
            e.printStackTrace();
        }

    }


    private void grantPrivileges(){
        HashMap<String, Object> serverResponse;

        String targetUser = askMandatoryInfo("To whom? (username)");

        try {
            serverResponse = ci.grantPrivileges(targetUser);

            if (serverResponse.containsKey("Error") || serverResponse == null){
                System.out.println((String)serverResponse.get("Error"));
            }
            else {

            }
        } catch (RemoteException e) {
            System.out.println("Something went wrong while granting privileges");
            e.printStackTrace();
        }
    }

    /* ======================================== SECONDARY FUNCTIONS ======================================== */

    private void readProperties(){
        Property properties = new Property("UcBusca.properties");
        RMIServerIP1 = properties.getProperty("RMIServerIP1");
        RMIServerPort = Integer.parseInt(properties.getProperty("RMIServerPort"));
        RMIClientPort = Integer.parseInt(properties.getProperty("RMIClientPort"));
        maxRecursion = Integer.parseInt(properties.getProperty("MaxRecursion"));
    }


    private void connectInterface(){
        do{
            try {
                Registry reg = LocateRegistry.getRegistry(RMIServerIP1, RMIServerPort);
                ci = (ServerInterface) reg.lookup("Server");

                System.out.println("\nConnected to RMIServer:\n" + ci.toString());
            } catch (NotBoundException | RemoteException e) {
                ci = null;
            }
        }while(ci == null);
    }


    private ArrayList<String> menuBuilder(){
        ArrayList<String> out = new ArrayList<>();

        if (!isLoggedIn) {
            out.add("Login");
            out.add("Register");

            out.add("Search");
        }
        else{
            out.add("Search");

            out.add("Index a new URL");
            out.add("Search History");

            if (hasNewNotifications)
                out.add("(new) Notifications");
            else
                out.add("Notifications");
        }

        if (isAdmin) {
            out.add("Administration Page");
            out.add("Grant Admin Privileges");
        }

        return out;
    }


    private void printMenu(ArrayList<String> menu, String lastLine){
        int menuLength = menu.size();

        System.out.println("\n\n");

        for (int i = 0; i < menuLength; i++)
            System.out.println("[" + (i+1) + "] " + menu.get(i));

        System.out.println(lastLine);
    }


    /** Will print a preset message until the user choses a valid number
     * @param lower Lower number that the function will allow user to insert. Should almost always be 0.
     * @param upper Upper number that the function will allow user to insert.
     * @param str Will usually be something like "Choice: "
     * @return returns the number chosen by the user
     */
    private static int chooser(int lower, int upper, String str){
        int choice = -1;

        while (choice < lower || choice > upper) {
            System.out.print(str);
            choice = readInt();

            if (choice < lower || choice > upper)
                System.out.println("Dude... just chose a valid option...\n");
        }

        return choice;
    }


    private static int readInt(){
        int out;

        String aux = sc.nextLine();

        try{
            out = Integer.parseInt(aux);
        }catch(NumberFormatException e){
            return -1;
        }

        return out;
    }


    private static String askMandatoryInfo(String asking){
        boolean whileCondition;
        String out;

        do{
            System.out.print(asking + ": ");
            out = sc.nextLine();

            whileCondition = false;

            if (out.isEmpty()) {
                System.out.println("\nWhy do you do this? Just write something...");
                whileCondition = true;
            }

            if (out.contains("|") || out.contains(";") || out.contains("\n")){
                System.out.println("\nAre you trying to break me!?\nPlease don't use '|', ';' or '\\n'");
                whileCondition = true;
            }

        }while (whileCondition);

        return out;
    }


    /* ======================================== CALLBACK FUNCTIONS ======================================== */

    public void setNewNotificationsTrue(){
        hasNewNotifications = true;
    }
}
