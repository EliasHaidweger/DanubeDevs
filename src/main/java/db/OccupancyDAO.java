package db;

import model.Occupancy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Alle SQL-Queries fuer die occupancies-Tabelle.
 */
public class OccupancyDAO {

    /** Alle Belegungen eines Hotels (Story #10). */
    public List<Occupancy> getOccupanciesByHotel(int hotelId) {
        List<Occupancy> list = new ArrayList<>();
        String sql = "SELECT * FROM occupancies WHERE hotel_id = ? ORDER BY year DESC, month DESC";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, hotelId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Belegungen eines Hotels in einem Zeitraum (Story #10 mit FROM/TO).
     * fromYear/fromMonth = Anfang, toYear/toMonth = Ende (inklusive).
     *
     * Trick: wir vergleichen (year*12 + month) - das ergibt eine fortlaufende Zahl.
     * Z.B. Mai 2026 = 2026*12 + 5 = 24317
     */
    public List<Occupancy> getOccupanciesByHotelInRange(int hotelId,
                                                       int fromYear, int fromMonth,
                                                       int toYear, int toMonth) {
        List<Occupancy> list = new ArrayList<>();
        String sql = "SELECT * FROM occupancies "
                   + "WHERE hotel_id = ? "
                   + "AND (year * 12 + month) >= (? * 12 + ?) "
                   + "AND (year * 12 + month) <= (? * 12 + ?) "
                   + "ORDER BY year, month";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, hotelId);
            ps.setInt(2, fromYear);
            ps.setInt(3, fromMonth);
            ps.setInt(4, toYear);
            ps.setInt(5, toMonth);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Belegungen fuer einen bestimmten Monat/Jahr (Story #2). */
    public List<Occupancy> getOccupanciesByMonth(int year, int month) {
        List<Occupancy> list = new ArrayList<>();
        String sql = "SELECT * FROM occupancies WHERE year = ? AND month = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Speichern (INSERT oder UPDATE - Story #6). */
    public boolean saveOccupancy(Occupancy o) {
        String checkSql = "SELECT COUNT(*) FROM occupancies WHERE hotel_id=? AND year=? AND month=?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement check = c.prepareStatement(checkSql)) {

            check.setInt(1, o.getHotelId());
            check.setInt(2, o.getYear());
            check.setInt(3, o.getMonth());

            boolean exists = false;
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) exists = true;
            }

            String sql = exists
                ? "UPDATE occupancies SET rooms=?, usedrooms=?, beds=?, usedbeds=? WHERE hotel_id=? AND year=? AND month=?"
                : "INSERT INTO occupancies (rooms, usedrooms, beds, usedbeds, hotel_id, year, month) VALUES (?,?,?,?,?,?,?)";

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, o.getRooms());
                ps.setInt(2, o.getUsedRooms());
                ps.setInt(3, o.getBeds());
                ps.setInt(4, o.getUsedBeds());
                ps.setInt(5, o.getHotelId());
                ps.setInt(6, o.getYear());
                ps.setInt(7, o.getMonth());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Occupancy map(ResultSet rs) throws SQLException {
        Occupancy o = new Occupancy();
        o.setHotelId(rs.getInt("hotel_id"));
        o.setRooms(rs.getInt("rooms"));
        o.setUsedRooms(rs.getInt("usedrooms"));
        o.setBeds(rs.getInt("beds"));
        o.setUsedBeds(rs.getInt("usedbeds"));
        o.setYear(rs.getInt("year"));
        o.setMonth(rs.getInt("month"));
        return o;
    }
}
