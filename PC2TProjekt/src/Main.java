import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static int numbersOnly(Scanner sc){
        int number;
        try {
            number = sc.nextInt();
        } catch (Exception e) {
            System.out.print("Zadajte prosim cele cislo: ");
            sc.nextLine();
            number = numbersOnly(sc);
        }
        if (number < 0){
            System.out.print("Zadajte prosim cele cislo: ");
            sc.nextLine();
            number = numbersOnly(sc);
        }
        return number;
    }

    public static void main(String[] args) {
        int cmd;
        Scanner sc = new Scanner(System.in);
        MovieDatabase myDatabase = SQLdb.loadDB();
        Movie exampleMovie;
        String fillerString;
        boolean run = true;
        while (run) {
            System.out.println("\n");
            System.out.println("Vyberte jednu z moznosti:");
            System.out.println("1 : Pridat novy film");
            System.out.println("2 : Upravit film");
            System.out.println("3 : Pridat hodnotenie filmu");
            System.out.println("4 : Vyhladat film");
            System.out.println("5 : Vymazat film");
            System.out.println("6 : Vypis vsetkych filmov");
            System.out.println("7 : Vypis hercov/animatorov, ktori sa podielali na viacerych filmoch");
            System.out.println("8 : Vypis filmov podla herca/animatora");
            System.out.println("9 : Ulozenie filmu do suboru");
            System.out.println("10: Nacitanie filmu zo suboru");
            System.out.println("11: Ukoncit aplikaciu");
            cmd = numbersOnly(sc);
            sc.nextLine();
            switch (cmd) {
                case 1:
                    exampleMovie = myDatabase.addMovie(sc);
                    if (exampleMovie == null)
                        break;
                    fillerString = exampleMovie instanceof AnimatedMovie ? "animatorov ?" : "hercov ?";
                    while (run) {
                        System.out.println("Chcete pridat zoznam " + fillerString);
                        System.out.println("1 : Ano");
                        System.out.println("2 : Nie");
                        cmd = Main.numbersOnly(sc);
                        sc.nextLine();
                        if (cmd == 1) {
                            exampleMovie.addActors(sc);
                            run = false;
                        }
                        else if (cmd == 2)
                            run = false;
                        else
                            System.out.println("Prosim zvolte jednu z moznosti (1 alebo 2)\n");
                    }
                    run = true;
                    break;
                case 2:
                    exampleMovie = myDatabase.getMovie(sc);
                    if (exampleMovie == null) {
                        System.out.println("Zadany film sa v databazi nenachadza\n");
                        break;
                    }
                    while (run) {
                        fillerString = exampleMovie instanceof AnimatedMovie ? "animatorov" : "hercov";
                        System.out.println("Vyberte jednu z moznosti:");
                        System.out.println("1 : Upravit nazov");
                        System.out.println("2 : Upravit rezisera");
                        System.out.println("3 : Upravit rok vydania");
                        System.out.println("4 : Upravit zoznam " + fillerString);
                        System.out.println("5 : Upravit odporucany vek divaka");
                        System.out.println("6 : Ukoncit upravu");
                        cmd = numbersOnly(sc);
                        sc.nextLine();
                        switch (cmd) {
                            case 1:
                                String name = exampleMovie.getName();
                                exampleMovie.setName(sc);
                                if (myDatabase.updateMovie(name, exampleMovie.getName()))
                                    System.out.println("Meno filmu bolo upravene na: " + exampleMovie.getName() + "\n");
                                else
                                    System.out.println("Film s danym menom sa v databazi uz nachadza");
                                break;
                            case 2:
                                exampleMovie.setDirector(sc);
                                System.out.println("Reziser filmu bol upraveny na: " + exampleMovie.getDirector() + "\n");
                                break;
                            case 3:
                                exampleMovie.setYear(sc);
                                System.out.println("Rok vydania filmu bol upraveny na: " + exampleMovie.getYear() + "\n");
                                break;
                            case 4:
                                while (run) {
                                    System.out.println(exampleMovie.printActors());
                                    fillerString = exampleMovie instanceof AnimatedMovie ? "animatora" : "herca";
                                    System.out.println("Chcete pridat " + fillerString + ", vymazat " + fillerString + " alebo vytvorit novy zoznam ?");
                                    System.out.println("1 : Pridat " + fillerString);
                                    System.out.println("2 : Vymazat " + fillerString);
                                    System.out.println("3 : Vytvorit novy zoznam");
                                    fillerString = exampleMovie instanceof AnimatedMovie ? "animatorov " : "hercov ";
                                    System.out.println("4 : Ukoncit upravu zoznamu " + fillerString);
                                    cmd = Main.numbersOnly(sc);
                                    sc.nextLine();
                                    switch (cmd) {
                                        case 1:
                                            exampleMovie.addActors(sc);
                                            break;
                                        case 2:
                                            exampleMovie.removeActor(sc);
                                            break;
                                        case 3:
                                            exampleMovie.setActors(new HashMap<>());
                                            exampleMovie.addActors(sc);
                                            break;
                                        case 4:
                                            run = false;
                                            break;
                                        default:
                                            System.out.println("Prosim zvolte jednu z moznosti (1, 2, 3 alebo 4)\n");
                                            break;
                                    }
                                }
                                run = true;
                                break;
                            case 5:
                                if (exampleMovie instanceof AnimatedMovie)
                                    ((AnimatedMovie) exampleMovie).setAge(sc);
                                else
                                    System.out.println("Odporucany vek divaka je mozne nastavit iba animovanym filmom\n");
                                break;
                            case 6:
                                run = false;
                                break;
                        }
                    }
                    run = true;
                    break;
                case 3:
                    exampleMovie = myDatabase.getMovie(sc);
                    if (exampleMovie == null)
                        System.out.println("Zadany film sa v databazi nenachadza\n");
                    else
                        exampleMovie.addReview(sc);
                    break;
                case 4:
                    exampleMovie = myDatabase.getMovie(sc);
                    if (exampleMovie == null)
                        System.out.println("Zadany film sa v databazi nenachadza\n");
                    else {
                        System.out.println(exampleMovie);
                        System.out.println("\t" + exampleMovie.printReviews());
                    }
                    break;
                case 5:
                    myDatabase.removeMovie(sc);
                    break;
                case 6:
                    myDatabase.getMovieDatabase();
                    break;
                case 7:
                    myDatabase.getMoreActors();
                    break;
                case 8:
                    myDatabase.getMovieByActor(sc);
                    break;
                case 9:
                    exampleMovie = myDatabase.getMovie(sc);
                    if (exampleMovie == null)
                        System.out.println("Zadany film sa v databazi nenachadza\n");
                    else {
                        exampleMovie.writeMovieToAFile();
                    }
                    break;
                case 10:
                    System.out.print("Zadajte meno suboru: ");
                    fillerString = sc.next();
                    myDatabase.readMovieFromAFile(fillerString);
                    break;
                case 11:
                    run = false;
                    if (!myDatabase.getItems().isEmpty()) {
                        myDatabase.saveDB();
                        SQLdb.closeConnection();
                    }
                    sc.close();
                    break;
            }
        }

    }
}