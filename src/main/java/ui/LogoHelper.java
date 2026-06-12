package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Laedt das NOE-Logo aus den Resources (US 17).
 * Das Logo liegt unter src/main/resources/Logo.jpg und wird im
 * Login-Fenster, im Hauptfenster und als Fenster-Icon verwendet.
 */
public class LogoHelper {

    private LogoHelper() {
        // Utility-Klasse - keine Instanzen
    }

    /** Logo als ImageIcon, auf die gewuenschte Hoehe skaliert (oder null). */
    public static ImageIcon getLogoIcon(int height) {
        Image image = loadImage();
        if (image == null) return null;
        return new ImageIcon(image.getScaledInstance(-1, height, Image.SCALE_SMOOTH));
    }

    /** Logo als Image fuer das Fenster-Icon (oder null). */
    public static Image getLogoImage() {
        return loadImage();
    }

    private static Image loadImage() {
        try {
            URL url = LogoHelper.class.getResource("/Logo.jpg");
            return (url != null) ? ImageIO.read(url) : null;
        } catch (Exception e) {
            return null;   // Logo ist optional - bei Fehler einfach keines anzeigen
        }
    }
}
