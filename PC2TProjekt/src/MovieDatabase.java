import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MovieDatabase {
    private Map<String, Movie> items;

    public Map<String, Movie> getItems() {
        return items;
    }

    public MovieDatabase(){
        items = new HashMap<>();
    }

    public void addPlayedMovie(String name, String director, int year){
        items.put(name, new PlayedMovie(name, director, year));
    }

    public void addAnimatedMovie(String name, String director, int year, int age){
        items.put(name, new AnimatedMovie(name, director, year, age));
    }

    public Movie addMovie(Scanner sc){
        String name;
        String director;
        int year;
        boolean condition = true;
        int cmd;
        System.out.print("Nazov filmu: ");
        name = sc.nextLine();
        if (items.get(name.trim()) != null){
            System.out.println("Zadany film sa v databazi uz nachadza");
            return null;
        }
        System.out.print("Meno rezisera: ");
        director = sc.nextLine();
        System.out.print("Rok vydania: ");
        year = Main.numbersOnly(sc);
        while (condition) {
            System.out.println("Zvolte o aky film ide:");
            System.out.println("1 : Hrany film");
            System.out.println("2 : Animovany film");
            cmd = Main.numbersOnly(sc);
            switch (cmd) {
                case 1:
                    this.addPlayedMovie(name.trim(), director.trim(), year);
                    condition = false;
                    break;
                case 2:
                    System.out.print("Zadajte odporucany vek divaka: ");
                    int age = Main.numbersOnly(sc);
                    this.addAnimatedMovie(name.trim(), director.trim(), year, age);
                    condition = false;
                    break;
                default:
                    System.out.println("Prosim zvolte jednu z moznosti (1 alebo 2)");
                    break;
            }
        }
        return items.get(name);
    }

    public void getMovieDatabase(){
        if (!(items.isEmpty())) {
            Set<String> keys = items.keySet();
            for (String key : keys) {
                System.out.println(items.get(key));
            }
        }
        else
            System.out.println("Databaza je prazdna");
    }

    public void removeMovie(Scanner sc){
        String name;
        Movie deletedMovie;
        System.out.print("Nazov filmu, ktory chete vymazat: ");
        name = sc.nextLine();
        deletedMovie = items.remove(name.trim());
        if (deletedMovie != null)
            System.out.println("Film " + deletedMovie.getName() + " bol vymazany");
        else
            System.out.println("Zadany film sa v databazi nenachadza");
    }

    public Movie getMovie(Scanner sc){
        System.out.print("Zadajte meno filmu: ");
        String name = sc.nextLine();
        return items.get(name.trim());
    }

    public boolean updateMovie(String oldName, String newName){
        Movie oldMovie = items.get(oldName);
        if (items.get(newName) == null) {
            if (oldMovie instanceof AnimatedMovie) {
                addAnimatedMovie(newName, oldMovie.getDirector(), oldMovie.getYear(), ((AnimatedMovie) oldMovie).getAge());
                items.get(newName).setReviews(oldMovie.getReviews());
            }
            if (oldMovie instanceof PlayedMovie) {
                addPlayedMovie(newName, oldMovie.getDirector(), oldMovie.getYear());
                items.get(newName).setReviews(oldMovie.getReviews());
            }
            items.remove(oldName);
            return true;
        }
        else
            return false;
    }

    public void getMoreActors(){
        List<String> actors = new ArrayList<>();
        Map<String, String> keyActors;
        int i;
        int j;
        int counter = 0;
        if (items.isEmpty())
            System.out.println("Databaza je prazdna");
        else {
            Set<String> Keys = items.keySet();
            for(String key : Keys) {
                keyActors = items.get(key).getActors();
                if (!keyActors.isEmpty()) {
                    Set<String> Keys2 = keyActors.keySet();
                    for (String key2 : Keys2) {
                        actors.add(keyActors.get(key2));
                    }
                }
            }
            Collections.sort(actors);
            for (i = 0; i < actors.size(); i++){
                for (j = i + 1; j < actors.size(); j++) {
                    if (actors.get(i).equals(actors.get(j))) {
                        actors.remove(j);
                        j-= 1;
                        counter++;
                    }
                }
                if (counter == 0) {
                    actors.remove(i);
                    i-= 1;
                }
                counter = 0;
            }
            for (i = 0; i < actors.size(); i++){
                System.out.println(actors.get(i) + ":");
                for (String key : Keys) {
                    if (items.get(key).containsActor(actors.get(i))){
                        System.out.println("\t" + items.get(key).getName());
                    }
                }
            }
            if (actors.isEmpty())
                System.out.println("Ziadni herci/animatori sa nepodielaju na viacerych filmoch");
        }
    }

    public void getMovieByActor(Scanner sc){
        String name;
        System.out.print("Zadajte meno herca/animatora: ");
        name = sc.nextLine();
        name = name.trim();
        boolean isEmpty = true;
        String string = "Zoznam filmov pre: " + name;
        Set<String> Keys = items.keySet();
        for(String key : Keys) {
            if(items.get(key).containsActor(name)) {
                string = string + "\n\t" + items.get(key).getName();
                isEmpty = false;
            }
        }
        if (isEmpty)
            string = string + "\n\t" + name + " sa nepodiela na ziadnych filmoch";
        System.out.println(string);
    }

    public void readMovieFromAFile(String fileName){
        File file = new File("Movies", fileName);
        boolean animated = false;
        String singleLine;
        String reviewString;
        char[] charArray;
        int age = 0;
        int count = 0;
        List <Review> reviews;
        Map<String, String> actors;
        String[] singleReview;
        String actor = "";
        String delimiter = ",";
        FileReader fr;
        BufferedReader br;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            singleLine = br.readLine();
            if (singleLine.equals("Animovany film:"))
                animated = true;
            String name = br.readLine();
            br.readLine();
            String director = br.readLine();
            br.readLine();
            singleLine = br.readLine();
            singleLine = singleLine.trim();
            int year = Integer.parseInt(singleLine);
            if (animated){
                br.readLine();
                singleLine = br.readLine();
                singleLine = singleLine.trim();
                age = Integer.parseInt(singleLine);
            }
            if(animated)
                this.addAnimatedMovie(name.trim(), director.trim(), year, age);
            else
                this.addPlayedMovie(name.trim(), director.trim(), year);
            singleLine = br.readLine();
            if (singleLine.equals("Animatori:") || singleLine.equals("Herci:")){
                actor = br.readLine();
                actors = new HashMap<>();
                while (actor != null && !(actor.equals("Hodnotenia:"))){
                    actors.put(actor.trim(), actor.trim());
                    actor = br.readLine();
                }
                items.get(name.trim()).setActors(actors);
            }
            singleLine = actor;
            if (singleLine.equals("Hodnotenia:")){
                reviewString = br.readLine();
                reviews = new ArrayList<>();
                while (reviewString != null) {
                    singleReview = reviewString.split(delimiter);
                    if (!animated){
                    for (int i = 0; i < singleReview[0].length(); i++) {
                        if (singleReview[0].charAt(i) == '*')
                            count++;
                    }
                    Review tempReview = new Review(count);
                    count = 0;
                    if (singleReview.length == 2)
                        tempReview.setCommentary(singleReview[1]);
                    reviews.add(tempReview);
                    }
                    else {
                        singleReview[0] = singleReview[0].trim();
                        charArray = singleReview[0].toCharArray();
                        charArray[charArray.length - 1] = ' ';
                        charArray[charArray.length - 2] = ' ';
                        charArray[charArray.length - 3] = ' ';
                        String tempStr = new String(charArray);
                        tempStr = tempStr.trim();
                        singleReview[0] = tempStr;
                        Review tempReview = new Review(Integer.parseInt(singleReview[0]));
                        if (singleReview.length == 2)
                            tempReview.setCommentary(singleReview[1]);
                        reviews.add(tempReview);
                    }
                    reviewString = br.readLine();
                }
                items.get(name.trim()).setReviews(reviews);
            }
            br.close();
            fr.close();
            System.out.println("Nacitanie filmu prebehlo uspesne");
        } catch (IOException e) {
            System.out.println("Zadany film sa nepodarilo nacitat");
        }
    }

    public void saveDB(){
        Set<String> keys = items.keySet();
        for (String key : keys){
            items.get(key).saveMovieToDB();
        }
    }
}