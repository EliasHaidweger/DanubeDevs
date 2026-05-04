package service;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class HotelService {

    // Story #1: Summary über Master Data - Hotels pro Kategorie + Durchschnittsgröße
    public void printMasterDataSummary() {
        String sql = "SELECT category, " +
                     "COUNT(*) AS hotel_count, " +
                     "AVG(CAST(noRooms AS FLOAT)) AS avg_rooms, " +
                     "AVG(CAST(noBeds AS FLOAT)) AS avg_beds " +
                     "FROM hotels " +
                     "GROUP BY category " +
                     "ORDER BY category";

        try (Connection con = DatabaseConnection.get();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n=== Master Data Summary (Story #1) ===");
            System.out.printf("%-10s %-12s %-12s %-12s%n", "Category", "Hotels", "Avg Rooms", "Avg Beds");
            System.out.println("-----------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-10s %-12d %-12.2f %-12.2f%n",
                        rs.getString("category"),
                        rs.getInt("hotel_count"),
                        rs.getDouble("avg_rooms"),
                        rs.getDouble("avg_beds"));
            }

        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    // Story #11: Hotel + verknüpfte Transactional Data löschen
    public void deleteHotelWithData(int hotelId, boolean confirmed) {
        if (!confirmed) {
            System.out.println("Loeschen abgebrochen.");
            return;
        }

        try (Connection con = DatabaseConnection.get()) {
            con.setAutoCommit(false); // Transaktion: alles oder nichts

            try (PreparedStatement delOcc = con.prepareStatement("DELETE FROM occupancies WHERE hotel_id = ?");
                 PreparedStatement delBook = con.prepareStatement("DELETE FROM bookings WHERE hotel_id = ?");
                 PreparedStatement delHotel = con.prepareStatement("DELETE FROM hotels WHERE id = ?")) {

                delOcc.setInt(1, hotelId);
                delOcc.executeUpdate();

                delBook.setInt(1, hotelId);
                delBook.executeUpdate();

                delHotel.setInt(1, hotelId);
                int rows = delHotel.executeUpdate();

                if (rows > 0) {
                    con.commit();
                    System.out.println("Hotel " + hotelId + " und alle verknuepften Daten geloescht.");
                } else {
                    con.rollback();
                    System.out.println("Hotel mit ID " + hotelId + " nicht gefunden.");
                }

            } catch (Exception e) {
                con.rollback();
                System.out.println("Fehler beim Loeschen: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
}
