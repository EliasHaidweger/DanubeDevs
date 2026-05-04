package UserStory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


/**
 * Utility class for loading occupancy transactional data from a file.
 * Provides a helper to determine the most recent record per hotel.
 */
public class OccupancyUtility {

    /**
     * Reads occupancies.txt and parses each line into an Occupancy object.
     * @return list of all occupancy records
     * @throws FileNotFoundException if occupancies.txt is missing
     */
    public static ArrayList<Occupancy> loadOccupanciesFromFile() throws FileNotFoundException {
        ArrayList<Occupancy> allOccupancies = new ArrayList<>();
        String path = "src/main/resources/occupancies.txt";

        Scanner sc = new Scanner(new File(path));
        // Skip the header line
        sc.nextLine();

        while (sc.hasNextLine()) {
            String[] parts = sc.nextLine().split(",");

            int id = Integer.parseInt(parts[0].trim());
            int rooms = Integer.parseInt(parts[1].trim());
            int usedRooms = Integer.parseInt(parts[2].trim());
            int beds = Integer.parseInt(parts[3].trim());
            int usedBeds = Integer.parseInt(parts[4].trim());
            int year = Integer.parseInt(parts[5].trim());
            int month = Integer.parseInt(parts[6].trim());

            allOccupancies.add(new Occupancy(id, rooms, usedRooms, beds, usedBeds, year, month));
        }

        return allOccupancies;
    }

    /**
     * Finds the most recent occupancy record for each hotel.
     * "Most recent" means the highest year; if the year is the same, the highest month wins.
     *
     * @param occupancies full list of occupancy records
     * @return map of hotel ID -> latest Occupancy record
     */
    public static Map<Integer, Occupancy> getLastReportedByHotel(ArrayList<Occupancy> occupancies) {
        Map<Integer, Occupancy> lastReported = new HashMap<>();

        for (Occupancy occ : occupancies) {
            int hotelId = occ.getId();

            if (!lastReported.containsKey(hotelId)) {
                lastReported.put(hotelId, occ);
            } else {
                Occupancy existing = lastReported.get(hotelId);

                boolean isNewerYear = occ.getYear() > existing.getYear();
                boolean isSameYearNewerMonth = occ.getYear() == existing.getYear()
                        && occ.getMonth() > existing.getMonth();

                if (isNewerYear || isSameYearNewerMonth) {
                    lastReported.put(hotelId, occ);
                }
            }
        }

        return lastReported;
    }
}
