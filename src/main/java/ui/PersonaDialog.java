package ui;

import db.PersonaDAO;
import model.Persona;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for creating and editing a persona (US 12).
 * The “Can delete hotels” checkbox sets the delete permission (US 13).
 */
public class PersonaDialog extends JDialog {

    private final PersonaDAO personaDAO = new PersonaDAO();
    private final Persona persona;        // null = new persona, otherwise edit
    private boolean saved = false;

    private final JTextField     tfUsername = new JTextField();
    private final JPasswordField tfPassword = new JPasswordField();
    private final JComboBox<String> cbRole  =
            new JComboBox<>(new String[]{Persona.ROLE_SENIOR, Persona.ROLE_HEAD, Persona.ROLE_HOTEL});
    private final JCheckBox chkCanDelete = new JCheckBox("Can delete hotels");

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

        if (persona != null) fillFields(persona);
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        p.add(new JLabel("Username:")); p.add(tfUsername);
        p.add(new JLabel("Password:")); p.add(tfPassword);
        p.add(new JLabel("Role:"));     p.add(cbRole);
        p.add(new JLabel(""));          p.add(chkCanDelete);
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

    private void fillFields(Persona p) {
        tfUsername.setText(p.getUsername());
        tfUsername.setEditable(false);       // The username is the key; it cannot be changed
        tfPassword.setText(p.getPassword());
        cbRole.setSelectedItem(p.getRole());
        chkCanDelete.setSelected(p.isCanDelete());
    }



    public boolean wasSaved() {
        return saved;
    }
}
