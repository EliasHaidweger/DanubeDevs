package ui;

import db.HotelDAO;
import db.OccupancyDAO;
import model.Hotel;
import model.Occupancy;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Belegung eintragen (Story #6).
 */
public class OccupancyPanel extends JPanel {

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

    public OccupancyPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        loadHotels();
    }

    /** Toolbar oben mit Refresh-Button. */
    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh hotels");
        btnRefresh.addActionListener(e -> loadHotels());
        p.add(btnRefresh);
        return p;
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setBorder(BorderFactory.createTitledBorder("Enter monthly occupancy"));

        cbHotel = new JComboBox<>();
        cbHotel.addActionListener(e -> updateHotelInfo());

        tfHotelId.setEditable(false);
        tfHotelId.setBackground(new Color(240, 240, 240));

        String[] months = {"January","February","March","April","May","June",
                "July","August","September","October","November","December"};
        cbMonth = new JComboBox<>(months);

        Integer[] years = new Integer[27];               // 2000 - 2026
        for (int i = 0; i < years.length; i++) years[i] = 2000 + i;
        cbYear = new JComboBox<>(years);
        cbYear.setSelectedItem(2026);

        p.add(new JLabel("Hotel:"));            p.add(cbHotel);
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
        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> onSave());
        p.add(btnSave);
        return p;
    }

    /** Laed alle Hotels aus der DB ins Dropdown. */
    private void loadHotels() {
        cbHotel.removeAllItems();
        List<Hotel> hotels = hotelDAO.getAllHotels();
        for (Hotel h : hotels) cbHotel.addItem(h);
        updateHotelInfo();
    }

    private void updateHotelInfo() {
        Hotel h = (Hotel) cbHotel.getSelectedItem();
        if (h != null) {
            tfHotelId.setText(String.valueOf(h.getId()));
            tfRooms.setText(String.valueOf(h.getNoRooms()));
            tfBeds.setText(String.valueOf(h.getNoBeds()));
        } else {
            tfHotelId.setText("");
            tfRooms.setText("");
            tfBeds.setText("");
        }
    }

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

            if (occupancyDAO.saveOccupancy(o)) {
                JOptionPane.showMessageDialog(this, "Occupancy saved!");
                tfUsedRooms.setText("");
                tfUsedBeds.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Save failed.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "All number fields must be numbers.");
        }
    }
}
