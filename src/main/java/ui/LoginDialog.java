package ui;

import db.PersonaDAO;
import model.Persona;
import model.Session;

import javax.swing.*;
import java.awt.*;

/**
 * Login-Dialog. Username + Password gegen persona-Tabelle.
 */
public class LoginDialog extends JDialog {

    private final PersonaDAO personaDAO = new PersonaDAO();
    private boolean loggedIn = false;

    private JTextField     tfUsername = new JTextField(15);
    private JPasswordField tfPassword = new JPasswordField(15);

    public LoginDialog() {
        super((Frame) null, "Login - Lower Austria Tourist Portal", true);

        Image logo = LogoHelper.getLogoImage();
        if (logo != null) setIconImage(logo);

        setSize(420, 320);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new HeaderPanel(), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        form.add(new JLabel("Username:"));
        form.add(tfUsername);
        form.add(new JLabel("Password:"));
        form.add(tfPassword);
        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnLogin = new JButton("Login");
        JButton btnExit  = new JButton("Exit");
        btnLogin.addActionListener(e -> onLogin());
        btnExit.addActionListener(e -> System.exit(0));
        buttons.add(btnLogin);
        buttons.add(btnExit);
        add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnLogin);
    }

    private void onLogin() {
        String username = tfUsername.getText().trim();
        String password = new String(tfPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        Persona persona = personaDAO.login(username, password);
        if (persona == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
            tfPassword.setText("");
            return;
        }

        Session.setCurrentPersona(persona);

        if (Persona.ROLE_HOTEL.equals(persona.getRole())) {
            Session.setMyHotelIds(personaDAO.getHotelIdsForPersona(persona.getId()));
        }

        loggedIn = true;
        dispose();
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
