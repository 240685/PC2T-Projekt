import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Scanner;

public class AnimatedMovie extends Movie{
    private int age;
    public AnimatedMovie(String name, String director, int year, int age) {
        super(name, director, year);
        this.age = age;
    }

    public int getAge() {
        return age;
    }
    public void setAge(Scanner sc){
        int age;
        System.out.println("Aktualny odporucany vek divaka: " + this.getAge());
        System.out.print("Zadajte novy odporucany vek divaka: ");
        age = Main.numbersOnly(sc);
        sc.nextLine();
        this.age = age;
    }

    @Override
    public String toString() {
        String string =  "\nAnimovany film: " + this.getName() + ", Reziser: " + this.getDirector() + ", Rok vydania: " + this.getYear() + ", Odporucany  vek divaka: " + this.getAge();
        string = string + "\n\t" + this.printActors();
        return string;
    }

    @Override
    public String printReviews() {
        String string;
        if (this.getReviews().isEmpty())
            string = "Film nema zadane ziadne hodnotenia";
        else {
            string = "Hodnotenia:";
            Collections.sort(this.getReviews(), Collections.reverseOrder());
            for (int i = 0; i < this.getReviews().size(); i++) {
                string = string + "\n\t" + this.getReviews().get(i).getPoints() + "/10";
                if (this.getReviews().get(i).getCommentary() != null)
                    string = string + ", " + this.getReviews().get(i).getCommentary();
            }
        }
        return string;
    }

    @Override
    public void saveMovieToDB() {
        Connection conn = SQLdb.getDBConnection();
        String sql = "INSERT INTO movies (name,director,year,type,age) VALUES(?,?,?,?,?)";
        try (PreparedStatement prStmt = conn.prepareStatement(sql)) {
            prStmt.setString(1, this.getName());
            prStmt.setString(2, this.getDirector());
            prStmt.setInt(3, this.getYear());
            prStmt.setString(4, "Animated");
            prStmt.setInt(5, this.getAge());

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
