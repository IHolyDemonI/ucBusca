package Testing;

import java.sql.*;

public class DatabaseTesting {

    public static void main(String[] args){
        String databaseFilePath = "C:\\Users\\Holy Demon\\Desktop\\ucBusca\\database.sqlite";
        Connection connection = null;

        //String sql_stmt = "INSERT INTO user(username, password, is_admin) VALUES('artem', 'artem', false);";
        String sql_stmt = "SELECT url, title, quote FROM page where url in (SELECT DISTINCT page_url FROM word_page WHERE word_word in ('w2', 'w3'));";
        try{
            connection = DriverManager.getConnection("jdbc:sqlite:\\\\" + databaseFilePath);

            PreparedStatement statement = connection.prepareStatement(sql_stmt);

            statement.execute();

            ResultSet result = statement.executeQuery();

            if (!result.next()) {
                System.out.println("ResultSet is empty.");
            } else {
                do {
                    System.out.println(result.getString("url") + " " + result.getString("title") + " " + result.getString("quote"));
                } while (result.next());
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
