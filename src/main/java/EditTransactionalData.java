import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


/*
 * View window for adding new transactional occupancy data (rooms, beds, month/year) per hotel.
 * US06
 *   AC-06-1: Hotel ID and name are pre-filled and locked (not editable).
 *   AC-06-2: Room occupancy, bed occupancy, and month fields are editable.
 *   AC-06-3: Entering a month that already has data for the same hotel shows a warning.
 * Structure layout documentation:
 *   GUI (constructor + setupUI) -> setupListeners -> validateInputs -> handleSave -> OccupancyUtility Input(=read)/Output(=write)
 */

public class EditTransactionalData extends JFrame {
    //window --> JFrame

    //attribute der klasse = Fields, die nicht bearbeitet werden können
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField roomField = new JTextField();
    private final JTextField bedField = new JTextField();
    private final JTextField yearField = new JTextField();
    private final JTextField monthField = new JTextField();
    private final JTextField usedRoomsField = new JTextField();
    private final JTextField usedBedField = new JTextField();
    private final JButton saveButton = new JButton("Save");//Speicher Button

    public EditTransactionalData(Hotel hotel) {
        //Hotelobjekt wird übergeben
        setTitle("Edit Transaction Data");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(null); // in der Mitte vom Screen
        setIconImage(new ImageIcon("src/main/resources/Logo.jpg").getImage());

        setupUI(hotel);//Methodenaufruf und Uebergabe

        setupListeners();
    }

    //Layout vom Fenster + GUI Komponenten erstellen&anordnen
    private void setupUI(Hotel hotel) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); //Händelt Absatzabstand
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));// macht Rahmen Abstand = nicht pickenden Text

        //Werte(aus Hotelobjekt) einfügen
        idField.setText(String.valueOf(hotel.id));
        nameField.setText(hotel.name);
        roomField.setText(String.valueOf(hotel.noRooms));
        bedField.setText(String.valueOf(hotel.noBeds));

        //unveränderbare Felder, nicht bearbeitbar machen
        idField.setEditable(false);
        nameField.setEditable(false);
        roomField.setEditable(false);
        bedField.setEditable(false);

        //fenster aufgebaut und die felder hinzugefügt
        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 10, 10));//eine row fürs Bild dazurechnen
        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Hotelname:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Number of rooms:"));
        inputPanel.add(roomField);
        inputPanel.add(new JLabel("Number of beds:"));
        inputPanel.add(bedField);
        inputPanel.add(new JLabel("Year:"));
        inputPanel.add(yearField);
        inputPanel.add(new JLabel("Month(in Number f.eg.: 1):"));
        inputPanel.add(monthField);
        inputPanel.add(new JLabel("Used Rooms:"));
        inputPanel.add(usedRoomsField);
        inputPanel.add(new JLabel("Used Beds:"));
        inputPanel.add(usedBedField);

        saveButton.setEnabled(true);

        //Komponenten ins Haupt-Panel einfügen
        mainPanel.add(new LogoComponent(), BorderLayout.PAGE_START);//Logo oben
        mainPanel.add(inputPanel, BorderLayout.CENTER);//Eingabe mittig
        mainPanel.add(saveButton, BorderLayout.SOUTH);//save Button unten

        add(mainPanel);
    }

    //zuerst zuhören dann login
    private void setupListeners() {
        // Validation listener ensures that fields aren't empty/too short
        saveButton.addActionListener(e -> validateInputs());// wenn Eingabe passt, Button = weiter zur handle safe methode
    }

    //es können nicht mehr betten belegt werden, als vorhanden
    // string in integer umwandeln zum vergleich
    private void validateInputs() {//weil in der klasse, nicht nötig zu übergeben
        try {
            if (Integer.parseInt(usedRoomsField.getText().trim()) > Integer.parseInt(roomField.getText().trim())) {
                JOptionPane.showMessageDialog(this, "The number of usedRooms has to be lower than the existing one's.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else if (Integer.parseInt(usedBedField.getText().trim()) > Integer.parseInt(bedField.getText().trim())) {
                JOptionPane.showMessageDialog(this, "The number of usedBeds has to be lower than the existing one's.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else if (Integer.parseInt(yearField.getText().trim()) < 1970) {
                JOptionPane.showMessageDialog(this, "Year has to be at least 1970", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else if (Integer.parseInt(monthField.getText().trim()) < 1 || Integer.parseInt(monthField.getText().trim()) > 12) {
                JOptionPane.showMessageDialog(this, "This month does not exist.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else {
                handleSave();//speichern, weil überprüfungen bestanden
            }
        } catch (
                NumberFormatException e) {//wenn parseInt nciht zahlen sieht wird exception geworfen nämlich numberformatexception
            JOptionPane.showMessageDialog(this, "Please enter a number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        }

    }

    //Speichern der Belegungsdaten: laden → prüfen auf Doppelte → speichern
    private void handleSave() {
        //saves occupancy to Database
        try {
            ArrayList<Occupancies> occupancies = OccupancyUtility.loadOccupanciesFromFile();//Alle vorhandenen Occupancies aus Datei laden

            for (Occupancies occupancy : occupancies) { //Nach bereits existierendem Eintrag suchen (gleicher Hotel-Jahr-Monat)
                if (occupancy.id == Integer.parseInt(idField.getText())) {// da ist ein = = sieht nur aus wie ==
                    if (occupancy.year == Integer.parseInt(yearField.getText())) {
                        if (occupancy.month == Integer.parseInt(monthField.getText())) {
                            JOptionPane.showMessageDialog(this, "Error: There is already an entry in the database for this month.");
                            return;//Abbruch von handlesave
                        }
                    }
                }
            }
            //Kein doppelter Eintrag → neues Occupancies-Objekt erstellen
            Occupancies o = new Occupancies(// beim übergeben wird noch umgewandelt
                    //id,rooms,usedrooms,beds,usedbeds,year,month
                    Integer.parseInt(idField.getText().trim()),//gib leerzeichen weg --> userfreundlich
                    Integer.parseInt(roomField.getText().trim()),
                    Integer.parseInt(usedRoomsField.getText().trim()),
                    Integer.parseInt(bedField.getText().trim()),
                    Integer.parseInt(usedBedField.getText().trim()),
                    Integer.parseInt(yearField.getText().trim()),
                    Integer.parseInt(monthField.getText().trim())
            );
            //Neuen Datensatz in Datei speichern
            OccupancyUtility.saveOccupancies(o);
            JOptionPane.showMessageDialog(this, "Occupancy has been saved successfully. ");
            dispose();//schließen

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
