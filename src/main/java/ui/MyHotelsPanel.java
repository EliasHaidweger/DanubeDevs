package ui;

import db.HotelDAO;
import model.Hotel;
import model.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Hotel Rep sieht nur eigene Hotels (Stories #24, #25).
 */
public class MyHotelsPanel extends JPanel {

    private final HotelDAO dao = new HotelDAO();
    private DefaultTableModel model;
    private JTable table;

    private final String[] columns = {
            "ID", "Name", "City", "Category", "Rooms", "Beds"
    };

    public MyHotelsPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);

        loadData();
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnEdit    = new JButton("Edit");
        JButton btnRefresh = new JButton("Refresh");
        btnEdit.addActionListener(e -> onEdit());
        btnRefresh.addActionListener(e -> loadData());
        p.add(btnEdit);
        p.add(btnRefresh);
        return p;
    }

    private JScrollPane buildTable() {
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        return new JScrollPane(table);
    }

    public void loadData() {
        model.setRowCount(0);
        for (Integer id : Session.getMyHotelIds()) {
            Hotel h = dao.getHotelById(id);
            if (h != null) {
                model.addRow(new Object[]{
                        h.getId(), h.getName(), h.getCity(),
                        h.getCategory(), h.getNoRooms(), h.getNoBeds()
                });
            }
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a hotel first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);

        if (!Session.getMyHotelIds().contains(id)) {
            JOptionPane.showMessageDialog(this, "Permission denied.");
            return;
        }

        Hotel h = dao.getHotelById(id);
        if (h == null) return;

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        HotelDialog dlg = new HotelDialog(parent, h);
        dlg.setVisible(true);
        if (dlg.wasSaved()) loadData();
    }
}
