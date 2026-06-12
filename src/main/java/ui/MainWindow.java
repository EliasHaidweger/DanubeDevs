package ui;

import model.Persona;
import model.Session;

import javax.swing.*;
import java.awt.*;

/**
 * Hauptfenster der Anwendung.
 *
 * Zeigt oben das Logo und den angemeldeten Benutzer mit Logout-Button,
 * darunter die Tabs. Welche Tabs sichtbar sind, haengt von der Rolle
 * des angemeldeten Benutzers ab (Senior User, Head of NOE-TO, Hotel Rep).
 */
public class MainWindow extends JFrame {

    public MainWindow() {
        Persona persona = Session.getCurrentPersona();
        setTitle("Lower Austria Tourist Portal - " + persona.getRoleLabel());

        Image logo = LogoHelper.getLogoImage();
        if (logo != null) setIconImage(logo);

        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(),   BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);

        header.add(new HeaderPanel(), BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        right.setBackground(Color.WHITE);

        Persona persona = Session.getCurrentPersona();
        JLabel info = new JLabel(
                "Logged in as: " + persona.getUsername() + " (" + persona.getRoleLabel() + ")"
        );
        info.setFont(new Font("SansSerif", Font.PLAIN, 12));
        right.add(info);

        JButton btnLogout = new JButton("Log out");
        btnLogout.addActionListener(e -> onLogout());
        right.add(btnLogout);

        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        String role = Session.getCurrentPersona().getRole();

        if (Persona.ROLE_SENIOR.equals(role)) {
            tabs.addTab("Dashboard",          new DashboardPanel());
            tabs.addTab("Hotels",             new HotelListPanel());
            tabs.addTab("Enter Occupancy",    new OccupancyPanel());
            tabs.addTab("Statistics",         new StatisticsPanel());
            tabs.addTab("Persona Management", new PersonaManagementPanel());
            tabs.addTab("Import",             new ImportPanel());
            tabs.addTab("Help",               new HelpPanel());

        } else if (Persona.ROLE_HEAD.equals(role)) {
            tabs.addTab("Dashboard",  new DashboardPanel());
            tabs.addTab("Statistics", new StatisticsPanel());
            tabs.addTab("Help",       new HelpPanel());

        } else if (Persona.ROLE_HOTEL.equals(role)) {
            tabs.addTab("My Hotels",        new MyHotelsPanel());
            tabs.addTab("Submit Occupancy", new MySubmissionPanel());
            tabs.addTab("Help",             new HelpPanel());
        }

        return tabs;
    }

    private void onLogout() {
        Session.clear();
        dispose();

        SwingUtilities.invokeLater(() -> {
            LoginDialog login = new LoginDialog();
            login.setVisible(true);

            if (login.isLoggedIn()) {
                new MainWindow().setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}
