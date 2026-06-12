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
 * Hotelansicht fuer den Hotel Representative.
 *
 * Deckt folgende User Stories ab:
 *   US 24 - Liste der eigenen, zugeordneten Hotels
 *   US 25 - Eigene Hoteldaten bearbeiten
 *
 * Die zugeordneten Hotel-IDs stehen in der Session (beim Login geladen).
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

    /** US 24: Laedt die dem Benutzer zugeordneten Hotels. */
    private void loadData() {
        model.setRowCount(0);
        for (Integer id : Session.getMyHotelIds()) {
            Hotel h = hotelDAO.findById(id);
            if (h != null) {
                model.addRow(new Object[]{
                        h.getId(), h.getName(), h.getCity(),
                        h.getCategory(), h.getNoRooms(), h.getNoBeds()
                });
            }
        }
    }

    /** US 25: Eigenes Hotel bearbeiten (nur fuer zugeordnete Hotels erlaubt). */
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
