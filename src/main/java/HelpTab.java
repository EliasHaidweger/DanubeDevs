import javax.swing.*;
import java.awt.*;

public class HelpTab extends JFrame {

    public HelpTab() {
        setTitle("Lower Austria Tourist Portal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Tab-System
        JTabbedPane tabbedPane = new JTabbedPane();

        //Hilfe-Tab
        JPanel helpPanel = createHelpContent();
        tabbedPane.addTab("Help", helpPanel);

        add(tabbedPane);
    }
    private JPanel createHelpContent() {
        JPanel panel = new JPanel(new BorderLayout());

        //Textbereich für die Hilfe-Information
        JTextArea helpText = new JTextArea();
        helpText.setEditable(false);
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setMargin(new Insets(10, 10, 10, 10));

        //Inhalt


        JScrollPane scrollPane = new JScrollPane(helpText);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}
