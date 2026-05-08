import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
 * Reusable logo component that displays the NOE-TO logo scaled without distortion in the UI.
 * US17
 *   AC-17-1: The logo appears on all main windows
 *   AC-17-2: Written copyright clearance is saved before the logo is added
 *   AC-17-3: The logo is not distorted (scaled proportionally)
 * Structure layout documentation:
 *   LogoComponent (JLabel subclass) -> loadImage (ImageIO) -> scaleImage (getScaledInstance) -> setIcon (ImageIcon)
 */


public class LogoComponent extends JLabel {

    //TO DO: kopiere das hier hinein in dein Window
    //mainPanel.add(new LogoComponent(),  BorderLayout.NORTH);

    public LogoComponent() {
        BufferedImage img = null; //BufferedImage ist das Logo, welches noch skaliert werden muss

        try {
            img = ImageIO.read(new File("src/main/resources/Logo.jpg"));//Bild laden
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*
        Bildskalierung: das Bild auf eine Höhe von 80 Pixel skalieren, Breite automatisch anpassen (-1),
            damit das Bild nicht verzerrt wird und in der Breite proportional mit skaliert
            Image.SCALE_SMOOTH sorgt für eine hochwertigere, weichere Skalierung
         */
        Image scaled = img.getScaledInstance(-1, 80, Image.SCALE_SMOOTH);
        /*Icon setzen: aus dem skalierten Bild ein ImageIcon erstellen und als Icon dieses JLabels festlegen
            Damit wird der JLabel zum Logo in der Größe 80px hoch, proportional skaliert*/
        setIcon(new ImageIcon(scaled));

    }
}