import ui.LoginDialog;
import ui.MainWindow;

import javax.swing.*;

/**
 * Entry point of the application.
 *
 * First displays the login dialog. After a successful login, the following opens
 * the main window with the tabs appropriate for each role.
 *
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

    /** Uses the operating system's native appearance (instead of the Java standard). */
    private static void applySystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Falls back on the default look and feel - non-critical
        }
    }
}
