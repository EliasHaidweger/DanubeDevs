package ui;

import db.HotelDAO;
import db.OccupancyDAO;
import model.Hotel;
import model.Occupancy;
import model.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Hotel Rep gibt Belegung fuer eigene Hotels ein (Stories #26, #27).
 */
public class MySubmissionPanel extends JPanel {

    private final HotelDAO     hotelDAO     = new HotelDAO();
    private final OccupancyDAO occupancyDAO = new OccupancyDAO();

    private JComboBox<Hotel>   cbHotel;
    private JTextField         tfHotelId   = new JTextField();
    private JComboBox<String>  cbMonth;
    private JComboBox<Integer> cbYear;
    private JTextField         tfRooms     = new JTextField();
    private JTextField         tfUsedRooms = new JTextField();
    private JTextField         tfBeds      = new JTextField();
    private JTextField         tfUsedBeds  = new JTextField();

    private DefaultTableModel  pastModel;

    private final String[] pastColumns = {
            "Hotel", "Year", "Month", "Rooms", "Used", "Beds", "Used", "Room %", "Bed %"
    };

    public MySubmissionPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new BorderLayout());
        top.add(buildForm(), BorderLayout.CENTER);
        top.add(buildButtons(), BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        add(buildPastTable(), BorderLayout.CENTER);

        loadMyHotels();
        loadPast();

        // Auto-Refresh: jedes Mal wenn dieser Tab sichtbar wird neu laden
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadMyHotels();
                loadPast();
            }
        });
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setBorder(BorderFactory.createTitledBorder("Submit monthly occupancy"));

        cbHotel = new JComboBox<>();
        cbHotel.addActionListener(e -> updateInfo());

        tfHotelId.setEditable(false);
        tfHotelId.setBackground(new Color(240, 240, 240));

        String[] months = {"January","February","March","April","May","June",
                "July","August","September","October","November","December"};
        cbMonth = new JComboBox<>(months);

        Integer[] years = new Integer[27];               // 2000 - 2026
        for (int i = 0; i < years.length; i++) years[i] = 2000 + i;
        cbYear = new JComboBox<>(years);
        cbYear.setSelectedItem(2026);

        p.add(new JLabel("My Hotel:"));         p.add(cbHotel);
        p.add(new JLabel("Hotel ID (auto):"));  p.add(tfHotelId);
        p.add(new JLabel("Month:"));            p.add(cbMonth);
        p.add(new JLabel("Year:"));             p.add(cbYear);
        p.add(new JLabel("Total rooms:"));      p.add(tfRooms);
        p.add(new JLabel("Used rooms:"));       p.add(tfUsedRooms);
        p.add(new JLabel("Total beds:"));       p.add(tfBeds);
        p.add(new JLabel("Used beds:"));        p.add(tfUsedBeds);
        return p;
    }

    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Submit");
        btnSave.addActionListener(e -> onSave());
        p.add(btnSave);
        return p;
    }

    private JScrollPane buildPastTable() {
        pastModel = new DefaultTableModel(pastColumns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(pastModel);
        t.setRowHeight(22);
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createTitledBorder("My past submissions"));
        return sp;
    }

    private void loadMyHotels() {
        cbHotel.removeAllItems();
        for (Integer id : Session.getMyHotelIds()) {
            Hotel h = hotelDAO.getHotelById(id);
            if (h != null) cbHotel.addItem(h);
        }
        updateInfo();
    }

    private void updateInfo() {
        Hotel h = (Hotel) cbHotel.getSelectedItem();
        if (h != null) {
            tfHotelId.setText(String.valueOf(h.getId()));
            tfRooms.setText(String.valueOf(h.getNoRooms()));
            tfBeds.setText(String.valueOf(h.getNoBeds()));
        }
    }

    private void loadPast() {
        pastModel.setRowCount(0);
        for (Integer id : Session.getMyHotelIds()) {
            Hotel h = hotelDAO.getHotelById(id);
            String name = (h != null) ? h.getName() : ("ID " + id);
            for (Occupancy o : occupancyDAO.getOccupanciesByHotel(id)) {
                pastModel.addRow(new Object[]{
                        name, o.getYear(), o.getMonth(),
                        o.getRooms(), o.getUsedRooms(),
                        o.getBeds(), o.getUsedBeds(),
                        String.format("%.1f", o.getRoomOccupancyPercent()),
                        String.format("%.1f", o.getBedOccupancyPercent())
                });
            }
        }
    }

    private void onSave() {
        Hotel h = (Hotel) cbHotel.getSelectedItem();
        if (h == null) {
            JOptionPane.showMessageDialog(this, "Please select a hotel.");
            return;
        }
        if (!Session.getMyHotelIds().contains(h.getId())) {
            JOptionPane.showMessageDialog(this, "Permission denied.");
            return;
        }

        try {
            Occupancy o = new Occupancy();
            o.setHotelId(h.getId());
            o.setMonth(cbMonth.getSelectedIndex() + 1);
            o.setYear((Integer) cbYear.getSelectedItem());
            o.setRooms(Integer.parseInt(tfRooms.getText().trim()));
            o.setUsedRooms(Integer.parseInt(tfUsedRooms.getText().trim()));
            o.setBeds(Integer.parseInt(tfBeds.getText().trim()));
            o.setUsedBeds(Integer.parseInt(tfUsedBeds.getText().trim()));

            if (o.getUsedRooms() > o.getRooms()) {
                JOptionPane.showMessageDialog(this, "Used rooms cannot exceed total rooms.");
                return;
            }
            if (o.getUsedBeds() > o.getBeds()) {
                JOptionPane.showMessageDialog(this, "Used beds cannot exceed total beds.");
                return;
            }

            if (occupancyDAO.saveOccupancy(o)) {
                JOptionPane.showMessageDialog(this, "Submitted successfully!");
                tfUsedRooms.setText("");
                tfUsedBeds.setText("");
                loadPast();
            } else {
                JOptionPane.showMessageDialog(this, "Save failed.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Numbers expected for occupancy fields.");
        }
    }
}
