import service.HotelService;
import service.OccupancyService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        HotelService hotelService = new HotelService();
        OccupancyService occupancyService = new OccupancyService();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Lower Austria Tourist Portal ===");
            System.out.println("1) Master Data Summary (Story #1)");
            System.out.println("2) Occupancy Summary (Story #2)");
            System.out.println("3) Hotel loeschen (Story #11)");
            System.out.println("0) Beenden");
            System.out.print("Auswahl: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    hotelService.printMasterDataSummary();
                    break;

                case "2":
                    System.out.print("Hotel ID (leer = alle): ");
                    Integer hotelId = parseOrNull(sc.nextLine());
                    System.out.print("Jahr (leer = alle): ");
                    Integer year = parseOrNull(sc.nextLine());
                    System.out.print("Monat 1-12 (leer = alle): ");
                    Integer month = parseOrNull(sc.nextLine());
                    System.out.print("Min. Kategorie z.B. *** (leer = alle): ");
                    String cat = sc.nextLine().trim();
                    occupancyService.printOccupancySummary(hotelId, year, month, cat.isEmpty() ? null : cat);
                    break;

                case "3":
                    System.out.print("Hotel ID zum Loeschen: ");
                    int delId = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Bist du sicher? (j/n): ");
                    boolean confirm = sc.nextLine().trim().equalsIgnoreCase("j");
                    hotelService.deleteHotelWithData(delId, confirm);
                    break;

                case "0":
                    System.out.println("Tschuess!");
                    return;

                default:
                    System.out.println("Ungueltige Auswahl.");
            }
        }
    }

    private static Integer parseOrNull(String s) {
        s = s.trim();
        if (s.isEmpty()) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
