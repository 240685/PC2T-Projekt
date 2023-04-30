import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Review implements Comparable<Review>{
    private int points;
    private String commentary;
    private static int count = 0;
    private int ID;

    public Review(int points) {
        this.points = points;
        count++;
        ID = count;
    }

    public int getID() {
        return ID;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public int getPoints() {
        return points;
    }

    public String getCommentary() {
        return commentary;
    }

    public void saveReviewToDB(String name){
        Connection conn = SQLdb.getDBConnection();
        int id = SQLdb.getMovieID(name);
        String sql = "INSERT INTO reviews (movie_id,points,commentary) VALUES(?,?,?)";
        try (PreparedStatement prStmt = conn.prepareStatement(sql)) {
            prStmt.setInt(1, id);
            prStmt.setInt(2, this.getPoints());
            prStmt.setString(3, this.getCommentary());

            prStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Nastala vynimka" + e);
            System.out.println("Ulozenie hodnotenia nebolo mozne");
        }
    }

    @Override
    public int compareTo(Review rev) {
        if (this.getPoints() < rev.getPoints())
            return -1;
        if (this.getPoints() > rev.getPoints())
            return 1;
        return 0;
    }

    @Override
    public int hashCode(){
        return ID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Review)
            return this.ID == ((Review) obj).ID;
        return false;
    }
}
