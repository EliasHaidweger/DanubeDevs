package ui;

import db.HotelDAO;
import model.Hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Master Data Overview for Senior Users and Head of NOE-TO (US 1).
 *
 * Displays the total figures (hotels, rooms, beds) as well as a breakdown by category.
 */
public class DashboardPanel extends JPanel {

    private static final String[] COLUMNS =
            {"Category", "Hotels", "Total Rooms", "Total Beds", "Avg. Rooms", "Avg. Beds"};

    private final HotelDAO hotelDAO = new HotelDAO();
    private final DefaultTableModel model;

    private final JLabel lblTotalHotels = new JLabel();
    private final JLabel lblTotalRooms  = new JLabel();
    private final JLabel lblTotalBeds   = new JLabel();

    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(24);

        add(buildSummary(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();

        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) { loadData(); }
        });
    }

    private JPanel buildSummary() {
        JPanel p = new JPanel(new GridLayout(1, 3, 10, 10));
        p.setBorder(BorderFactory.createTitledBorder("Overview"));

        Font big = new Font("SansSerif", Font.BOLD, 18);
        lblTotalHotels.setFont(big);
        lblTotalRooms.setFont(big);
        lblTotalBeds.setFont(big);

        p.add(makeBox("Total Hotels", lblTotalHotels));
        p.add(makeBox("Total Rooms", lblTotalRooms));
        p.add(makeBox("Total Beds", lblTotalBeds));
        return p;
    }

    private JPanel makeBox(String label, JLabel valueLabel) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    /** US 1: Calculates the master data summary and populates the table and totals. */
    private void loadData() {
        Map<String, CategoryStats> byCategory = new LinkedHashMap<>();
        int totalHotels = 0, totalRooms = 0, totalBeds = 0;

        for (Hotel h : hotelDAO.findAll()) {
            String category = (h.getCategory() != null) ? h.getCategory() : "-";
            byCategory.computeIfAbsent(category, key -> new CategoryStats())
                      .add(h.getNoRooms(), h.getNoBeds());

            totalHotels++;
            totalRooms += h.getNoRooms();
            totalBeds  += h.getNoBeds();
        }

        for (Map.Entry<String, CategoryStats> entry : byCategory.entrySet()) {
            CategoryStats s = entry.getValue();
            model.addRow(new Object[]{
                    entry.getKey(), s.hotelCount, s.rooms, s.beds, s.averageRooms(), s.averageBeds()
            });
        }

        lblTotalHotels.setText(String.valueOf(totalHotels));
        lblTotalRooms.setText(String.valueOf(totalRooms));
        lblTotalBeds.setText(String.valueOf(totalBeds));
    }

    /** Sum the subtotals for a hotel category. */
    private static class CategoryStats {
        private int hotelCount;
        private int rooms;
        private int beds;

        void add(int roomCount, int bedCount) {
            hotelCount++;
            rooms += roomCount;
            beds  += bedCount;
        }

        int averageRooms() { return (hotelCount > 0) ? rooms / hotelCount : 0; }
        int averageBeds()  { return (hotelCount > 0) ? beds / hotelCount : 0; }
    }
}
