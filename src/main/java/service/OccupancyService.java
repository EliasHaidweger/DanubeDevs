package service;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OccupancyService {

    // Story #2: Summary Transactional Data mit optionalen Filtern.
    // null oder 0 fuer einen Parameter = Filter wird ignoriert.
    public void printOccupancySummary(Integer hotelId, Integer year, Integer month, String minCategory) {

        StringBuilder sql = new StringBuilder(
                "SELECT o.year, o.month, h.category, " +
                "SUM(o.usedrooms) AS used_rooms, SUM(o.rooms) AS total_rooms, " +
                "SUM(o.usedbeds) AS used_beds, SUM(o.beds) AS total_beds " +
                "FROM occupancies o " +
                "JOIN hotels h ON h.id = o.hotel_id " +
                "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (hotelId != null && hotelId > 0) {
            sql.append("AND o.hotel_id = ? ");
            params.add(hotelId);
        }
        if (year != null && year > 0) {
            sql.append("AND o.year = ? ");
            params.add(year);
        }
        if (month != null && month > 0) {
            sql.append("AND o.month = ? ");
            params.add(month);
        }
        if (minCategory != null && !minCategory.isEmpty()) {
            // "alle hoeher oder gleich" -> Kategorie als String laenger oder gleich (mehr Sterne = laenger)
            sql.append("AND LEN(h.category) >= ? ");
            params.add(minCategory.length());
        }

        sql.append("GROUP BY o.year, o.month, h.category ORDER BY o.year, o.month, h.category");

        try (Connection con = DatabaseConnection.get();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n=== Occupancy Summary (Story #2) ===");
                System.out.printf("%-6s %-6s %-10s %-15s %-15s%n",
                        "Year", "Month", "Category", "Room Occ. %", "Bed Occ. %");
                System.out.println("--------------------------------------------------------");

                while (rs.next()) {
                    int totalRooms = rs.getInt("total_rooms");
                    int usedRooms = rs.getInt("used_rooms");
                    int totalBeds = rs.getInt("total_beds");
                    int usedBeds = rs.getInt("used_beds");

                    double roomPct = totalRooms == 0 ? 0 : (usedRooms * 100.0 / totalRooms);
                    double bedPct = totalBeds == 0 ? 0 : (usedBeds * 100.0 / totalBeds);

                    System.out.printf("%-6d %-6d %-10s %-15.2f %-15.2f%n",
                            rs.getInt("year"),
                            rs.getInt("month"),
                            rs.getString("category"),
                            roomPct,
                            bedPct);
                }
            }

        } catch (Exception e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }
}
