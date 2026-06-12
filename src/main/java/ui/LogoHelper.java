package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Download the NOE logo from the Resources section (US 17).
 * The logo is located at src/main/resources/Logo.jpg and is used in the
 * used in the login window, the main window, and as a window icon.
 */
public class LogoHelper {

    private LogoHelper() {
        // Utility class - no instances
    }

    /** Logo as an image icon, scaled to the desired height (or zero). */
    public static ImageIcon getLogoIcon(int height) {
        Image image = loadImage();
        if (image == null) return null;
        return new ImageIcon(image.getScaledInstance(-1, height, Image.SCALE_SMOOTH));
    }

    /** Logo to use as the window icon (or none). */
    public static Image getLogoImage() {
        return loadImage();
    }

    private static Image loadImage() {
        try {
            URL url = LogoHelper.class.getResource("/Logo.jpg");
            return (url != null) ? ImageIO.read(url) : null;
        } catch (Exception e) {
            return null;   // The logo is optional, if there's an error, just don't display one
        }
    }
}
