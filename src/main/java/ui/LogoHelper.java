package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Hilfsklasse zum Laden des Logos.
 * Logo liegt unter src/main/resources/Logo.jpg
 */
public class LogoHelper {

    /** Logo als ImageIcon (skaliert auf Hoehe). */
    public static ImageIcon getLogoIcon(int height) {
        Image img = loadImage();
        if (img == null) return null;
        Image scaled = img.getScaledInstance(-1, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /** Logo als Image (fuer Fenster-Icon, ersetzt den Pinguin). */
    public static Image getLogoImage() {
        return loadImage();
    }

    /**
     * Probiert mehrere Pfade durch um das Logo zu finden.
     */
    private static Image loadImage() {
        // 1) Resource im Classpath: /Logo.jpg (aus src/main/resources/)
        try {
            URL url = LogoHelper.class.getResource("/Logo.jpg");
            if (url != null) return ImageIO.read(url);
        } catch (Exception ignored) {}

        // 2) ClassLoader-Variante
        try {
            InputStream in = LogoHelper.class.getClassLoader().getResourceAsStream("Logo.jpg");
            if (in != null) return ImageIO.read(in);
        } catch (Exception ignored) {}

        // 3) Fallback: direkt aus dem Filesystem
        try {
            File file = new File("src/main/resources/Logo.jpg");
            if (file.exists()) return ImageIO.read(file);
        } catch (Exception ignored) {}

        System.err.println("Logo nicht gefunden!");
        return null;
    }
}
