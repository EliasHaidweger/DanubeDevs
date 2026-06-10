package ui;

import db.HotelDAO;
import model.Hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Master Data Summary (Story #1).
 */
public class DashboardPanel extends JPanel {

    private final HotelDAO dao = new HotelDAO();
    private DefaultTableModel model;

    private JLabel lblTotalHotels = new JLabel();
    private JLabel lblTotalRooms  = new JLabel();
    private JLabel lblTotalBeds   = new JLabel();

    private final String[] columns = {
            "Category", "Hotels", "Total Rooms", "Total Beds",
            "Avg. Rooms", "Avg. Beds"
    };

    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(buildSummary(), BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);

        loadData();

        // Auto-Refresh: jedes Mal wenn dieser Tab sichtbar wird neu laden
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadData();
            }
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
        p.add(makeBox("Total Rooms",  lblTotalRooms));
        p.add(makeBox("Total Beds",   lblTotalBeds));
        return p;
    }

    private JPanel makeBox(String label, JLabel value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    private JScrollPane buildTable() {
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(model);
        t.setRowHeight(24);
        return new JScrollPane(t);
    }

    public void loadData() {
        model.setRowCount(0);

        List<Hotel> hotels = dao.getAllHotels();
        Map<String, int[]> stats = new HashMap<>();

        int totalH = 0, totalR = 0, totalB = 0;

        for (Hotel h : hotels) {
            String cat = (h.getCategory() != null) ? h.getCategory() : "-";
            stats.putIfAbsent(cat, new int[3]);
            int[] s = stats.get(cat);
            s[0]++;
            s[1] += h.getNoRooms();
            s[2] += h.getNoBeds();
            totalH++;
            totalR += h.getNoRooms();
            totalB += h.getNoBeds();
        }

        for (Map.Entry<String, int[]> e : stats.entrySet()) {
            int[] s = e.getValue();
            int avgR = (s[0] > 0) ? s[1] / s[0] : 0;
            int avgB = (s[0] > 0) ? s[2] / s[0] : 0;
            model.addRow(new Object[]{e.getKey(), s[0], s[1], s[2], avgR, avgB});
        }

        lblTotalHotels.setText(String.valueOf(totalH));
        lblTotalRooms.setText(String.valueOf(totalR));
        lblTotalBeds.setText(String.valueOf(totalB));
    }
}
