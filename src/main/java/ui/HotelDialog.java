package ui;

import db.HotelDAO;
import model.Hotel;

import javax.swing.*;
import java.awt.*;

/**
 * Modal zum Hinzufuegen oder Bearbeiten eines Hotels (Story #3, #5).
 */
public class HotelDialog extends JDialog {

    private final HotelDAO dao = new HotelDAO();
    private final Hotel hotel;
    private boolean saved = false;

    private JTextField tfId        = new JTextField();
    private JComboBox<String> cbCategory = new JComboBox<>(new String[]{"*****","****","***","**","*"});
    private JTextField tfName      = new JTextField();
    private JTextField tfOwner     = new JTextField();
    private JTextField tfContact   = new JTextField();
    private JTextField tfAddress   = new JTextField();
    private JTextField tfCity      = new JTextField();
    private JTextField tfCityCode  = new JTextField();
    private JTextField tfPhone     = new JTextField();
    private JTextField tfRooms     = new JTextField();
    private JTextField tfBeds      = new JTextField();
    private JTextField tfTags      = new JTextField();

    public HotelDialog(JFrame parent, Hotel hotel) {
        super(parent, true);
        this.hotel = hotel;

        Image logo = LogoHelper.getLogoImage();
        if (logo != null) setIconImage(logo);

        setTitle(hotel == null ? "Add new Hotel" : "Edit Hotel");
        setSize(500, 520);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        // ID-Feld immer gesperrt
        tfId.setEditable(false);
        tfId.setBackground(new Color(240, 240, 240));

        if (hotel != null) {
            // Edit-Modus -> bestehende ID anzeigen
            fillFields(hotel);
        } else {
            // Add-Modus -> Vorschau der naechsten ID anzeigen
            int nextId = dao.getNextId();
            tfId.setText(String.valueOf(nextId));
            // Standard-Werte fuer Rooms/Beds (sonst NumberFormatException)
            tfRooms.setText("0");
            tfBeds.setText("0");
        }
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        p.add(new JLabel("Hotel ID (auto):"));   p.add(tfId);
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
        // Save zuerst (links), Cancel daneben (rechts)
        p.add(btnSave);
        p.add(btnCancel);
        return p;
    }

    private void fillFields(Hotel h) {
        tfId.setText(String.valueOf(h.getId()));
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
            // statt refresh button soll bei der action selbst die liste refreshed werden
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

            boolean ok = (hotel == null) ? dao.insertHotel(h) : dao.updateHotel(h);
            if (ok) {
                saved = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Saving failed - check console for details.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number in Rooms/Beds.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private int parseInt(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        return Integer.parseInt(s.trim());
    }

    public boolean wasSaved() { return saved; }
}
