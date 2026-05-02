import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LogoComponent extends JLabel {

    //TO DO: kopiere das hier hinein in dein window
    //mainPanel.add(new LogoComponent(),  BorderLayout.NORTH);

    public LogoComponent() {
        BufferedImage img = null;

        try {
            img = ImageIO.read(new File("src/main/resources/Logo.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Image scaled = img.getScaledInstance(-1, 80, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(scaled));
    }
}
/*import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class LogoComponent extends JComponent {
    private BufferedImage originalImage;// original
    private Image scaledImage;//angepasstes

    public LogoComponent() {
        try {
            originalImage = ImageIO.read(new java.io.File("src/main/resources/Logo.jpg")); // Bild reinladen
            updateScaledImage(); //methodenaufruf
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Logo.jpg nicht gefunden: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateScaledImage() {
        if (originalImage != null) {//wenn vorhanden
            scaledImage = originalImage.getScaledInstance(-1, 80, Image.SCALE_SMOOTH);//methoden defintion
        } //breite ist variabel > image ratio beibehalten während 80px hoch
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (scaledImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // **Kein vertikales Zentrieren** - Bild oben links, aber Panel passt sich an
            g2d.drawImage(scaledImage, 0, 0, this); //(0,0 links oben)
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (scaledImage != null) {
            return new Dimension(scaledImage.getWidth(null), scaledImage.getHeight(null));
        }
        return new Dimension(250, 80);
    }
}*/