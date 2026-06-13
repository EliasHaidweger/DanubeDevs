package ui;

import model.Hotel;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog box for creating (US 3) and editing (US 5) a hotel.
 * The ID is automatically assigned when you save.
 */

public class HotelDialog extends JDialog {

    private final HotelDAO hotelDAO = new HotelDAO();
    private final Hotel hotel;        // null = neues Hotel, sonst Bearbeiten
    private boolean saved = false;

    private final JComboBox<String> cbCategory =
            new JComboBox<>(new String[]{"*****", "****", "***", "**", "*"});
    private final JTextField tfName     = new JTextField();
    private final JTextField tfOwner    = new JTextField();
    private final JTextField tfContact  = new JTextField();
    private final JTextField tfAddress  = new JTextField();
    private final JTextField tfCity     = new JTextField();
    private final JTextField tfCityCode = new JTextField();
    private final JTextField tfPhone    = new JTextField();
    private final JTextField tfRooms    = new JTextField("0");
    private final JTextField tfBeds     = new JTextField("0");
    private final JTextField tfTags     = new JTextField();

    public HotelDialog(JFrame parent, Hotel hotel) {
        super(parent, true);
        this.hotel = hotel;

        Image logo = LogoHelper.getLogoImage();
        if (logo != null) setIconImage(logo);

        setTitle(hotel == null ? "Add new Hotel" : "Edit Hotel");
        setSize(500, 480);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        if (hotel != null) fillFields(hotel);
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        p.add(new JLabel("Category:"));          p.add(cbCategory);
        p.add(new JLabel("Hotel Name:"));        p.add(tfName);
        p.add(new JLabel("Owner:"));             p.add(tfOwner);
        p.add(new JLabel("Contact:"));           p.add(tfContact);
        p.add(new JLabel("Address:"));           p.add(tfAddress);
        p.add(new JLabel("City:"));              p.add(tfCity);
        p.add(new JLabel("City Code:"));         p.add(tfCityCode);
        p.add(new JLabel("Phone:"));             p.add(tfPhone);
        p.add(new JLabel("Number of Rooms:"));   p.add(tfRooms);
        p.add(new JLabel("Number of Beds:"));    p.add(tfBeds);
        p.add(new JLabel("Tags (comma sep.):")); p.add(tfTags);
        return p;
    }

    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave   = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());
        p.add(btnSave);
        p.add(btnCancel);
        return p;
    }

    private void fillFields(Hotel h) {
        cbCategory.setSelectedItem(h.getCategory());
        tfName.setText(h.getName());
        tfOwner.setText(h.getOwner());
        tfContact.setText(h.getContact());
        tfAddress.setText(h.getAddress());
        tfCity.setText(h.getCity());
        tfCityCode.setText(h.getCityCode());
        tfPhone.setText(h.getPhone());
        tfRooms.setText(String.valueOf(h.getNoRooms()));
        tfBeds.setText(String.valueOf(h.getNoBeds()));
        tfTags.setText(h.getTags());
    }

    private void onSave() {
        if (tfName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hotel name is required.");
            return;
        }

        try {
            Hotel h = (hotel == null) ? new Hotel() : hotel;
            h.setCategory((String) cbCategory.getSelectedItem());
            h.setName(tfName.getText().trim());
            h.setOwner(tfOwner.getText().trim());
            h.setContact(tfContact.getText().trim());
            h.setAddress(tfAddress.getText().trim());
            h.setCity(tfCity.getText().trim());
            h.setCityCode(tfCityCode.getText().trim());
            h.setPhone(tfPhone.getText().trim());
            h.setNoRooms(parseInt(tfRooms.getText()));
            h.setNoBeds(parseInt(tfBeds.getText()));
            h.setTags(tfTags.getText().trim());

            if (hotel == null) hotelDAO.save(h);
            else               hotelDAO.update(h);

            saved = true;
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Rooms and beds must be numbers.");
        }
    }

    private int parseInt(String text) {
        String t = text.trim();
        return t.isEmpty() ? 0 : Integer.parseInt(t);
    }

    public boolean wasSaved() {
        return saved;
    }
}
