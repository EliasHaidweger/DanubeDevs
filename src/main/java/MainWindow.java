import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * Starts the main window for editing transactional occupancy data (rooms, beds, month/year).
 * US06
 */

public class MainWindow {
    public static void main(String[] args) {

        Hotel hotel = new Hotel(3,"**","Haus am See","hubert","123","sesamstrasse1","wien","1020","Hawaii",300,600);

        EditTransactionalData l = new EditTransactionalData(hotel);
        l.setVisible(true);
    }
}
