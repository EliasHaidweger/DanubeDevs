package ui;

import db.HotelDAO;
import db.OccupancyDAO;
import model.Hotel;
import model.Occupancy;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * CSV-Import fuer Belegungsdaten (US 16).
 * Nutzt Apache Commons CSV.
 *
 * Erwartetes Format (Semikolon-getrennt, UTF-8, erste Zeile = Header):
 *   hotel_id;rooms;usedrooms;beds;usedbeds;year;month
 *
 * Validierungen je Zeile:
 *   - Hotel-ID muss in der DB existieren
 *   - alle Felder muessen gueltige Zahlen sein
 *   - usedrooms <= rooms, usedbeds <= beds
 *   - Year zwischen 2000-2030, Month zwischen 1-12
 */
public class ImportPanel extends JPanel {

    private final OccupancyDAO occupancyDAO = new OccupancyDAO();
    private final HotelDAO     hotelDAO     = new HotelDAO();
    private JTextArea taLog = new JTextArea();

    public ImportPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextArea info = new JTextArea(
                "CSV Import for Occupancy Data (US 16)\n\n"
              + "Expected file format (semicolon-separated, UTF-8, first line = header):\n"
              + "hotel_id;rooms;usedrooms;beds;usedbeds;year;month\n"
              + "1;152;81;300;123;2026;1\n"
              + "2;85;44;170;69;2026;1\n\n"
              + "Notes:\n"
              + "- Hotel ID must exist in the database\n"
              + "- usedrooms <= rooms, usedbeds <= beds\n"
              + "- If a record for the same hotel/year/month exists, it will be UPDATED\n"
              + "- Tip: Excel can save files as CSV via File > Save As > CSV (semicolon)"
        );
        info.setEditable(false);
        info.setBackground(getBackground());
        info.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(info, BorderLayout.NORTH);

        taLog.setEditable(false);
        taLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(taLog), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnImport = new JButton("Import CSV File...");
        btnImport.addActionListener(e -> onImport());
        buttons.add(btnImport);
        add(buttons, BorderLayout.SOUTH);
    }

    private void onImport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "CSV files (*.csv)", "csv"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        taLog.setText("Importing: " + file.getAbsolutePath() + "\n");
        taLog.append("----------------------------------------\n\n");

        // Alle Hotel-IDs einmal vorab laden (fuer Validierung)
        Set<Integer> validHotelIds = new HashSet<>();
        for (Hotel h : hotelDAO.findAll()) {
            validHotelIds.add(h.getId());
        }

        if (validHotelIds.isEmpty()) {
            taLog.append("ERROR: No hotels in database. Add hotels before importing.\n");
            return;
        }

        int success = 0;
        int errors  = 0;

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setIgnoreSurroundingSpaces(true)
                .setTrim(true)
                .build();

        // UTF-8 statt FileReader (fuer Umlaute)
        try (Reader reader = new InputStreamReader(
                     new FileInputStream(file), StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader, format)) {

            for (CSVRecord record : parser) {
                long lineNum = record.getRecordNumber() + 1;
                try {
                    Occupancy o = parseRecord(record);

                    // ====== Validierungen ======
                    if (!validHotelIds.contains(o.getHotelId())) {
                        throw new RuntimeException("Hotel ID " + o.getHotelId()
                                + " does not exist in database");
                    }
                    if (o.getMonth() < 1 || o.getMonth() > 12) {
                        throw new RuntimeException("Month must be 1-12, was "
                                + o.getMonth());
                    }
                    if (o.getYear() < 2000 || o.getYear() > 2030) {
                        throw new RuntimeException("Year must be 2000-2030, was "
                                + o.getYear());
                    }
                    if (o.getUsedRooms() > o.getRooms()) {
                        throw new RuntimeException("usedrooms (" + o.getUsedRooms()
                                + ") > rooms (" + o.getRooms() + ")");
                    }
                    if (o.getUsedBeds() > o.getBeds()) {
                        throw new RuntimeException("usedbeds (" + o.getUsedBeds()
                                + ") > beds (" + o.getBeds() + ")");
                    }
                    if (o.getRooms() < 0 || o.getBeds() < 0
                        || o.getUsedRooms() < 0 || o.getUsedBeds() < 0) {
                        throw new RuntimeException("negative values not allowed");
                    }

                    // ====== Speichern ======
                    occupancyDAO.save(o);
                    success++;
                    taLog.append("Line " + lineNum + ": OK  -> Hotel "
                            + o.getHotelId() + ", " + o.getYear() + "-"
                            + String.format("%02d", o.getMonth())
                            + " (used " + o.getUsedRooms() + "/" + o.getRooms()
                            + " rooms)\n");
                } catch (Exception ex) {
                    errors++;
                    taLog.append("Line " + lineNum + ": ERROR - " + ex.getMessage() + "\n");
                }
            }
        } catch (Exception ex) {
            taLog.append("\nFile error: " + ex.getMessage() + "\n");
            return;
        }

        taLog.append("\n----------------------------------------\n");
        taLog.append("Done. Success: " + success + ", Errors: " + errors + "\n");

        if (success > 0) {
            JOptionPane.showMessageDialog(this,
                    "Import done.\nSuccess: " + success + "\nErrors: " + errors,
                    "Import result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Wandelt einen CSV-Record in ein Occupancy-Objekt um.
     * Wirft RuntimeException wenn ein Feld fehlt oder keine Zahl ist.
     */
    private Occupancy parseRecord(CSVRecord record) {
        Occupancy o = new Occupancy();
        o.setHotelId(parseIntField(record, "hotel_id"));
        o.setRooms(parseIntField(record, "rooms"));
        o.setUsedRooms(parseIntField(record, "usedrooms"));
        o.setBeds(parseIntField(record, "beds"));
        o.setUsedBeds(parseIntField(record, "usedbeds"));
        o.setYear(parseIntField(record, "year"));
        o.setMonth(parseIntField(record, "month"));
        return o;
    }

    private int parseIntField(CSVRecord record, String field) {
        if (!record.isMapped(field)) {
            throw new RuntimeException("missing column: " + field);
        }
        String value = record.get(field);
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException("empty value for " + field);
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("'" + value + "' is not a number (" + field + ")");
        }
    }
}
