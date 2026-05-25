package ui;

import db.HotelDAOHibernate;
import model.Hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Neuer Tab "Hotels (Hibernate)" - macht das gleiche wie der normale
 * Hotels-Tab, nutzt aber den HotelDAOHibernate statt HotelDAO.
 *
 * So sieht man im UI: beide Versionen arbeiten auf der gleichen DB
 * und liefern die gleichen Daten.
 */
public class HibernateDemoPanel extends JPanel {

    private final HotelDAOHibernate dao = new HotelDAOHibernate();
    private DefaultTableModel model;
    private JTable table;

    private final String[] columns = {
            "ID", "Category", "Name", "City", "Rooms", "Beds"
    };

    public HibernateDemoPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildInfo(),    BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);

        loadData();
    }

    /** Info-Text oben damit der Prof sieht: "Aha, das ist die Hibernate-Variante" */
    private JLabel buildInfo() {
        JLabel info = new JLabel(
                "<html><b>Hibernate-Demo:</b> Diese Tabelle wird ueber Hibernate geladen "
        );
        info.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        return info;
    }

    private JScrollPane buildTable() {
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        return new JScrollPane(table);
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAdd     = new JButton("+ Add Hotel (Hibernate)");
        JButton btnDelete  = new JButton("Delete (Hibernate)");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> onAdd());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData());

        p.add(btnAdd);
        p.add(btnDelete);
        p.add(btnRefresh);
        return p;
    }

    private void loadData() {
        model.setRowCount(0);
        List<Hotel> hotels = dao.getAllHotels();   // <-- Hibernate!
        for (Hotel h : hotels) {
            model.addRow(new Object[]{
                    h.getId(), h.getCategory(), h.getName(),
                    h.getCity(), h.getNoRooms(), h.getNoBeds()
            });
        }
    }

    /** Beispiel-Hotel anlegen ueber Hibernate. */
    private void onAdd() {
        String name = JOptionPane.showInputDialog(this, "Hotel name:");
        if (name == null || name.isBlank()) return;

        Hotel h = new Hotel();
        h.setId(dao.getNextId());
        h.setCategory("***");
        h.setName(name);
        h.setCity("Demo City");
        h.setNoRooms(50);
        h.setNoBeds(100);

        if (dao.insertHotel(h)) {
            JOptionPane.showMessageDialog(this, "Saved via Hibernate (ID " + h.getId() + ")");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Save failed.");
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a hotel first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        if (dao.deleteHotel(id)) loadData();
        else JOptionPane.showMessageDialog(this, "Delete failed.");
    }
}
