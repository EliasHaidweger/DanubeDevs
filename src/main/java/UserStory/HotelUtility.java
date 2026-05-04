package UserStory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Utility class for loading hotel master data from a text file.
 */
public class HotelUtility {

    /**
     * Reads hotels.txt from the resources folder and parses each line into a Hotel object.
     * @return list of all hotels
     * @throws FileNotFoundException if hotels.txt is missing
     */
    public static ArrayList<Hotel> loadHotelsFromFile() throws FileNotFoundException {
        ArrayList<Hotel> allHotels = new ArrayList<>();
        String path = "src/main/resources/hotels.txt";

        Scanner sc = new Scanner(new File(path));
        // Skip the header line
        sc.nextLine();

        while (sc.hasNextLine()) {
            String[] parts = sc.nextLine().split(",");

            int id = Integer.parseInt(parts[0].trim());
            String category = parts[1].replaceAll("\"", "").trim();
            String name = parts[2].replaceAll("\"", "").trim();
            String owner = parts[3].replaceAll("\"", "").trim();
            String contact = parts[4].replaceAll("\"", "").trim();
            String address = parts[5].replaceAll("\"", "").trim();
            String city = parts[6].replaceAll("\"", "").trim();
            String cityCode = parts[7].replaceAll("\"", "").trim();
            String phone = parts[8].replaceAll("\"", "").trim();
            int noRooms = Integer.parseInt(parts[9].replaceAll("\"", "").trim());
            int noBeds = Integer.parseInt(parts[10].replaceAll("\"", "").trim());

            allHotels.add(new Hotel(id, category, name, owner, contact, address, city, cityCode, phone, noRooms, noBeds));
        }

        return allHotels;
    }
}

