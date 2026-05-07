import javax.swing.*;
import java.awt.*;

public class HelpTab extends JFrame {

    public HelpTab() {
        setTitle("Lower Austria Tourist Portal - Help");
        setSize(800, 600);
        //Dispose damit nur Hilfe Tab geschlossen wird
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //Tab-System
        JTabbedPane tabbedPane = new JTabbedPane();

        //Hilfe-Tab
        tabbedPane.addTab("Master Data", createHelpContent(getMasterDataHelp()));
        tabbedPane.addTab("Transactional Data", createHelpContent(getTransactionalHelp()));
        tabbedPane.addTab("User Management", createHelpContent(getUserManagementHelp()));
        tabbedPane.addTab("Reports", createHelpContent(getReportsHelp()));
        tabbedPane.addTab("About", createHelpContent(getAboutHelp()));

        add(tabbedPane, BorderLayout.CENTER);

        //Button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        //Panel für Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);

    }
    private JPanel createHelpContent(String content) {
        JPanel panel = new JPanel(new BorderLayout());

        //Textbereich für die Hilfe-Information
        JTextArea helpText = new JTextArea(content);
        helpText.setEditable(false);
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setMargin(new Insets(15, 15, 15, 15));

        helpText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(helpText);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    private String getMasterDataHelp() {
        return """
                MANAGING HOTEL MASTER DATA
                
                This section allows you to maintain the database of all registered tourist establishments in Lower Austria.
                
                How to add a new hotel:
                - Open the 'Master Data' view and click 'Add Hotel'.
                - Provide the necessary details such as name, address, and category.
                - Note: The Hotel ID is assigned automatically by the system to ensure data integrity.
                
                Updating Information:
                If a hotel changes its capacity or contact details, you can edit the entry directly. Keeping this data current is essential for accurate provincial statistics.
                
                Deleting Entries:
                Only Senior Users are authorized to delete hotels. Please be aware that removing a hotel will also permanently delete all its historical occupancy data.
                """;
    }

    private String getTransactionalHelp() {
        return """
                OCCUPANCY & TRANSACTIONAL DATA
                
                Tracking occupancy rates is vital for the NOE-TO to support the tourism industry effectively.
                
                Submission Deadline:
                Please ensure that all occupancy data is entered into the system by the 5th business day of the following month.
                
                Manual Entry:
                1. Select the relevant hotel.
                2. Input the room and bed occupancy for the reporting month.
                3. Save the entry to update the statistics.
                
                Data Imports:
                Senior Users can utilize the automated import feature for Excel or Email submissions to streamline the collection process.
                
                Reviewing Data:
                Use the 'Combined View' to monitor submissions and identify any missing reports for the current period.
                """;
    }

    private String getUserManagementHelp() {
        return """
                ROLES AND PERMISSIONS
                
                To protect sensitive business information, the portal uses a role-based access system.
                
                - Senior Users (NOE-TO Staff):
                  Have full administrative access, including the ability to manage user accounts, edit all master data, and delete records.
                
                - Hotel Representatives:
                  Access is limited to their own establishments. They can view and update their own hotel's data but cannot see information from other companies.
                
                Security Note:
                Permissions for sensitive actions, such as deleting master data, are restricted to specific authorized roles.
                """;
    }

    private String getReportsHelp() {
        return """
                STATISTICS AND REPORTING
                
                The portal generates high-quality insights to help identify trends in the tourist sector.
                
                PDF Exports:
                Standardized monthly reports can be generated as .pdf files. These are designed for internal use or for reporting to the government authorities.
                
                Visual Analytics:
                The 'Charts' section provides histograms for room and bed occupancy. This visual representation helps in detecting early indicators of market changes or crises.
                
                Distribution:
                Reports can be sent directly via email to predefined addresses, facilitating easy sharing with stakeholders.
                """;
    }

    private String getAboutHelp() {
        return """
                SYSTEM INFORMATION
                
                Lower Austria Tourist Portal 2026
                Developed for: NOE-Tourism Office GmbH (NOE-TO)
                
                Infrastructure:
                This application is hosted on the private cloud environment of the Government of Lower Austria.
                
                Support:
                For technical assistance or issues regarding the cloud environment, please contact the internal IT competency centers or the project contact, Claudia Nemecek.
                
                Version: 1.0
                """;
    }
}


