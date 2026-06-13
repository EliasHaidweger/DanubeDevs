package ui;

import db.HotelDAO;
import model.Hotel;
import model.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Hotel Management for Senior Users.
 *
 * Covers the following user stories:
 *   US 4  - List of all hotels
 *   US 3  - Create Hotel (opens HotelDialog)
 *   US 5  - Edit Hotel (opens the Hotel dialog)
 *   US 11 - Cancel hotel reservation (with confirmation)
 *   US 13 - Delete only if authorized (canDelete)
 */
public class HotelListPanel extends JPanel {

    private final HotelDAO hotelDAO = new HotelDAO();
    private final DefaultTableModel model;
    private final JTable table;

    private static final String[] COLUMNS =
            {"ID", "Category", "Name", "City", "Rooms", "Beds", "Tags"};

    public HotelListPanel() {
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
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAdd    = new JButton("+ Add Hotel");
        JButton btnEdit   = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        // US 13: Delete nur fuer Personas mit Loeschberechtigung
        if (!currentUserCanDelete()) {
            btnDelete.setEnabled(false);
            btnDelete.setToolTipText("You do not have permission to delete hotels.");
        }

        p.add(btnAdd);
        p.add(btnEdit);
        p.add(btnDelete);
        return p;
    }


        /** Returns the ID of the selected hotel, or null with a message if nothing is selected. */
        private Integer selectedHotelId() {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a hotel first.");
                return null;
            }
            return (int) model.getValueAt(row, 0);
        }

        private boolean currentUserCanDelete() {
            return Session.getCurrentPersona() != null
                    && Session.getCurrentPersona().isCanDelete();
        }

        private JFrame parentFrame() {
            return (JFrame) SwingUtilities.getWindowAncestor(this);
        }
    }
