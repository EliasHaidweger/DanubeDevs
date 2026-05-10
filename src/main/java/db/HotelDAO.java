package db;

import model.Hotel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Alle SQL-Queries fuer die hotels-Tabelle.
 */
public class HotelDAO {

    /** Alle Hotels (Story #4). */
    public List<Hotel> getAllHotels() {
        List<Hotel> list = new ArrayList<>();
        String sql = "SELECT * FROM hotels ORDER BY id";

        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Ein Hotel anhand der ID. */
    public Hotel getHotelById(int id) {
        String sql = "SELECT * FROM hotels WHERE id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Liefert die naechste freie ID (hoechste vorhandene ID + 1).
     * Wenn die Tabelle leer ist -> 1.
     */
    public int getNextId() {
        String sql = "SELECT ISNULL(MAX(id), 0) + 1 FROM hotels";

        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * Neues Hotel anlegen (Story #3).
     * ID wird selbst berechnet (max + 1) - kein IDENTITY noetig.
     */
    public boolean insertHotel(Hotel h) {
        // ID automatisch holen
        int newId = getNextId();
        h.setId(newId);

        String sql = "INSERT INTO hotels (id, category, name, owner, contact, address, city, cityCode, phone, noRooms, noBeds, tags) "
                   + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, h.getId());
            ps.setString(2,  h.getCategory());
            ps.setString(3,  h.getName());
            ps.setString(4,  h.getOwner());
            ps.setString(5,  h.getContact());
            ps.setString(6,  h.getAddress());
            ps.setString(7,  h.getCity());
            ps.setString(8,  h.getCityCode());
            ps.setString(9,  h.getPhone());
            ps.setInt(10,    h.getNoRooms());
            ps.setInt(11,    h.getNoBeds());
            ps.setString(12, h.getTags());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Hotel aktualisieren (Story #5). */
    public boolean updateHotel(Hotel h) {
        String sql = "UPDATE hotels SET category=?, name=?, owner=?, contact=?, address=?, city=?, cityCode=?, phone=?, noRooms=?, noBeds=?, tags=? "
                   + "WHERE id=?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1,  h.getCategory());
            ps.setString(2,  h.getName());
            ps.setString(3,  h.getOwner());
            ps.setString(4,  h.getContact());
            ps.setString(5,  h.getAddress());
            ps.setString(6,  h.getCity());
            ps.setString(7,  h.getCityCode());
            ps.setString(8,  h.getPhone());
            ps.setInt(9,     h.getNoRooms());
            ps.setInt(10,    h.getNoBeds());
            ps.setString(11, h.getTags());
            ps.setInt(12,    h.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Hotel + alle Belegungen loeschen (Story #11). */
    public boolean deleteHotel(int id) {
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement p1 = c.prepareStatement("DELETE FROM occupancies WHERE hotel_id = ?");
                 PreparedStatement p2 = c.prepareStatement("DELETE FROM bookings WHERE hotel_id = ?");
                 PreparedStatement p3 = c.prepareStatement("DELETE FROM persona_hotels WHERE hotel_id = ?");
                 PreparedStatement p4 = c.prepareStatement("DELETE FROM hotels WHERE id = ?")) {

                p1.setInt(1, id); p1.executeUpdate();
                p2.setInt(1, id); p2.executeUpdate();
                p3.setInt(1, id); p3.executeUpdate();
                p4.setInt(1, id); p4.executeUpdate();

                c.commit();
                return true;

            } catch (SQLException ex) {
                c.rollback();
                ex.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Hotel map(ResultSet rs) throws SQLException {
        Hotel h = new Hotel();
        h.setId(rs.getInt("id"));
        h.setCategory(rs.getString("category"));
        h.setName(rs.getString("name"));
        h.setOwner(rs.getString("owner"));
        h.setContact(rs.getString("contact"));
        h.setAddress(rs.getString("address"));
        h.setCity(rs.getString("city"));
        h.setCityCode(rs.getString("cityCode"));
        h.setPhone(rs.getString("phone"));
        h.setNoRooms(rs.getInt("noRooms"));
        h.setNoBeds(rs.getInt("noBeds"));
        h.setTags(rs.getString("tags"));
        return h;
    }
}
