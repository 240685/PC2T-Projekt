import java.sql.*;
import java.util.Map;
import java.util.Set;

public class SQLdb {

    private static volatile Connection dbConnection;

    public static Connection getDBConnection() {
        if (dbConnection == null) {
            synchronized (SQLdb.class) {
                if (dbConnection == null) {
                    try {
                        Class.forName("org.sqlite.JDBC");
                        dbConnection = DriverManager.getConnection("jdbc:sqlite:db/Movies.db");
                    } catch (SQLException | ClassNotFoundException e) {
                        System.out.println("Nastala vynimka " + e);
                    }
                }
            }
        }
        return dbConnection;
    }

    public static void closeConnection() {
        if (dbConnection != null) {
            try {
                dbConnection.close();
            } catch (SQLException e) {
                System.out.println("Nastala vynimka " + e);
                System.out.println("Spojenie nebolo mozne uzavriet");
            }
        }
    }

    public static int getMovieID(String name){
        Connection conn = getDBConnection();
        String sql = "SELECT movie_id FROM movies WHERE name = ?";
        int id = 0;
        try {
            PreparedStatement prStmt = conn.prepareStatement(sql);
            prStmt.setString(1, name);
            ResultSet rs = prStmt.executeQuery();
            id = rs.getInt("movie_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static void saveActorsToDB(int id, Map<String, String> actors){
        Connection conn = getDBConnection();
        Set<String> keys = actors.keySet();
        for (String key : keys){
            String sql = "INSERT INTO actors (movie_id,actor) VALUES(?,?)";
            try {
                PreparedStatement prStmt = conn.prepareStatement(sql);
                prStmt.setInt(1, id);
                prStmt.setString(2, actors.get(key));
                prStmt.executeUpdate();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public static void clearDB(){
        Connection conn = SQLdb.getDBConnection();
        try {
            PreparedStatement prStmt = conn.prepareStatement("DELETE FROM movies");
            prStmt.executeUpdate();
            prStmt = conn.prepareStatement("DELETE FROM actors");
            prStmt.executeUpdate();
            prStmt = conn.prepareStatement("DELETE FROM reviews");
            prStmt.executeUpdate();
        }catch (SQLException e){
            System.out.println("Nastala vynimka typu " + e);
        }
    }

    public static MovieDatabase loadDB(){
        Connection conn = SQLdb.getDBConnection();
        String sql;
        String name;
        String director;
        int year;
        String type;
        int age;
        int id;
        int points;
        String commentary;
        String actor;
        MovieDatabase myDB = new MovieDatabase();
        try {
            PreparedStatement prStmt = conn.prepareStatement("SELECT * FROM movies");
            ResultSet rs = prStmt.executeQuery();
            while(rs.next()){
                name = rs.getString("name");
                director = rs.getString("director");
                year = rs.getInt("year");
                type = rs.getString("type");
                age = rs.getInt("age");
                id = rs.getInt("movie_id");

                if(type.equals("Animated")){
                    myDB.addAnimatedMovie(name, director, year, age);
                }
                else
                    myDB.addPlayedMovie(name, director,year);

                sql = "SELECT actor FROM actors WHERE movie_id = ?";
                PreparedStatement prStmt2 = conn.prepareStatement(sql);
                prStmt2.setInt(1, id);
                ResultSet rs2 = prStmt2.executeQuery();
                while(rs2.next()){
                    actor = rs2.getString("actor");
                    myDB.getItems().get(name).getActors().put(actor, actor);
                }

                sql = "SELECT points, commentary FROM reviews WHERE movie_id = ?";
                PreparedStatement prStmt3 = conn.prepareStatement(sql);
                prStmt3.setInt(1, id);
                ResultSet rs3 = prStmt3.executeQuery();
                while(rs3.next()){
                    points = rs3.getInt("points");
                    commentary = rs3.getString("commentary");
                    Review review = new Review(points);
                    review.setCommentary(commentary);
                    myDB.getItems().get(name).getReviews().add(review);
                }
            }
            clearDB();
        } catch (SQLException e){
            System.out.println("Nastala vynimka " + e);
            System.out.println("Databazu sa nepodarilo nacitat");
        }
        return myDB;
    }
}
