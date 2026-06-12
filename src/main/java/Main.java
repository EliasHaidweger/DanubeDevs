import ui.LoginDialog;
import ui.MainWindow;

import javax.swing.*;

/**
 * Einstiegspunkt der Anwendung.
 *
 * Zeigt zuerst den Login-Dialog. Nach erfolgreichem Login oeffnet sich
 * das Hauptfenster mit den fuer die jeweilige Rolle passenden Tabs.
 */
public class Main {

    public static void main(String[] args) {
        applySystemLookAndFeel();

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

    /** Verwendet das native Aussehen des Betriebssystems (statt des Java-Standards). */
    private static void applySystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Faellt auf das Standard-Look-and-Feel zurueck - unkritisch
        }
    }
}