package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Help tab with user manual (US 18).
 */
public class HelpPanel extends JPanel {

    public HelpPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextArea text = new JTextArea(
            "===========================================\n"
          + "  Lower Austria Tourist Portal - Help\n"
          + "===========================================\n\n"

          + "DASHBOARD\n"
          + "Shows total number of hotels, rooms and beds.\n"
          + "The table below lists statistics per category.\n\n"

          + "HOTELS\n"
          + "  + Add Hotel : opens a dialog to register a new hotel.\n"
          + "                Hotel ID is generated automatically.\n"
          + "  Edit        : select a row and click Edit.\n"
          + "  Delete      : select a row and click Delete.\n"
          + "                Only personas with delete permission can use this.\n\n"

          + "ENTER OCCUPANCY\n"
          + "  Choose a hotel from the dropdown.\n"
          + "  The hotel ID, rooms and beds fill in automatically.\n"
          + "  Enter month, year, used rooms, used beds and click Save.\n"
          + "  If a record already exists for this month, it will be updated.\n\n"

          + "STATISTICS\n"
          + "  Two views:\n"
          + "    - 'By month/year' : all hotels for one month\n"
          + "    - 'By hotel'      : all months for one hotel\n"
          + "  Choose filters and click 'Show'.\n\n"

          + "PERSONA MANAGEMENT (Senior only)\n"
          + "  Manage persona accounts and roles.\n"
          + "  'Can Delete' permission must be ticked for personas\n"
          + "  who should be allowed to delete hotels (US 13).\n\n"

          + "IMPORT (Senior only)\n"
          + "  Import occupancy data from a CSV file.\n"
          + "  See the Import tab for the expected format.\n\n"

          + "MY HOTELS (Hotel Representative)\n"
          + "  Shows only hotels assigned to your account.\n"
          + "  You can edit your hotel data but cannot delete.\n\n"

          + "SUBMIT OCCUPANCY (Hotel Representative)\n"
          + "  Submit your monthly occupancy data here.\n"
          + "  Below you see all your past submissions.\n\n"

          + "LOG OUT\n"
          + "  Use the 'Log out' button at the top right to switch persona.\n"
        );
        text.setEditable(false);
        text.setFont(new Font("Monospaced", Font.PLAIN, 13));
        text.setCaretPosition(0);

        add(new JScrollPane(text), BorderLayout.CENTER);
    }
}
