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
        Image scaled = img.getScaledInstance(150, 40, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(scaled));
    }
}
