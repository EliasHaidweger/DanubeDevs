import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HotelUtility {

    private static final String PATH = "src/main/resources/hotels.txt";

    //Liste lädt alle vorhanden Daten aus der Datei
    public static ArrayList<Hotel> loadHotelsFromFile() throws FileNotFoundException {
        ArrayList<Hotel> hotels = new ArrayList<>();

        Scanner sc = new Scanner(new File(PATH));

        while(sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isBlank()) {
                continue; //Falls leere Zeilen
            }
            //Zeilen einlesen und beim Komma splitten
            String[] parts = sc.nextLine().split(",");

            //neues Hotel Objekt und Anführungszeichen entfernen
            Hotel h = new Hotel(
                    Integer.parseInt(parts[0].replace("\"", "")),
                    parts[1].replace("\"", ""),
                    parts[2].replace("\"", ""),
                    parts[3].replace("\"", ""),
                    parts[4].replace("\"", ""),
                    parts[5].replace("\"", ""),
                    parts[6].replace("\"", ""),
                    parts[7].replace("\"", ""),
                    parts[8].replace("\"", ""),
                    Integer.parseInt(parts[9].replace("\"", "")),
                    Integer.parseInt(parts[10].replace("\"", ""))
            );
            hotels.add(h);
        }
        sc.close();
        return hotels;
    }
    //Erzeugt neue eindeutige Hotel ID
    public static int generateNewHotelId() throws FileNotFoundException {
        ArrayList<Hotel> hotels = loadHotelsFromFile();

        int maxId = 0;

        //Durch alle Hotels gehen um die höchste ID zu finden
        for(Hotel h : hotels){
            if(h.getId() > maxId){
                maxId = h.getId();
            }
        }
        //Neue ID ist der Nachfolger von der höchsten
        return maxId + 1;
    }
    public static void saveHotel(Hotel hotel) throws IOException {

        String csvLine =
                "\"" + hotel.getId() + "\"," +
                        "\"" + hotel.getCategory() + "\"," +
                        "\"" + hotel.getName() + "\"," +
                        "\"" + hotel.getOwner() + "\"," +
                        "\"" + hotel.getContact() + "\"," +
                        "\"" + hotel.getAddress() + "\"," +
                        "\"" + hotel.getCity() + "\"," +
                        "\"" + hotel.getCityCode() + "\"," +
                        "\"" + hotel.getState() + "\"," +
                        "\"" + hotel.getNoRooms() + "\"," +
                        "\"" + hotel.getNoBeds() + "\"";

        //neue Zeile ans Ende der Datei hinzufügen
        Files.write(Path.of(PATH), List.of(csvLine), StandardOpenOption.APPEND);
    }
}





