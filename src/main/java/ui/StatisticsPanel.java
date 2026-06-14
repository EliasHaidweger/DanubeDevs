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
 * Occupancy statistics for Senior User and Head of NOE-TO.
 *
 * Offers two views:
 *   US 2  - “By month/year”: Availability of all hotels for a month
 *   US 10 - “By hotel”: Room occupancy for a hotel during a specific period (from/to)
 */
public class StatisticsPanel extends JPanel {

    private final HotelDAO     hotelDAO     = new HotelDAO();
    private final OccupancyDAO occupancyDAO = new OccupancyDAO();

    private JComboBox<String>  cbView;

    // Filter for US 2 ("By month/year")
    private JComboBox<String>  cbMonth;
    private JComboBox<Integer> cbYear;

    //Filter 10

    // Panels that are shown or hidden depending on the view
    private JPanel monthFilterPanel;
    private JPanel hotelFilterPanel;

    private DefaultTableModel model;

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

        // Auto-Refresh: every time this tab becomes visible, the hotel list is being reloaded
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshHotels();
            }
        });
    }

    private JPanel buildFilters() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createTitledBorder("Filter"));

        // ====== Row 1: View-Option ======
        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbView = new JComboBox<>(new String[]{"By month/year", "By hotel"});
        cbView.addActionListener(e -> updateFilterVisibility());
        viewPanel.add(new JLabel("View:"));
        viewPanel.add(cbView);
        main.add(viewPanel);

        // ====== Row 2: Filter for US 2 ======
        monthFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbMonth = new JComboBox<>(MONTHS);
        cbYear  = makeYearCombo();
        monthFilterPanel.add(new JLabel("Month:")); monthFilterPanel.add(cbMonth);
        monthFilterPanel.add(new JLabel("Year:"));  monthFilterPanel.add(cbYear);
        main.add(monthFilterPanel);

        //row 3:

        // ====== Row 4: Show ======
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnShow = new JButton("Show");
        btnShow.addActionListener(e -> loadData());
        buttonPanel.add(btnShow);
        main.add(buttonPanel);

        // Default: only shows "By month/year"
        updateFilterVisibility();

        return main;
    }

    /** Year combo with fixed years 2000–2026. */
    private JComboBox<Integer> makeYearCombo() {
        Integer[] years = new Integer[27];        // 2000 - 2026
        for (int i = 0; i < years.length; i++) years[i] = 2000 + i;
        JComboBox<Integer> cb = new JComboBox<>(years);
        cb.setSelectedItem(2026);
        return cb;
    }

    /**
     * Shows only the filters that match the selected view.
     */
    private void updateFilterVisibility() {
        String view = (String) cbView.getSelectedItem();
        boolean byHotel = "By hotel".equals(view);
        monthFilterPanel.setVisible(!byHotel);
        hotelFilterPanel.setVisible(byHotel);
        revalidate();
        repaint();
    }

    /** Reload the hotel list (for auto-refresh). */
    private void refreshHotels() {
        Hotel previouslySelected = (Hotel) cbHotel.getSelectedItem();

        cbHotel.removeAllItems();
        for (Hotel h : hotelDAO.findAll()) cbHotel.addItem(h);

        // Restore previous selection if available
        if (previouslySelected != null) {
            for (int i = 0; i < cbHotel.getItemCount(); i++) {
                if (cbHotel.getItemAt(i).getId() == previouslySelected.getId()) {
                    cbHotel.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private JScrollPane buildTable() {
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(model);
        t.setRowHeight(22);
        return new JScrollPane(t);
    }

    // load Data

}
