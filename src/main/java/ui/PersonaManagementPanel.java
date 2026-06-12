package ui;

import db.PersonaDAO;
import model.Persona;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * User management for senior users.
 *
 * Covers the following user stories:
 *   US 12 - Create, list, edit, and delete personas
 *   US 13 - Set delete permission per user (canDelete)
 */
public class PersonaManagementPanel extends JPanel {

    private final PersonaDAO personaDAO = new PersonaDAO();
    private final DefaultTableModel model;
    private final JTable table;

    private static final String[] COLUMNS = {"ID", "Username", "Role", "Can Delete"};

    public PersonaManagementPanel() {
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

        JButton btnAdd    = new JButton("+ Add Persona");
        JButton btnEdit   = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        p.add(btnAdd);
        p.add(btnEdit);
        p.add(btnDelete);
        return p;
    }

    /** US 12: Load all personas into the table. */
    private void loadData() {
        model.setRowCount(0);
        for (Persona p : personaDAO.findAll()) {
            model.addRow(new Object[]{
                    p.getId(), p.getUsername(), p.getRoleLabel(),
                    p.isCanDelete() ? "Yes" : "No"
            });
        }
    }

    /** US 12: Create a new persona. */
    private void onAdd() {
        PersonaDialog dialog = new PersonaDialog(parentFrame(), null);
        dialog.setVisible(true);
        if (dialog.wasSaved()) loadData();
    }

    /** US 12/13: Edit the selected persona. */
    private void onEdit() {
        Integer id = selectedPersonaId();
        if (id == null) return;

        Persona persona = personaDAO.findById(id);
        if (persona == null) {
            JOptionPane.showMessageDialog(this, "Persona not found.");
            return;
        }

        PersonaDialog dialog = new PersonaDialog(parentFrame(), persona);
        dialog.setVisible(true);
        if (dialog.wasSaved()) loadData();
    }

    /** US 12: Delete the selected persona. */
    private void onDelete() {
        Integer id = selectedPersonaId();
        if (id == null) return;

        String username = (String) model.getValueAt(table.getSelectedRow(), 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete persona '" + username + "'?",
                "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            personaDAO.delete(id);
            loadData();
        }
    }

    /** Returns the ID of the selected persona, or null with a message if nothing is selected. */
    private Integer selectedPersonaId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a persona first.");
            return null;
        }
        return (int) model.getValueAt(row, 0);
    }

    private JFrame parentFrame() {
        return (JFrame) SwingUtilities.getWindowAncestor(this);
    }
}
