import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * View window that allows adding new transactional occupancy data (rooms, beds, month/year) per hotel.
 * US06
 *   AC-06-1: Hotel ID and name are pre-filled and locked (not editable)
 *   AC-06-2: Room occupancy, bed occupancy, and month fields are editable
 *   AC-06-3: Entering a month that already has data for the same hotel shows a warning
 * Structure layout documentation:
 *   GUI (constructor + setupUI) -> setupListeners -> validateInputs -> handleSave -> OccupancyUtility (scanners for txt files)
 */


public class OccupancyUtility {
    private static final String PATH = "src/main/resources/occupancies.txt";

    //Methode Returntype Name()
    //Lädt alle gespeicherten Occupancies-Daten aus der Textdatei in eine ArrayList
    public static ArrayList<Occupancies> loadOccupanciesFromFile() throws FileNotFoundException {
        ArrayList<Occupancies> occupancies = new ArrayList<>();

        Scanner sc = new Scanner(new File(PATH));//liest die Datei

        boolean isFirstLine = true; // Flag, um zu erkennen, ob man gerade in der ersten Zeile (Kopfzeile) ist, die man überspringen will

        while (sc.hasNextLine()) { // Solange noch eine nächste Zeile gibt
            String line = sc.nextLine(); // Eine Zeile einlesen

            if (line.isBlank()) {
                continue; // Falls leere Zeile, wird diese übersprungen
            }

            // Erste nicht-leere Zeile ist die Kopfzeile (id,rooms,usedrooms,...) --> überspringen
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }

            // Zeile beim Komma splitten --> ergibt ein Array mit 7 Teilen
            String[] parts = line.split(",");

            // Neues Objekt erstellen
            // Reihenfolge: id,rooms,usedrooms,beds,usedbeds,year,month
            Occupancies o = new Occupancies(// beim Übergeben wird noch umgewandelt
                    // part[0] bis [6] werden in Integer umgewandelt und an den Constructor in Occupancies übergeben
                    Integer.parseInt(parts[0].trim()),// .trim() entfernt eventuelle Leerzeichen um das Komma herum
                    Integer.parseInt(parts[1].trim()),
                    Integer.parseInt(parts[2].trim()),
                    Integer.parseInt(parts[3].trim()),
                    Integer.parseInt(parts[4].trim()),
                    Integer.parseInt(parts[5].trim()),
                    Integer.parseInt(parts[6].trim())
            ); // Um spaeter fuer andere US auslesen zu können / zum Überprüfen
            occupancies.add(o); // Erstelltes Occupancies-Objekt adds to ArrayList
        }
        sc.close();
        return occupancies;//Die vollständig gefüllte ArrayList mit allen Occupancies zurückgeben
    }

    //Die Daten werden als CSV-Zeile (durch Komma getrennt) angehängt.
    public static void saveOccupancies(Occupancies o) throws IOException {

        String csvLine =
                //id,rooms,usedrooms,beds,usedbeds,year,month
                o.id + "," + o.rooms + "," + o.usedRooms + "," + o.beds + "," + o.usedBeds + "," + o.year + "," + o.month;

        //neue Zeile(=neues Occupancies-Objekt) ans Ende der Datei hinzufügen
        //StandardOpenOption.APPEND sorgt dafür, dass die Datei nicht überschrieben wird,
        Files.write(Path.of(PATH), List.of(csvLine), StandardOpenOption.APPEND); //write at the end of the list (autom. neue zeile)
    }


}
