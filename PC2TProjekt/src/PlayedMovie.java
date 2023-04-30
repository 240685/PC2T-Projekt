import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;


public class PlayedMovie extends Movie{
    public PlayedMovie(String name, String director, int year) {
        super(name, director, year);
    }

    @Override
    public String toString() {
        String string = "\nHrany film: " + this.getName() + ", Reziser: " + this.getDirector() + ", Rok vydania: " + this.getYear();
        string = string + "\n\t" + this.printActors();
        return string;
    }

    @Override
    public String printReviews(){
        String string;
        if (this.getReviews().isEmpty())
            string = "Film nema zadane ziadne hodnotenia";
        else {
            string = "Hodnotenia:";
            Collections.sort(this.getReviews(), Collections.reverseOrder());
            for (int i = 0; i < this.getReviews().size(); i++) {
                string = string + "\n\t";
                for (int j = 0; j < this.getReviews().get(i).getPoints(); j++){
                    string = string + "* ";
                }
                if (this.getReviews().get(i).getCommentary() != null)
                    string = string + ", " + this.getReviews().get(i).getCommentary();
            }
            }
        return string;
    }

    @Override
    public void saveMovieToDB() {
        Connection conn = SQLdb.getDBConnection();
        String sql = "INSERT INTO movies (name,director,year,type) VALUES(?,?,?,?)";
        try (PreparedStatement prStmt = conn.prepareStatement(sql)) {
            prStmt.setString(1, this.getName());
            prStmt.setString(2, this.getDirector());
            prStmt.setInt(3, this.getYear());
            prStmt.setString(4, "Played");

            prStmt.executeUpdate();

            if (!this.getReviews().isEmpty()){
                for (Review review : this.getReviews()) {
                    review.saveReviewToDB(this.getName());
                }
            }

            if (!this.getActors().isEmpty()){
                int id = SQLdb.getMovieID(this.getName());
                SQLdb.saveActorsToDB(id, this.getActors());
            }
        } catch (SQLException e) {
            System.out.println("Nastala vynimka" + e);
            System.out.println("Ulozenie filmu " + this.getName() + " nebolo mozne");
        }
    }
}