import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OccupancyUtility {
    private static final String PATH = "src/main/resources/occupancies.txt";

    //methode returntype name()
    public static ArrayList<Occupancies> loadOccupanciesFromFile() throws FileNotFoundException {
        ArrayList<Occupancies> occupancies = new ArrayList<>();

        Scanner sc = new Scanner(new File(PATH));

        while (sc.hasNextLine()) {//solange noch eine nextline gibt, also bis zum Ende der Liste
            String line = sc.nextLine();
            if (line.isBlank()) {
                continue; //Falls leere Zeile, wird die übersprungen
            }
            //Zeilen einlesen und beim Komma splitten
            String[] parts = sc.nextLine().split(",");

            //neues Objekt erstellen
            //id,rooms,usedrooms,beds,usedbeds,year,month
            Occupancies o = new Occupancies(// beim übergeben wird noch umgewandelt
                    //part 0 wird eingelesen in integer mit parseint umgewandelt und als id dem constructor in Occupancie übergeben
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]),
                    Integer.parseInt(parts[4]),
                    Integer.parseInt(parts[5]),
                    Integer.parseInt(parts[6])
            );//um später auslesen zu können/zum überprüfen
            occupancies.add(o); //adds to ArrayList
        }
        sc.close();
        return occupancies;
    }

    public static void saveOccupancies(Occupancies o) throws IOException {

        String csvLine =
                //id,rooms,usedrooms,beds,usedbeds,year,month
                o.id+","+o.rooms+","+o.usedRooms+","+o.beds+","+o.usedBeds+","+o.year+","+o.month;

        //neue Zeile ans Ende der Datei hinzufügen
        Files.write(Path.of(PATH), List.of(csvLine), StandardOpenOption.APPEND); //write at the end of the list (autom. neue zeile)
        //
    }


}
