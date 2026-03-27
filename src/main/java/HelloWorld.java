import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HelloWorld {
    public static void main(String[] args) {

        File hotels= new File("src/main/resources/hotels.txt");
        Scanner scanner1 = null;
        try {
            scanner1 = new Scanner(hotels);
            while (scanner1.hasNextLine()) {
                System.out.println(scanner1.nextLine());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        File occupancies= new File("src/main/resources/occupancies.txt");
        Scanner scanner2 = null;
        try {
            scanner2 = new Scanner(occupancies);
            while (scanner2.hasNextLine()) {
                System.out.println(scanner2.nextLine());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
