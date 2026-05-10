package ui;

import db.HotelDAO;
import model.Hotel;
import model.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Hotel-Liste mit Add/Edit/Delete.
 *
 * Stories: #4 (Liste), #3 (Add), #5 (Edit), #11 (Delete), #13 (Permission).
 */
public class HotelListPanel extends JPanel {

    private final HotelDAO dao = new HotelDAO();
    private DefaultTableModel model;
    private JTable table;
    private JButton btnDelete;

    private final String[] columns = {
            "ID", "Category", "Name", "City", "Rooms", "Beds", "Tags"
    };

    public HotelListPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);

        loadData();
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAdd     = new JButton("+ Add Hotel");
        JButton btnEdit    = new JButton("Edit");
        btnDelete          = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData());

        // Story #13
        if (Session.getCurrentPersona() == null || !Session.getCurrentPersona().isCanDelete()) {
            btnDelete.setEnabled(false);
            btnDelete.setToolTipText("You do not have permission to delete hotels.");
        }

        p.add(btnAdd);
        p.add(btnEdit);
        p.add(btnDelete);
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
        List<Hotel> hotels = dao.getAllHotels();
        for (Hotel h : hotels) {
            model.addRow(new Object[]{
                    h.getId(), h.getCategory(), h.getName(),
                    h.getCity(), h.getNoRooms(), h.getNoBeds(), h.getTags()
            });
        }
    }

    private void onAdd() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        HotelDialog dlg = new HotelDialog(parent, null);
        dlg.setVisible(true);
        if (dlg.wasSaved()) loadData();
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a hotel first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        Hotel h = dao.getHotelById(id);
        if (h == null) {
            JOptionPane.showMessageDialog(this, "Hotel not found.");
            return;
        }
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        HotelDialog dlg = new HotelDialog(parent, h);
        dlg.setVisible(true);
        if (dlg.wasSaved()) loadData();
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a hotel first.");
            return;
        }
        int id      = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete hotel '" + name + "' (ID " + id + ")?\n"
              + "All linked occupancy data will also be deleted!",
                "Are you sure?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.deleteHotel(id)) loadData();
            else JOptionPane.showMessageDialog(this, "Delete failed.");
        }
    }
}
