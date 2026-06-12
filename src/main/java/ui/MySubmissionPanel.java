package ui;

import db.HotelDAO;
import db.OccupancyDAO;
import model.Hotel;
import model.Occupancy;
import model.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Belegungserfassung fuer den Hotel Representative.
 *
 * Deckt folgende User Stories ab:
 *   US 26 - Eigene Belegung erfassen
 *   US 27 - Liste der eigenen, bereits erfassten Belegungen
 *
 * Erfassen und Bearbeiten sind nur fuer die eigenen, zugeordneten
 * Hotels (Session) moeglich.
 */
public class MySubmissionPanel extends JPanel {

    private static final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    private static final String[] PAST_COLUMNS = {
            "Hotel", "Year", "Month", "Rooms", "Used Rooms", "Beds", "Used Beds", "Room %", "Bed %"};

    private final HotelDAO     hotelDAO     = new HotelDAO();
    private final OccupancyDAO occupancyDAO = new OccupancyDAO();

    private final JComboBox<Hotel>   cbHotel    = new JComboBox<>();
    private final JTextField         tfHotelId  = new JTextField();
    private final JComboBox<String>  cbMonth    = new JComboBox<>(MONTHS);
    private final JComboBox<Integer> cbYear     = buildYearCombo();
    private final JTextField         tfRooms    = new JTextField();
    private final JTextField         tfUsedRooms = new JTextField();
    private final JTextField         tfBeds     = new JTextField();
    private final JTextField         tfUsedBeds = new JTextField();

    private final DefaultTableModel pastModel;

    public MySubmissionPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new BorderLayout());
        top.add(buildForm(), BorderLayout.CENTER);
        top.add(buildButtons(), BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        pastModel = new DefaultTableModel(PAST_COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable pastTable = new JTable(pastModel);
        pastTable.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(pastTable);
        scroll.setBorder(BorderFactory.createTitledBorder("My past submissions"));
        add(scroll, BorderLayout.CENTER);

        loadMyHotels();
        loadPast();

        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) {
                loadMyHotels();
                loadPast();
            }
        });
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setBorder(BorderFactory.createTitledBorder("Submit monthly occupancy"));

        cbHotel.addActionListener(e -> showSelectedHotelInfo());

        tfHotelId.setEditable(false);
        tfHotelId.setBackground(new Color(240, 240, 240));

        p.add(new JLabel("My Hotel:"));        p.add(cbHotel);
        p.add(new JLabel("Hotel ID (auto):")); p.add(tfHotelId);
        p.add(new JLabel("Month:"));           p.add(cbMonth);
        p.add(new JLabel("Year:"));            p.add(cbYear);
        p.add(new JLabel("Total rooms:"));     p.add(tfRooms);
        p.add(new JLabel("Used rooms:"));      p.add(tfUsedRooms);
        p.add(new JLabel("Total beds:"));      p.add(tfBeds);
        p.add(new JLabel("Used beds:"));       p.add(tfUsedBeds);
        return p;
    }

    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSubmit = new JButton("Submit");
        btnSubmit.addActionListener(e -> onSubmit());
        p.add(btnSubmit);
        return p;
    }

    /** US 26: Laedt die eigenen Hotels ins Dropdown. */
    private void loadMyHotels() {
        cbHotel.removeAllItems();
        for (Integer id : Session.getMyHotelIds()) {
            Hotel h = hotelDAO.findById(id);
            if (h != null) cbHotel.addItem(h);
        }
        showSelectedHotelInfo();
    }

    private void showSelectedHotelInfo() {
        Hotel h = (Hotel) cbHotel.getSelectedItem();
        if (h == null) return;
        tfHotelId.setText(String.valueOf(h.getId()));
        tfRooms.setText(String.valueOf(h.getNoRooms()));
        tfBeds.setText(String.valueOf(h.getNoBeds()));
    }

    /** US 27: Laedt alle bereits erfassten Belegungen der eigenen Hotels. */
    private void loadPast() {
        pastModel.setRowCount(0);
        for (Integer id : Session.getMyHotelIds()) {
            Hotel h = hotelDAO.findById(id);
            String name = (h != null) ? h.getName() : ("ID " + id);
            for (Occupancy o : occupancyDAO.findByHotel(id)) {
                pastModel.addRow(new Object[]{
                        name, o.getYear(), o.getMonth(),
                        o.getRooms(), o.getUsedRooms(), o.getBeds(), o.getUsedBeds(),
                        String.format("%.1f", o.getRoomOccupancyPercent()),
                        String.format("%.1f", o.getBedOccupancyPercent())
                });
            }
        }
    }

    /** US 26: Eigene Belegung erfassen. */
    private void onSubmit() {
        Hotel h = (Hotel) cbHotel.getSelectedItem();
        if (h == null) {
            JOptionPane.showMessageDialog(this, "Please select a hotel.");
            return;
        }
        if (!Session.getMyHotelIds().contains(h.getId())) {
            JOptionPane.showMessageDialog(this, "You can only submit data for your own hotels.");
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

            occupancyDAO.save(o);
            JOptionPane.showMessageDialog(this, "Occupancy submitted.");
            tfUsedRooms.setText("");
            tfUsedBeds.setText("");
            loadPast();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "All number fields must contain numbers.");
        }
    }

    private static JComboBox<Integer> buildYearCombo() {
        Integer[] years = new Integer[27];          // 2000 - 2026
        for (int i = 0; i < years.length; i++) years[i] = 2000 + i;
        JComboBox<Integer> cb = new JComboBox<>(years);
        cb.setSelectedItem(2026);
        return cb;
    }
}
