package ui;

import db.PersonaDAO;
import model.Persona;

import javax.swing.*;
import java.awt.*;

/**
 * Add/Edit Persona Modal (Stories #12, #13).
 */
public class PersonaDialog extends JDialog {

    private final PersonaDAO dao = new PersonaDAO();
    private final Persona persona;
    private boolean saved = false;

    private JTextField     tfUsername  = new JTextField();
    private JPasswordField tfPassword  = new JPasswordField();
    private JComboBox<String> cbRole   = new JComboBox<>(new String[]{
            Persona.ROLE_SENIOR, Persona.ROLE_HEAD, Persona.ROLE_HOTEL
    });
    private JCheckBox      chkCanDelete = new JCheckBox("Can delete hotels (Story #13)");

    public PersonaDialog(JFrame parent, Persona persona) {
        super(parent, true);
        this.persona = persona;

        Image logo = LogoHelper.getLogoImage();
        if (logo != null) setIconImage(logo);

        setTitle(persona == null ? "Add new Persona" : "Edit Persona");
        setSize(420, 280);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        if (persona != null) {
            tfUsername.setText(persona.getUsername());
            tfUsername.setEditable(false);
            tfPassword.setText(persona.getPassword());
            cbRole.setSelectedItem(persona.getRole());
            chkCanDelete.setSelected(persona.isCanDelete());
        }
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        p.add(new JLabel("Username:"));    p.add(tfUsername);
        p.add(new JLabel("Password:"));    p.add(tfPassword);
        p.add(new JLabel("Role:"));        p.add(cbRole);
        p.add(new JLabel(""));             p.add(chkCanDelete);
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

    private void onSave() {
        String username = tfUsername.getText().trim();
        String password = new String(tfPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password required.");
            return;
        }

        Persona p = (persona == null) ? new Persona() : persona;
        p.setUsername(username);
        p.setPassword(password);
        p.setRole((String) cbRole.getSelectedItem());
        p.setCanDelete(chkCanDelete.isSelected());

        boolean ok = (persona == null) ? dao.insertPersona(p) : dao.updatePersona(p);
        if (ok) {
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Save failed (username already exists?).");
        }
    }

    public boolean wasSaved() { return saved; }
}
