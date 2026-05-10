package ui;

import db.PersonaDAO;
import model.Persona;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Persona Management (Story #12, #13).
 */
public class PersonaManagementPanel extends JPanel {

    private final PersonaDAO dao = new PersonaDAO();
    private DefaultTableModel model;
    private JTable table;

    private final String[] columns = {
            "ID", "Username", "Role", "Can Delete"
    };

    public PersonaManagementPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);

        loadData();
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAdd     = new JButton("+ Add Persona");
        JButton btnEdit    = new JButton("Edit");
        JButton btnDelete  = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadData());

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
        List<Persona> personas = dao.getAllPersonas();
        for (Persona p : personas) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getUsername(),
                    p.getRoleLabel(),
                    p.isCanDelete() ? "Yes" : "No"
            });
        }
    }

    private void onAdd() {
        PersonaDialog dlg = new PersonaDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dlg.setVisible(true);
        if (dlg.wasSaved()) loadData();
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a persona first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        for (Persona p : dao.getAllPersonas()) {
            if (p.getId() == id) {
                PersonaDialog dlg = new PersonaDialog((JFrame) SwingUtilities.getWindowAncestor(this), p);
                dlg.setVisible(true);
                if (dlg.wasSaved()) loadData();
                return;
            }
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a persona first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete persona '" + username + "'?",
                "Are you sure?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.deletePersona(id)) loadData();
            else JOptionPane.showMessageDialog(this, "Delete failed.");
        }
    }
}
