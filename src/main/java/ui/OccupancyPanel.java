package ui;

import db.HotelDAO;
import db.OccupancyDAO;
import model.Hotel;
import model.Occupancy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Room Reservation Entry for Senior Users (US 6).
 *
 * The hotel dropdown is automatically updated as soon as the tab is displayed and after each save.
 */
public class OccupancyPanel extends JPanel {

    private static final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

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

    public OccupancyPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        loadHotels();

        // Reload the hotel list as soon as the tab becomes visible
        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) { loadHotels(); }
        });
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setBorder(BorderFactory.createTitledBorder("Enter monthly occupancy"));

        cbHotel.addActionListener(e -> showSelectedHotelInfo());

        tfHotelId.setEditable(false);
        tfHotelId.setBackground(new Color(240, 240, 240));

        p.add(new JLabel("Hotel:"));           p.add(cbHotel);
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
        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> onSave());
        p.add(btnSave);
        return p;
    }

    /** Load all hotels into the dropdown menu and keep the current selection. */
    private void loadHotels() {
        Hotel previous = (Hotel) cbHotel.getSelectedItem();

        cbHotel.removeAllItems();
        for (Hotel h : hotelDAO.findAll()) cbHotel.addItem(h);

        if (previous != null) {
            for (int i = 0; i < cbHotel.getItemCount(); i++) {
                if (cbHotel.getItemAt(i).getId() == previous.getId()) {
                    cbHotel.setSelectedIndex(i);
                    break;
                }
            }
        }
        showSelectedHotelInfo();
    }

    /** Enter the ID, number of rooms, and number of beds for the selected hotel. */
    private void showSelectedHotelInfo() {
        Hotel h = (Hotel) cbHotel.getSelectedItem();
        if (h == null) {
            tfHotelId.setText("");
            tfRooms.setText("");
            tfBeds.setText("");
            return;
        }
        tfHotelId.setText(String.valueOf(h.getId()));
        tfRooms.setText(String.valueOf(h.getNoRooms()));
        tfBeds.setText(String.valueOf(h.getNoBeds()));
    }

    /** US 6: Save the reservation. */
    private void onSave() {
        Hotel h = (Hotel) cbHotel.getSelectedItem();
        if (h == null) {
            JOptionPane.showMessageDialog(this, "Please select a hotel.");
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
            JOptionPane.showMessageDialog(this, "Occupancy saved.");
            tfUsedRooms.setText("");
            tfUsedBeds.setText("");
            loadHotels();

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
