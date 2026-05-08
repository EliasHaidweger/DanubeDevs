package trial;

import java.sql.*;

public class DatabaseDiagnostics {

        private static final String URL =
                "jdbc:sqlserver://185.119.119.126:1433;databaseName=SP_DEUTSCH;encrypt=true;trustServerCertificate=true";
        private static final String USER = "johndoe";
        private static final String PASSWORD = "johndoe";

        public static void main(String[] args) {
            pruefeVerbindung();
            trennlinie();

            pruefeGrunddaten();
            trennlinie();

            pruefeSchemas();
            trennlinie();

            pruefeTabellen();
            trennlinie();

            pruefeViews();
            trennlinie();

            pruefeRollen();
            trennlinie();

            pruefeBerechtigungen();
            trennlinie();
        }

        public static void pruefeVerbindung() {
            System.out.println("VERBINDUNGSPRUEFUNG");

            try (Connection con = getConnection()) {
                if (con != null && !con.isClosed()) {
                    System.out.println("Verbindung zur Datenbank erfolgreich hergestellt.");
                } else {
                    System.out.println("Verbindung konnte nicht hergestellt werden.");
                }
            } catch (SQLException e) {
                gibSqlFehlerAus(e);
            }
        }

        public static void pruefeGrunddaten() {
            System.out.println("GRUNDDATEN");

            try (Connection con = getConnection()) {
                DatabaseMetaData meta = con.getMetaData();

                System.out.println("Datenbankprodukt : " + meta.getDatabaseProductName());
                System.out.println("Version          : " + meta.getDatabaseProductVersion());
                System.out.println("Treibername      : " + meta.getDriverName());
                System.out.println("Treiberversion   : " + meta.getDriverVersion());
                System.out.println("URL              : " + meta.getURL());
                System.out.println("Benutzer         : " + meta.getUserName());
                System.out.println("Katalog          : " + con.getCatalog());
                System.out.println("AutoCommit       : " + con.getAutoCommit());
                System.out.println("ReadOnly         : " + con.isReadOnly());
                System.out.println("TransactionLevel : " + con.getTransactionIsolation());
            } catch (SQLException e) {
                gibSqlFehlerAus(e);
            }
        }

        public static void pruefeSchemas() {
            System.out.println("SCHEMAS");

            try (Connection con = getConnection();
                 ResultSet rs = con.getMetaData().getSchemas()) {

                boolean gefunden = false;

                while (rs.next()) {
                    System.out.println("Schema : " + rs.getString("TABLE_SCHEM")
                            + " | Katalog: " + rs.getString("TABLE_CATALOG"));
                    gefunden = true;
                }

                if (!gefunden) {
                    System.out.println("Keine Schemas sichtbar.");
                }

            } catch (SQLException e) {
                gibSqlFehlerAus(e);
            }
        }

        public static void pruefeTabellen() {
            System.out.println("TABELLEN UND SPALTEN");

            try (Connection con = getConnection();
                 ResultSet rs = con.getMetaData().getTables(con.getCatalog(), null, "%", new String[]{"TABLE"})) {

                boolean gefunden = false;

                while (rs.next()) {
                    String schema = rs.getString("TABLE_SCHEM");
                    String table = rs.getString("TABLE_NAME");
                    String type = rs.getString("TABLE_TYPE");

                    System.out.println("Tabelle : " + schema + "." + table + " | Typ: " + type);
                    pruefeSpalten(con, schema, table);
                    System.out.println();
                    gefunden = true;
                }

                if (!gefunden) {
                    System.out.println("Keine Tabellen sichtbar oder keine Berechtigung vorhanden.");
                }

            } catch (SQLException e) {
                gibSqlFehlerAus(e);
            }
        }

        public static void pruefeViews() {
            System.out.println("VIEWS");

            try (Connection con = getConnection();
                 ResultSet rs = con.getMetaData().getTables(con.getCatalog(), null, "%", new String[]{"VIEW"})) {

                boolean gefunden = false;

                while (rs.next()) {
                    System.out.println("View : " + rs.getString("TABLE_SCHEM") + "." + rs.getString("TABLE_NAME"));
                    gefunden = true;
                }

                if (!gefunden) {
                    System.out.println("Keine Views sichtbar.");
                }

            } catch (SQLException e) {
                gibSqlFehlerAus(e);
            }
        }

        public static void pruefeRollen() {
            System.out.println("ROLLEN");

            String sql =
                    "SELECT dp2.name AS role_name " +
                            "FROM sys.database_role_members drm " +
                            "JOIN sys.database_principals dp1 ON drm.member_principal_id = dp1.principal_id " +
                            "JOIN sys.database_principals dp2 ON drm.role_principal_id = dp2.principal_id " +
                            "WHERE dp1.name = USER_NAME()";

            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                boolean gefunden = false;

                while (rs.next()) {
                    System.out.println("Rolle : " + rs.getString("role_name"));
                    gefunden = true;
                }

                if (!gefunden) {
                    System.out.println("Keine Rollen gefunden oder keine Berechtigung zur Einsicht.");
                }

            } catch (SQLException e) {
                gibSqlFehlerAus(e);
            }
        }

        public static void pruefeBerechtigungen() {
            System.out.println("BERECHTIGUNGEN");

            String sql =
                    "SELECT permission_name, state_desc, class_desc " +
                            "FROM fn_my_permissions(NULL, 'DATABASE')";

            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                boolean gefunden = false;

                while (rs.next()) {
                    System.out.println("Recht : " + rs.getString("permission_name")
                            + " | Status: " + rs.getString("state_desc")
                            + " | Bereich: " + rs.getString("class_desc"));
                    gefunden = true;
                }

                if (!gefunden) {
                    System.out.println("Keine Berechtigungen gefunden oder Abfrage nicht erlaubt.");
                }

            } catch (SQLException e) {
                gibSqlFehlerAus(e);
            }
        }

        private static void pruefeSpalten(Connection con, String schema, String table) {
            try (ResultSet rs = con.getMetaData().getColumns(con.getCatalog(), schema, table, "%")) {

                boolean gefunden = false;

                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String typeName = rs.getString("TYPE_NAME");
                    int columnSize = rs.getInt("COLUMN_SIZE");
                    String nullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable ? "JA" : "NEIN";

                    System.out.println("   -> Spalte: " + columnName
                            + " | Typ: " + typeName
                            + " | Groesse: " + columnSize
                            + " | Nullable: " + nullable);
                    gefunden = true;
                }

                if (!gefunden) {
                    System.out.println("   -> Keine Spalten sichtbar.");
                }

            } catch (SQLException e) {
                System.out.println("   -> Fehler beim Lesen der Spalten von " + schema + "." + table);
                gibSqlFehlerAus(e);
            }
        }

        private static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }

        private static void gibSqlFehlerAus(SQLException e) {
            System.out.println("SQL-FEHLER:");

            SQLException current = e;
            while (current != null) {
                System.out.println("Nachricht  : " + current.getMessage());
                System.out.println("SQL State  : " + current.getSQLState());
                System.out.println("Fehlercode : " + current.getErrorCode());
                System.out.println();
                current = current.getNextException();
            }
        }

        private static void trennlinie() {
            System.out.println("\n============================================================\n");
        }
    }
