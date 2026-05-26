package db;

import model.Persona;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL-Queries fuer persona + persona_hotels.
 */
public class PersonaDAO {

    /** Login pruefen. */
    public Persona login(String username, String password) {
        String sql = "SELECT * FROM persona WHERE username = ? AND password = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Alle Personas (Story #12). */
    public List<Persona> getAllPersonas() {
        List<Persona> list = new ArrayList<>();
        String sql = "SELECT * FROM persona ORDER BY id";

        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Liefert die naechste freie Persona-ID (max + 1).
     * Wenn die Tabelle leer ist -> 1.
     * Vermeidet die IDENTITY-Spruenge (1001, 2001 etc.) die der
     * SQL Server nach einem Neustart macht.
     */
    public int getNextId() {
        String sql = "SELECT ISNULL(MAX(id), 0) + 1 FROM persona";

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
     * Persona hinzufuegen (Story #12).
     * ID wird manuell vergeben (max + 1) - kein IDENTITY noetig.
     */
    public boolean insertPersona(Persona p) {
        int newId = getNextId();
        p.setId(newId);

        String sql = "INSERT INTO persona (id, username, password, role, can_delete) VALUES (?,?,?,?,?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, p.getId());
            ps.setString(2, p.getUsername());
            ps.setString(3, p.getPassword());
            ps.setString(4, p.getRole());
            ps.setBoolean(5, p.isCanDelete());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Persona aktualisieren (Story #13). */
    public boolean updatePersona(Persona p) {
        String sql = "UPDATE persona SET password=?, role=?, can_delete=? WHERE id=?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getPassword());
            ps.setString(2, p.getRole());
            ps.setBoolean(3, p.isCanDelete());
            ps.setInt(4, p.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Persona loeschen. */
    public boolean deletePersona(int id) {
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement p1 = c.prepareStatement("DELETE FROM persona_hotels WHERE persona_id = ?");
                 PreparedStatement p2 = c.prepareStatement("DELETE FROM persona WHERE id = ?")) {

                p1.setInt(1, id); p1.executeUpdate();
                p2.setInt(1, id); p2.executeUpdate();
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

    /** Welche Hotel-IDs gehoeren einer Hotel-Rep-Persona? */
    public List<Integer> getHotelIdsForPersona(int personaId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT hotel_id FROM persona_hotels WHERE persona_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Persona map(ResultSet rs) throws SQLException {
        Persona p = new Persona();
        p.setId(rs.getInt("id"));
        p.setUsername(rs.getString("username"));
        p.setPassword(rs.getString("password"));
        p.setRole(rs.getString("role"));
        p.setCanDelete(rs.getBoolean("can_delete"));
        return p;
    }
}
