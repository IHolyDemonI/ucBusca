package MulticastRMIServers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MulticastServer {
    /**
     * Queue containing all the requests sent to this server except the indexation requests
     */
    ArrayList<HashMap<String, Object>> requestQueue = new ArrayList<>();

    /**
     * Same as {@link MulticastServer#requestQueue} but contains only indexation requests
     */
    ArrayList<HashMap<String, Object>> indexationQueue = new ArrayList<>();

    //get properties
    Property properties = new Property("UcBusca.properties");

    private String databaseFilePath = properties.getProperty("DatabaseFilePath");
    int quoteLength;

    private MulticastServer() {
        //store properties
        int multicastServerID = Integer.parseInt(properties.getProperty("MulticastServerID"));
        int rmiServerID = Integer.parseInt(properties.getProperty("RMIServerID"));
        int multicastPort = Integer.parseInt(properties.getProperty("MulticastPort"));
        int rmiMulticastPort = Integer.parseInt(properties.getProperty("RMImulticastPort"));
        String multicastAddress = properties.getProperty("MulticastAddress");
        int bufferSize = Integer.parseInt(properties.getProperty("BufferSize"));
        quoteLength = Integer.parseInt(properties.getProperty("QuoteLength"));

        //multicast stuff
        MulticastSocket multicastSocket = null;
        InetAddress multicastGroup;
        byte[] buffer;
        DatagramPacket packet;

        //will be used to temporarily store the request being currently worked on
        HashMap<String, Object> answer;

        try {
            //create multicastSocket and connect it to multicast network
            multicastSocket = new MulticastSocket(multicastPort);
            //multicastSocket.setInterface(InetAddress.getByName(multicastAddress));
            multicastGroup = InetAddress.getByName(multicastAddress);
            multicastSocket.joinGroup(multicastGroup);
            buffer = new byte[bufferSize];

            while (true){
                //receive message
                buffer = new byte[bufferSize];
                packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);
                buffer = packet.getData();

                //transform to HashMap
                answer = ProtocolMessage.bytesToHashMap(buffer);

                //skip if not for me
                if (!answer.get("ServerID").equals(multicastServerID))
                    continue;

                answer = processRequest(answer);

                if (answer != null){
                    //add id of the RMI server
                    answer.put("ServerID", rmiServerID);

                    //send response to multicast
                    buffer = ProtocolMessage.hashMapToBytes(answer);
                    packet = new DatagramPacket(buffer, buffer.length, multicastGroup, rmiMulticastPort);
                    multicastSocket.send(packet);
                }
            }

            /*
            //create the receptionist and run the thread
            MulticastReception receptionist = new MulticastReception(this, multicastServerID, multicastPort, multicastAddress, bufferSize);
            receptionist.run();

            MulticastIndexer indexer = new MulticastIndexer(this, quoteLength);
            indexer.run();

            while (true) {
                // NOT NECESSARY TO CALL FOR wait(), it's called in addRemoveRequest(null) -> getFirstRequest()

                //gets the 1st request, processes it and stores the response ready to be sent back to RMIServer
                response = processRequest(addRemoveRequest(null, false));

                if (response != null){
                    //add id of the RMI server
                    response.put("ServerID", rmiServerID);

                    //send response to multicast
                    buffer = ProtocolMessage.hashMapToBytes(response);
                    packet = new DatagramPacket(buffer, buffer.length, multicastGroup, rmiMulticastPort);
                    multicastSocket.send(packet);
                }
            }
            */

        } catch (UnknownHostException e) {
            System.out.println("line: multicastGroup = InetAddress.getByName(multicastAddress);");
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (multicastSocket != null)
                multicastSocket.close();
        }

    }

    /* =============== REQUEST PROCESSING FUNCTIONS =============== */

    /**
     * Processes the request and returns an appropriate response
     * @param request HashMap with all the information about the request
     * @return HashMap with the answer or null if {@code request} is null
     */
    private HashMap<String, Object> processRequest(HashMap<String, Object> request){
        if (request == null)
            return null;

        switch ((String)request.get("Type")){
            case "register":
                return registerAnswer(request);

            case "loginRequest":
                return loginAnswer(request);

            case "searchHistoryRequest":
                return searchHistoryAnswer(request);

            case "searchRequest":
                return searchAnswer(request);

            case "connectedPagesRequest":
                return connectedPagesAnswer(request);

            case "indexNewURLRequest":
                recursiveIndexation((String) request.get("URL"), (int) request.get("Level"), 0);

            case "getNotificationsRequest":
                return notificationsAnswer(request);

            case "getAdministrationPageRequest":
                return administrationPageAnswer();

            case "grantPrivilegesRequest":
                return grantPrivilegesAnswer(request);
        }

        return null;
    }

    private HashMap<String, Object> registerAnswer(HashMap<String, Object> request){
        HashMap<String, Object> answer = new HashMap<>();

        //get request info
        String username = (String) request.get("Username");
        String password = (String) request.get("Password");

        try {
            Connection connection = DriverManager.getConnection(databaseFilePath);

            //to check if username already exists
            String sql = "SELECT * FROM user WHERE username = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);

            ResultSet result = statement.executeQuery();

            //if username was not found
            if (!result.next()){
                //cannot use variable 'sql' because it'll think I'm using the previous statement, not this one
                String sql2 = "INSERT INTO user(username, password, is_admin) VALUES(?, ?, ?)";
                statement = connection.prepareStatement(sql2);

                statement.setString(1, username);
                statement.setString(2, password);
                statement.setBoolean(3, false);

                statement.execute();

                //connection.commit();
            } else {
                answer.put("Error", "Username already exists");
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //add answer type
        answer.put("Type", "registerAnswer");


        return answer;
    }

    private HashMap<String, Object> loginAnswer(HashMap<String, Object> request){
        HashMap<String, Object> answer = new HashMap<>();

        //get request info
        String username = (String) request.get("Username");
        String password = (String) request.get("Password");

        try {
            Connection connection = DriverManager.getConnection(databaseFilePath);

            //to check if username already exists
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            //if username was not found. Already checks is password is correct because of the "AND" in the sql query
            if (!result.next()){
                answer.put("Error", "Wrong credentials");
            } else {
                //see if is admin
                String sql2 = "SELECT is_admin FROM user WHERE username = ?";
                statement = connection.prepareStatement(sql2);

                statement.setString(1, username);

                result = statement.executeQuery();

                answer.put("isAdmin", result.getBoolean("is_admin"));

                //see if has new notifications
                String sql3 =   "SELECT COUNT(is_new) as has_new_notifications FROM notification " +
                                "WHERE user_username = ? AND is_new = true";
                statement = connection.prepareStatement(sql3);

                statement.setString(1, username);

                result = statement.executeQuery();

                if (result.getInt("has_new_notifications") > 0)
                    answer.put("hasNewNotifications", true);
                else
                    answer.put("hasNewNotifications", false);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //add answer type
        answer.put("Type", "loginAnswer");

        return answer;
    }

    private HashMap<String, Object> searchHistoryAnswer(HashMap<String, Object> request){
        HashMap<String, Object> answer = new HashMap<>();
        ArrayList<String> searchHistory = new ArrayList<>();

        //get request info
        String username = (String) request.get("Username");

        try {
            Connection connection = DriverManager.getConnection(databaseFilePath);

            //to check if username already exists
            String sql =    "SELECT search_history_search " +
                            "FROM search_history_user, user " +
                            "WHERE user.username = ? " +
                            "AND search_history_user.user_username = ?;";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, username);

            ResultSet result = statement.executeQuery();

            //if no searches were found
            if (!result.next()){
                answer.put("Error", "No search history results found");

            } else {
                do{
                    searchHistory.add(result.getString("search_history_search"));
                }while (result.next());

                //add search history
                answer.put("SearchHistory", searchHistory);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //add answer type
        answer.put("Type", "searchHistoryAnswer");

        return answer;
    }

    private HashMap<String, Object> searchAnswer(HashMap<String, Object> request){
        HashMap<String, Object> answer = new HashMap<>();

        ArrayList<String> searchedTitles = new ArrayList<>();
        ArrayList<String> searchedURLs = new ArrayList<>();
        ArrayList<String> searchedCitations = new ArrayList<>();

        String sql, sql2, sql3, sql4, sql5;
        PreparedStatement statement;
        ResultSet result;

        String searchTarget = (String) request.get("SearchTarget");

        ArrayList<String> wordsRequested = getWords(searchTarget.toLowerCase());

        if (!wordsRequested.isEmpty()) {
            try {
                Connection connection = DriverManager.getConnection(databaseFilePath);

                //get the words and build the query
                sql = searchQueryBuilder(wordsRequested);

                statement = connection.prepareStatement(sql);

                result = statement.executeQuery();

                if (result.next()) {
                    do {
                        searchedURLs.add(result.getString("url"));
                        searchedTitles.add(result.getString("title"));
                        searchedCitations.add(result.getString("quote"));
                    } while (result.next());
                }

                sql2 = "SELECT search FROM search_history WHERE search = ?;";

                statement = connection.prepareStatement(sql2);
                statement.setString(1, (String) request.get("SearchTarget"));

                result = statement.executeQuery();

                //already exists, only update
                if (result.next()) {
                    sql3 = "UPDATE search_history SET times_searched = " +
                            "((SELECT times_searched FROM search_history WHERE search = ?) + 1)" +
                            "WHERE search = ?;";

                    statement = connection.prepareStatement(sql3);
                    statement.setString(1, (String) request.get("SearchTarget"));
                    statement.setString(2, (String) request.get("SearchTarget"));

                    statement.execute();
                }
                //if missing, inset
                else {
                    sql4 = "INSERT INTO search_history(search, times_searched) VALUES(?, 1);";

                    statement = connection.prepareStatement(sql4);
                    statement.setString(1, (String) request.get("SearchTarget"));

                    statement.execute();
                }

                if (request.get("Username") != null && !request.get("Username").equals("")){
                    sql5 = "INSERT INTO search_history_user(search_history_search, user_username) VALUES(?, ?);";

                    statement = connection.prepareStatement(sql5);
                    statement.setString(1, (String) request.get("SearchTarget"));
                    statement.setString(2, (String) request.get("Username"));

                    statement.execute();
                }

                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //add answer type
        answer.put("Type", "searchAnswer");

        answer.put("SearchedTitles", searchedTitles);
        answer.put("SearchedURLs", searchedURLs);
        answer.put("SearchedCitations", searchedCitations);

        return answer;
    }

    private String searchQueryBuilder(ArrayList<String> words){
        String querry = "SELECT url, title, quote, COUNT(*) AS num FROM page, page_page WHERE page_url = page.url AND " +
                "url in (SELECT page_url FROM word_page WHERE word_word = ";

        querry = querry.concat(" '" + words.get(0) + "' ");

        for (int i = 1; i < words.size(); i++)
            querry = querry.concat(" INTERSECT SELECT page_url FROM word_page WHERE word_word = '" + words.get(i) + "' ");

        querry = querry.concat(") GROUP BY page_url ORDER BY num DESC;");

        return querry;
    }

    private HashMap<String, Object> connectedPagesAnswer(HashMap<String, Object> request){
        HashMap<String, Object> answer = new HashMap<>();

        String sql;
        PreparedStatement statement;
        ResultSet result;

        ArrayList<String> connectedTitles = new ArrayList<>();
        ArrayList<String> connectedURLs = new ArrayList<>();
        ArrayList<String> connectedCitations = new ArrayList<>();

        //get request info
        String pageURL = (String) request.get("PageURL");

        try {
            Connection connection = DriverManager.getConnection(databaseFilePath);

            //get connected urls
            sql = "SELECT url, title, quote FROM page where url IN (SELECT page_url FROM page_page WHERE origin_url = ?);";

            statement = connection.prepareStatement(sql);
            statement.setString(1, pageURL);

            result = statement.executeQuery();

            if (result.next()){
                do{
                    connectedURLs.add(result.getString("url"));
                    connectedTitles.add(result.getString("title"));
                    connectedCitations.add(result.getString("quote"));
                }while (result.next());
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //add answer type
        answer.put("Type", "connectedPagesAnswer");

        answer.put("ConnectedTitles", connectedTitles);
        answer.put("ConnectedURLs", connectedURLs);
        answer.put("ConnectedCitations", connectedCitations);

        return answer;
    }

    private void recursiveIndexation(String url, int level, int depth) {
        HashMap<String, Object> siteInfo = extractWebsiteInfo(url);

        for (int i = 0; i < depth; i++)
            System.out.print("..");
        System.out.println("Starting indexation, level " + level);

        if (level > 1){
            for (String conn_url : (ArrayList<String>) siteInfo.get("Links")) {
                recursiveIndexation(conn_url, level - 1, depth + 1);
            }
        }

        siteInfo.put("URL", url);

        indexNewURL(siteInfo);

        for (int i = 0; i < depth; i++)
            System.out.print("..");
        System.out.println("Indexation done, level " + level);
    }

    void indexNewURL(HashMap<String, Object> siteInfo){
        String siteTitle = (String) siteInfo.get("DocTitle");
        String url = (String) siteInfo.get("URL");
        String quote = (String) siteInfo.get("Quote");

        //ArrayList<String> titles = (ArrayList<String>) siteInfo.get("Titles");
        //ArrayList<String> links = (ArrayList<String>) siteInfo.get("Links");
        //ArrayList<String> words = (ArrayList<String>) siteInfo.get("Words");

        try {
            Connection connection = DriverManager.getConnection(databaseFilePath);

            String sql = "SELECT url FROM page WHERE url = ?;";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, url);

            ResultSet result = statement.executeQuery();

            //if not found, need to index
            if (!result.next()){
                String sql2 = "INSERT INTO page(url, title, quote) VALUES(?, ?, ?)";

                statement = connection.prepareStatement(sql2);
                statement.setString(1, url);
                statement.setString(2, siteTitle);
                statement.setString(3, quote);

                statement.execute();

                connectURLs(url, (ArrayList<String>) siteInfo.get("Links"));

                connectWords(url, (ArrayList<String>) siteInfo.get("Words"));
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void connectURLs(String original, ArrayList<String> urls){
        String sql, sql2, sql3;
        PreparedStatement statement;
        ResultSet result;

        try {
            Connection connection = DriverManager.getConnection(databaseFilePath);
/*
            sql =   "INSERT INTO page_page (origin_url, page_url) SELECT DISTINCT ?, ? FROM page_page " +
                    "WHERE NOT EXISTS (SELECT 1 FROM page_page WHERE page_page.origin_url = ? AND page_page.page_url = ?);";

            //sql and sql2 won't work if table is empty
            sql2 = "INSERT INTO word(word) VALUES('');";
            statement = connection.prepareStatement(sql2);
            statement.execute();

            for (String url : urls){
                statement = connection.prepareStatement(sql);

                statement.setString(1, original);
                statement.setString(2, url);
                statement.setString(3, original);
                statement.setString(4, url);

                statement.execute();
            }

            sql3 = "DELETE FROM word WHERE word.word = '';";
            statement = connection.prepareStatement(sql3);
            statement.execute();
*/

            //try to find row
            sql = "SELECT origin_url, page_url FROM page_page WHERE origin_url = ? AND page_url = ?;";

            //connect urls
            sql2 = "INSERT INTO page_page(origin_url, page_url) VALUES(?, ?)";

            for (String url : urls){
                statement = connection.prepareStatement(sql);

                statement.setString(1, original);
                statement.setString(2, url);

                result = statement.executeQuery();

                //no row isn't found, istert it
                if (!result.next()){
                    statement = connection.prepareStatement(sql2);

                    statement.setString(1, original);
                    statement.setString(2, url);

                    statement.execute();
                }
            }


            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void connectWords(String url, ArrayList<String> words){
        String sql, sql2, sql3, sql4;
        PreparedStatement statement;
        ResultSet result;

        try {
            Connection connection = DriverManager.getConnection(databaseFilePath);
/*
            sql = "INSERT INTO word (word) SELECT DISTINCT ? FROM word " +
                        "WHERE NOT EXISTS (SELECT 1 FROM word WHERE word.word = ?);";

            sql2 = "INSERT INTO word_page (word_word, page_url) SELECT DISTINCT ?, ? FROM word_page " +
                        "WHERE NOT EXISTS (SELECT 1 FROM word_page WHERE word_page.word_word = ? AND word_page.page_url = ?);";

            //sql and sql2 won't work if table is empty
            sql3 = "INSERT INTO word(word) VALUES('');";
            statement = connection.prepareStatement(sql3);
            statement.execute();

            for (String w : words){
                statement = connection.prepareStatement(sql);
                statement.setString(1, w);
                statement.setString(2, w);
                statement.execute();

                statement = connection.prepareStatement(sql2);
                statement.setString(1, w);
                statement.setString(2, url);
                statement.setString(3, w);
                statement.setString(4, url);
                statement.execute();
            }

            sql4 = "DELETE FROM word WHERE word.word = '';";
            statement = connection.prepareStatement(sql4);
            statement.execute();
 */

            sql = "SELECT word.word FROM word WHERE word.word = ?;";

            sql2 = "INSERT INTO word(word) VALUES(?)";

            sql3 = "SELECT word_word, page_url FROM word_page where word_word = ? AND page_url = ?";

            sql4 = "INSERT INTO word_page(word_word, page_url) VALUES(?, ?)";

            for (String w : words){
                //see if word is already in table
                statement = connection.prepareStatement(sql);

                statement.setString(1, w);

                result = statement.executeQuery();

                //if word not yet in table, put it there
                if (!result.next()){
                    statement = connection.prepareStatement(sql2);

                    statement.setString(1, w);

                    statement.execute();
                }

                //see if connection already exists
                statement = connection.prepareStatement(sql3);

                statement.setString(1, w);
                statement.setString(2, url);

                result = statement.executeQuery();

                //if connection doesn't exist
                if (!result.next()) {
                    //now connect word to page
                    statement = connection.prepareStatement(sql4);

                    statement.setString(1, w);
                    statement.setString(2, url);

                    statement.execute();
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Object> notificationsAnswer(HashMap<String, Object> request){
        HashMap<String, Object> answer = new HashMap<>();

        ArrayList<String> newNotifications = new ArrayList<>();
        ArrayList<String> notifications = new ArrayList<>();

        ArrayList<Integer> newNotificationIDs = new ArrayList<>();


        //get request info
        String username = (String) request.get("Username");

        try {
            Connection connection = DriverManager.getConnection(databaseFilePath);

            //to check if username already exists
            String sql = "SELECT notification_id, message, is_new FROM notification  WHERE user_username = ?;";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);

            ResultSet result = statement.executeQuery();

            //if username was not found
            if (!result.next()){
                answer.put("Error", "No notifications found");

            } else {
                //sort notifications
                do{
                    if (result.getBoolean("is_new")) {
                        newNotifications.add(result.getString("message"));
                        newNotificationIDs.add(result.getInt("notification_id"));
                    }
                    else
                        notifications.add(result.getString("message"));
                }while (result.next());

                //add notifications to answer
                answer.put("NewNotifications", newNotifications);
                answer.put("Notifications", notifications);

                //set new notifications to not new
                String sql2 = "UPDATE notification SET is_new = false WHERE notification_id = ?;";
                statement = connection.prepareStatement(sql2);

                for (Integer i : newNotificationIDs) {
                    statement.setInt(1, i);
                    statement.execute();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //add answer type
        answer.put("Type", "notificationsAnswer");

        return answer;
    }

    private HashMap<String, Object> administrationPageAnswer(){
        HashMap<String, Object> answer = new HashMap<>();
        ArrayList<String> importantPages = new ArrayList<>();
        ArrayList<String> commonSearches = new ArrayList<>();

        String sql, sql2;
        PreparedStatement statement;
        ResultSet result;

        try {
            Connection connection = DriverManager.getConnection(databaseFilePath);

            sql = "SELECT page_url, COUNT(*) AS num FROM page_page, page WHERE page_url = page.url GROUP BY page_url ORDER BY num DESC";

            sql2 = "SELECT search, times_searched FROM search_history ORDER BY times_searched DESC;";

            //important pages
            statement = connection.prepareStatement(sql);
            result = statement.executeQuery();

            if (!result.next())
                System.out.println("ResultSet for important pages is empty.");
            else {
                for (int i = 0; i < 10; i++){
                    importantPages.add(result.getString("page_url"));

                    if (!result.next())
                        break;
                }
            }

            //common searches
            statement = connection.prepareStatement(sql2);
            result = statement.executeQuery();

            if (!result.next())
                System.out.println("ResultSet for common searches is empty");
            else {
                for (int i = 0; i < 10; i++){
                    commonSearches.add(result.getString("search"));

                    if (!result.next())
                        break;
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //add answer type
        answer.put("Type", "administrationPageAnswer");

        answer.put("ImportantPages", importantPages);
        answer.put("CommonSearches", commonSearches);

        return answer;
    }

    private HashMap<String, Object> grantPrivilegesAnswer(HashMap<String, Object> request){
        String sql, sql2, sql3;
        PreparedStatement statement;
        ResultSet result;

        HashMap<String, Object> answer = new HashMap<>();

        //get request info
        String targetUser = (String) request.get("TargetUser");

        try {
            Connection connection = DriverManager.getConnection(databaseFilePath);

            //to check if username exists
            sql = "SELECT username, is_admin FROM user WHERE username = ?;";

            statement = connection.prepareStatement(sql);
            statement.setString(1, targetUser);

            result = statement.executeQuery();

            //if target user was not found
            if (!result.next()){
                answer.put("Error", "No user named " + targetUser + " was found");
            } else {
                if (result.getBoolean("is_admin")){
                    answer.put("Error", targetUser + "is already admin");
                }
                else {
                    //make user admin
                    sql2 = "UPDATE user SET is_admin = true WHERE username = ?";

                    statement = connection.prepareStatement(sql2);
                    statement.setString(1, targetUser);

                    statement.execute();

                    //send notification to user
                    sql3 = "INSERT INTO notification(notification_id, message, is_new, user_username) " +
                            "VALUES((SELECT MAX(notification_id) FROM notification) + 1, 'You have been granted admin privileges', true, ?)";

                    statement = connection.prepareStatement(sql3);
                    statement.setString(1, targetUser);

                    statement.execute();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //add answer type
        answer.put("Type", "grantPrivilegesAnswer");
        answer.put("TargetUser", targetUser);

        return answer;
    }


    /* =============== QUEUE MANIPULATION FUNCTIONS =============== */

    /**
     * The sole purpose of this method is to prevent that elements are added and removed from the request and indexation queue at the same time.
     * If {@code request} is null then either {@link MulticastServer#getFirstRequest()} or {@link MulticastServer#getFirstIndexation()} ()} are executed,
     * otherwise {@link MulticastServer#addRequest(HashMap, boolean)} is
     * @param request Request to be added to the queue. Should be null if the intent is to get the first request in the queue.
     * @param is_indexation should be true if you're trying to add or get an indexation request.
     * @return If {@code request} is null returns the 1st request from {@link MulticastServer#requestQueue}, otherwise returns null.
     * If {@code is_indexation} is true, the 1st request comes from {@link MulticastServer#indexationQueue}
     */
    synchronized HashMap<String, Object> addRemoveRequest(HashMap<String, Object> request, boolean is_indexation){
        if (request != null) {
            addRequest(request, is_indexation);
            return null;
        }
        else{
            if (is_indexation)
                return getFirstIndexation();
            else
                return getFirstRequest();
        }
    }

    /**
     * Removes the first element from the indexation queue and returns it. If queue is empty, causes the server to wait.
     * @return Returns the first element (index 0) in the Queue of requests or null if queue is empty
     */
    private HashMap<String, Object> getFirstIndexation(){
        if (indexationQueue.isEmpty())
            return null;

        HashMap<String, Object> firstRequest = indexationQueue.get(0);

        indexationQueue.remove(0);

        return firstRequest;
    }

    /**
     * Removes the first element from the request queue and returns it. If queue is empty, causes the server to wait.
     * @return Returns the first element (index 0) in the Queue of requests.
     */
    private HashMap<String, Object> getFirstRequest(){
        /*
        if (requestQueue.isEmpty())

            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Server caught InterruptedException while waiting for requests.");
                e.printStackTrace();
            }
        */

        HashMap<String, Object> firstRequest = requestQueue.get(0);

        requestQueue.remove(0);

        return firstRequest;
    }

    /**
     * Adds {@code request} do the end of the request queue. Executes the line of code {@code requestQueue.add(request);}
     * Afterwards notifies all threads
     * @param request Request to be added
     */
    private void addRequest(HashMap<String, Object> request, boolean is_indexation){
        if (is_indexation)
            indexationQueue.add(request);
        else
            requestQueue.add(request);

        //notifyAll();
    }

    private HashMap <String, Object> extractWebsiteInfo(String ws){
        HashMap <String, Object> websiteInfo = new HashMap();

        ArrayList<String> titlesOut = new ArrayList<>();
        ArrayList<String> linksOut = new ArrayList<>();

        Document doc = null;
        Elements links;

        if (! ws.startsWith("http://") && ! ws.startsWith("https://"))
            ws = "http://".concat(ws);

        try {
            // Attempt to connect and get the document
            doc = Jsoup.connect(ws).get();  // Documentation: https://jsoup.org/

            // Title
            websiteInfo.put("DocTitle", doc.title());

        }catch (IOException e){
            System.out.println("Ups... Looks like there was a problem extracting data from the site");
        }

        links = doc.select("a[href]");

        for (Element link : links) {
            // Ignore bookmarks within the page
            if (link.attr("href").startsWith("#"))
                continue;

            // Shall we ignore local links? Otherwise we have to rebuild them for future parsing
            if (!link.attr("href").startsWith("http"))
                continue;

            titlesOut.add(link.text());
            linksOut.add(link.attr("href"));
        }

        // Get website text and count words
        String text = doc.body().text(); // We can use doc.body().text() if we only want to get text from <body></body>

        websiteInfo.put("Quote", "\"" + doc.body().text().substring(0, quoteLength) + "...\"");

        websiteInfo.put("Titles", titlesOut);
        websiteInfo.put("Links", linksOut);

        websiteInfo.put("Words", getWords(text));

        return websiteInfo;
    }

    private ArrayList<String> getWords(String text){
        ArrayList<String> out = new ArrayList<>();
        String line;

        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));

        while (true) {
            try {
                if ((line = reader.readLine()) == null)
                    break;

                String[] words = line.split("[ ,;:.?!“”(){}\\[\\]<>']+");

                for (String word : words) {
                    word = word.toLowerCase();

                    if ("".equals(word))
                        continue;

                    if (!out.contains(word))
                        out.add(word);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    public static void main(String[] args){
        new MulticastServer();
    }
}
