package ui;

import db.HotelDAO;
import model.Hotel;
import model.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Hotel overview for the hotel representative.
 *
 * Covers the following user stories:
 *   US 24 - List of your own assigned hotels
 *   US 25 - Edit your hotel information
 *
 * The assigned hotel IDs are stored in the session (loaded upon login).
 */
public class MyHotelsPanel extends JPanel {

    private final HotelDAO hotelDAO = new HotelDAO();
    private final DefaultTableModel model;
    private final JTable table;

    private static final String[] COLUMNS = {"ID", "Name", "City", "Category", "Rooms", "Beds"};

    public MyHotelsPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);

        add(buildToolbar(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();

        addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) { loadData(); }
        });
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnEdit = new JButton("Edit");
        btnEdit.addActionListener(e -> onEdit());
        p.add(btnEdit);
        return p;
    }



    /** US 25: Edit your own hotel (only allowed for assigned hotels). */
    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a hotel first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);

        if (!Session.getMyHotelIds().contains(id)) {
            JOptionPane.showMessageDialog(this, "You can only edit your own hotels.");
            return;
        }

        Hotel hotel = hotelDAO.findById(id);
        if (hotel == null) return;

        HotelDialog dialog = new HotelDialog((JFrame) SwingUtilities.getWindowAncestor(this), hotel);
        dialog.setVisible(true);
        if (dialog.wasSaved()) loadData();
    }
}
