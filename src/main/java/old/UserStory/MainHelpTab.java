package old.UserStory;

import javax.swing.*;

public class MainHelpTab {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            HelpTab helpTab = new HelpTab();
            helpTab.setVisible(true);

            System.out.println("Lower Austria Tourist Portal - Help Module 1.0 started.");
        });
    }
}
