import ui.LoginDialog;
import ui.MainWindow;

import javax.swing.*;

/**
 * Einstiegspunkt der Anwendung.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // egal
        }

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
