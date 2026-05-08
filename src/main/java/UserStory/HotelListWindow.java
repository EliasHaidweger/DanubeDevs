package UserStory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;


/**
 * Hotel list view for senior users of NOE-TO.
 * Shows all hotels with the required fields:
 * ID, name, address, number of rooms, number of beds, last reported transactional data.
 */
public class HotelListWindow extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public HotelListWindow() {
        defineFrame();
        initComponents();
        addComponents();
        loadData();
    }

    /**
     * Sets the basic window properties.
     */
    private void defineFrame() {
        setTitle("NOE-TO – Hotel List");
        setSize(950, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    /**
     * Creates the table and defines all required columns.
     */
    private void initComponents() {
        model = new DefaultTableModel();
        table = new JTable();
        table.setModel(model);

        model.addColumn("ID");
        model.addColumn("Hotel Name");
        model.addColumn("Address");
        model.addColumn("Rooms");
        model.addColumn("Beds");
        model.addColumn("Last Reported Data");
    }

    /**
     * Wraps the table in a scroll pane and places it in the center of the frame.
     */
    private void addComponents() {
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Loads hotels and occupancies from file, then fills the table row by row.
     * Displays an error dialog if a file cannot be found.
     */
    private void loadData() {
        try {
            ArrayList<Hotel> hotels = HotelUtility.loadHotelsFromFile();
            ArrayList<Occupancy> occupancies = OccupancyUtility.loadOccupanciesFromFile();

            hotels.sort((a, b) -> Integer.compare(a.getId(), b.getId()));
            // Build a quick lookup: hotel ID -> most recent occupancy record

            Map<Integer, Occupancy> lastReported = OccupancyUtility.getLastReportedByHotel(occupancies);

            for (Hotel hotel : hotels) {
                String fullAddress = hotel.getAddress() + ", " + hotel.getCityCode() + " " + hotel.getCity();

                // Format the most recent reporting period as YYYY-MM, or "–" if no record exists
                String lastData;
                if (lastReported.containsKey(hotel.getId())) {
                    Occupancy last = lastReported.get(hotel.getId());
                    lastData = last.getYear() + "-" + String.format("%02d", last.getMonth());
                } else {
                    lastData = "–";
                }

                model.addRow(new Object[]{
                        hotel.getId(),
                        hotel.getName(),
                        fullAddress,
                        hotel.getNoRooms(),
                        hotel.getNoBeds(),
                        lastData
                });
            }

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not load data: " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

