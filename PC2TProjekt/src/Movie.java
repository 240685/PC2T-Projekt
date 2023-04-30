import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public abstract class Movie {
    private String name;
    private String director;
    private int year;
    private List<Review> reviews;
    private Map<String, String> actors;

    public Movie(String name, String director, int year) {
        this.name = name;
        this.director = director;
        this.year = year;
        actors = new HashMap<>();
        reviews = new ArrayList<>();
    }

    public void addActors(Scanner sc){
        String actor;
        boolean run = true;
        String fillerString = this instanceof AnimatedMovie ? "animatorov " : "hercov  ";
        System.out.println("Mena jednotlivych " + fillerString + "potvrdte klavesou Enter, pre ukoncenie zadajte \"stop\"");
        while (run) {
            fillerString = this instanceof AnimatedMovie ? "animatora filmu " : "herca vo filme ";
            System.out.print("Zadajte meno " + fillerString + this.getName() + ": ");
            fillerString = this instanceof AnimatedMovie ? "animatorov filmu " : "hercov vo filme ";
            actor = sc.nextLine();
            actor = actor.trim();
            if (!actor.equalsIgnoreCase("stop")) {
                actors.put(actor, actor);
                System.out.println(actors.get(actor) + " bol pridany do zoznamu " + fillerString + this.getName());
            }
            else
                run = false;
        }
    }

    public void setActors(Map<String, String> actors) {
        this.actors = actors;
    }

    public Map<String, String> getActors() {
        return actors;
    }

    public void removeActor(Scanner sc){
        String actor;
        String fillerString = this instanceof AnimatedMovie ? "animatora " : "herca ";
        System.out.print("Zadajte meno " + fillerString + " ktoreho chcete zo zoznamu vymazat: ");
        actor = sc.nextLine();
        actor = actor.trim();
        if (actors.get(actor) != null)
            System.out.println(actors.remove(actor) + " bol vymazany zo zoznamu\n");
        else
            System.out.println("Daneho herca nebolo zo zoznamu mozne vymazat\n");
    }

    public String printActors() {
        String string;
        String fillerString = this instanceof AnimatedMovie ? "animatorov " : "hercov ";
        if (actors.isEmpty())
            string = "Zoznam " + fillerString + "je prazdny";
        else {
            string = this instanceof AnimatedMovie ? "Animatori:" : "Herci:";
            Set<String> keys = actors.keySet();
            for (String key : keys) {
                string = string + "\n\t" + actors.get(key);
            }
        }
        return string;
    }

    public String getName() {
        return name;
    }

    public void setName(Scanner sc) {
        String name;
        System.out.println("Aktualny nazov filmu: " + this.getName());
        System.out.print("Zadajte novy nazov filmu: ");
        name = sc.nextLine();
        this.name = name.trim();
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(Scanner sc) {
        String director;
        System.out.println("Aktualny reziser: " + this.getDirector());
        System.out.print("Zadajte nove meno rezisera: ");
        director = sc.nextLine();
        this.director = director.trim();
    }

    public void setDirector(String director){
        this.director = director;
    }

    public int getYear() {
        return year;
    }

    public void setYear(Scanner sc) {
        int year;
        System.out.println("Aktualny rok vydania filmu: " + this.getYear());
        System.out.print("Zadajte novy rok vydania filmu: ");
        year = Main.numbersOnly(sc);
        sc.nextLine();
        this.year = year;
    }

    public void setYear(int year){
        this.year = year;
    }

    public void addReview(Scanner sc){
        int maxPoints = this instanceof AnimatedMovie ? 10 : 5;
        int points;
        boolean run = true;
        int cmd;
        while (true){
            System.out.print("Zadajte hodnotenie (cislo od 1 do " + maxPoints + ") filmu " + this.getName() + " : ");
            points = Main.numbersOnly(sc);
            sc.nextLine();
            if (points < 1 || points > maxPoints)
                System.out.println("Zadajte cislo od 1 do " + maxPoints);
            else
                break;
        }
        Review review = new Review(points);
        while (run) {
            System.out.println("Chcete pridat slovny komentar ? ");
            System.out.println("1 : Ano");
            System.out.println("2 : Nie");
            cmd = Main.numbersOnly(sc);
            sc.nextLine();
            switch (cmd) {
                case 1:
                    System.out.print("Zadajte komentar: ");
                    String comment = sc.nextLine();
                    review.setCommentary(comment);
                    run = false;
                    break;
                case 2:
                    run = false;
                    break;
                default:
                    System.out.println("Prosim zvolte jednu z moznosti (1 alebo 2)");
                    break;
            }
        }
        reviews.add(review);
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public abstract String printReviews();
    public abstract void saveMovieToDB();

    public boolean containsActor(String actorName){
        if (actors.get(actorName) == null)
            return false;
        else
            return true;
    }

    public void writeMovieToAFile(){
        String fileName = this.getName();
        char [] charArray = fileName.toCharArray();
        for (int i = 0; i < charArray.length; i++){
            if (charArray[i] == ' ' || charArray[i] == '\t')
                charArray[i] = '_';
        }
        fileName = new String(charArray);
        File file = new File("Movies", fileName + ".txt");
        FileWriter fw;
        BufferedWriter bw;
        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            if (this instanceof AnimatedMovie)
                bw.write("Animovany film:" + "\n\t" + this.getName() + "\nReziser:\n\t" + this.getDirector() + "\nRok vydania:\n\t" + this.getYear() + "\nOdporucany vek divaka:\n\t" + ((AnimatedMovie) this).getAge() + "\n");
            else
                bw.write("Hrany film:" + "\n\t" + this.getName() + "\nReziser:\n\t" + this.getDirector() + "\nRok vydania:\n\t" + this.getYear() + "\n");
            if (!this.actors.isEmpty())
                bw.write(this.printActors() + "\n");
            if (!this.reviews.isEmpty())
                bw.write(this.printReviews());
            bw.close();
            fw.close();
            System.out.println("Zapis filmu prebehol uspesne");
        } catch (IOException e) {
            System.out.println("Film sa nepodarilo ulozit do suboru");
        }
    }
}