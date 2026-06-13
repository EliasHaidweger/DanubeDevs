package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Displays the NOE logo at the top of the window (US 17).
 *
 */
public class HeaderPanel extends JPanel {

    public HeaderPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setBackground(Color.WHITE);

        ImageIcon icon = LogoHelper.getLogoIcon(70);
        if (icon != null) {
            add(new JLabel(icon));
        } else {
            add(new JLabel("Lower Austria Tourist Portal"));
        }
    }
}