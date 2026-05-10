package ui;

import db.HotelDAO;
import db.OccupancyDAO;
import model.Hotel;
import model.Occupancy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Belegungsdaten-Statistik mit Filter.
 *
 * Stories:
 *   #2  "By month/year": alle Hotels fuer EINEN Monat
 *   #10 "By hotel":      alle Belegungen EINES Hotels mit FROM/TO Zeitraum
 */
public class StatisticsPanel extends JPanel {

    private final HotelDAO     hotelDAO     = new HotelDAO();
    private final OccupancyDAO occupancyDAO = new OccupancyDAO();

    private JComboBox<String>  cbView;

    // Filter fuer "By month/year" (Story #2)
    private JComboBox<String>  cbMonth;
    private JComboBox<Integer> cbYear;

    // Filter fuer "By hotel" mit FROM/TO (Story #10)
    private JComboBox<Hotel>   cbHotel;
    private JComboBox<String>  cbFromMonth;
    private JComboBox<Integer> cbFromYear;
    private JComboBox<String>  cbToMonth;
    private JComboBox<Integer> cbToYear;

    // Panels die je nach View ein-/ausgeblendet werden
    private JPanel monthFilterPanel;
    private JPanel hotelFilterPanel;

    private DefaultTableModel  model;

    private final String[] columns = {
            "Hotel ID", "Hotel Name", "Year", "Month",
            "Rooms", "Used Rooms", "Room %", "Beds", "Used Beds", "Bed %"
    };

    private static final String[] MONTHS = {
            "January","February","March","April","May","June",
            "July","August","September","October","November","December"
    };

    public StatisticsPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFilters(), BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
    }

    private JPanel buildFilters() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createTitledBorder("Filter"));

        // ====== Reihe 1: View-Auswahl ======
        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbView = new JComboBox<>(new String[]{"By month/year", "By hotel"});
        cbView.addActionListener(e -> updateFilterVisibility());
        viewPanel.add(new JLabel("View:"));
        viewPanel.add(cbView);
        main.add(viewPanel);

        // ====== Reihe 2: Filter fuer "By month/year" (Story #2) ======
        monthFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbMonth = new JComboBox<>(MONTHS);
        cbYear  = makeYearCombo();
        monthFilterPanel.add(new JLabel("Month:")); monthFilterPanel.add(cbMonth);
        monthFilterPanel.add(new JLabel("Year:"));  monthFilterPanel.add(cbYear);
        main.add(monthFilterPanel);

        // ====== Reihe 3: Filter fuer "By hotel" mit FROM/TO (Story #10) ======
        hotelFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cbHotel = new JComboBox<>();
        for (Hotel h : hotelDAO.getAllHotels()) cbHotel.addItem(h);

        cbFromMonth = new JComboBox<>(MONTHS);
        cbFromYear  = makeYearCombo();
        cbFromYear.setSelectedItem(2000);                 // Default: aeltestes Jahr
        cbToMonth   = new JComboBox<>(MONTHS);
        cbToMonth.setSelectedIndex(11);                   // Default: December
        cbToYear    = makeYearCombo();
        cbToYear.setSelectedItem(2026);                   // Default: neuestes Jahr

        hotelFilterPanel.add(new JLabel("Hotel:"));
        hotelFilterPanel.add(cbHotel);
        hotelFilterPanel.add(new JLabel("  From:"));
        hotelFilterPanel.add(cbFromMonth);
        hotelFilterPanel.add(cbFromYear);
        hotelFilterPanel.add(new JLabel("  To:"));
        hotelFilterPanel.add(cbToMonth);
        hotelFilterPanel.add(cbToYear);
        main.add(hotelFilterPanel);

        // ====== Reihe 4: Show + Refresh ======
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnShow = new JButton("Show");
        JButton btnRefresh = new JButton("Refresh hotels");
        btnShow.addActionListener(e -> loadData());
        btnRefresh.addActionListener(e -> refreshHotels());
        buttonPanel.add(btnShow);
        buttonPanel.add(btnRefresh);
        main.add(buttonPanel);

        // Default: nur "By month/year" anzeigen
        updateFilterVisibility();

        return main;
    }

    /** Year-Combo mit festen Jahren 2000 - 2026. */
    private JComboBox<Integer> makeYearCombo() {
        Integer[] years = new Integer[27];        // 2000 - 2026
        for (int i = 0; i < years.length; i++) years[i] = 2000 + i;
        JComboBox<Integer> cb = new JComboBox<>(years);
        cb.setSelectedItem(2026);
        return cb;
    }

    /**
     * Zeigt nur die zur gewaehlten View passenden Filter.
     */
    private void updateFilterVisibility() {
        String view = (String) cbView.getSelectedItem();
        boolean byHotel = "By hotel".equals(view);
        monthFilterPanel.setVisible(!byHotel);
        hotelFilterPanel.setVisible(byHotel);
        revalidate();
        repaint();
    }

    private void refreshHotels() {
        cbHotel.removeAllItems();
        for (Hotel h : hotelDAO.getAllHotels()) cbHotel.addItem(h);
    }

    private JScrollPane buildTable() {
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(model);
        t.setRowHeight(22);
        return new JScrollPane(t);
    }

    private void loadData() {
        model.setRowCount(0);

        Map<Integer, Hotel> hotelMap = new HashMap<>();
        for (Hotel h : hotelDAO.getAllHotels()) hotelMap.put(h.getId(), h);

        String view = (String) cbView.getSelectedItem();
        List<Occupancy> data;

        if ("By hotel".equals(view)) {
            // ====== Story #10 mit FROM/TO ======
            Hotel h = (Hotel) cbHotel.getSelectedItem();
            if (h == null) {
                JOptionPane.showMessageDialog(this, "Please select a hotel.");
                return;
            }

            int fromYear  = (Integer) cbFromYear.getSelectedItem();
            int fromMonth = cbFromMonth.getSelectedIndex() + 1;
            int toYear    = (Integer) cbToYear.getSelectedItem();
            int toMonth   = cbToMonth.getSelectedIndex() + 1;

            if ((fromYear * 12 + fromMonth) > (toYear * 12 + toMonth)) {
                JOptionPane.showMessageDialog(this,
                        "'From' must be earlier than or equal to 'To'.");
                return;
            }

            data = occupancyDAO.getOccupanciesByHotelInRange(
                    h.getId(), fromYear, fromMonth, toYear, toMonth);
        } else {
            // ====== Story #2 ======
            int month = cbMonth.getSelectedIndex() + 1;
            int year  = (Integer) cbYear.getSelectedItem();
            data = occupancyDAO.getOccupanciesByMonth(year, month);
        }

        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data found.");
            return;
        }

        for (Occupancy o : data) {
            Hotel h = hotelMap.get(o.getHotelId());
            String name = (h != null) ? h.getName() : "(unknown)";
            model.addRow(new Object[]{
                    o.getHotelId(), name, o.getYear(), o.getMonth(),
                    o.getRooms(), o.getUsedRooms(),
                    String.format("%.1f", o.getRoomOccupancyPercent()),
                    o.getBeds(), o.getUsedBeds(),
                    String.format("%.1f", o.getBedOccupancyPercent())
            });
        }
    }
}
