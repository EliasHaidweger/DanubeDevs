import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AddHotelForm extends JFrame {

    //Eingabefelder für die Hotel Stammdaten
    JTextField categoryField = new JTextField();
    JTextField nameField = new JTextField();
    JTextField ownerField = new JTextField();
    JTextField contactField = new JTextField();
    JTextField addressField = new JTextField();
    JTextField cityField = new JTextField();
    JTextField cityCodeField = new JTextField();
    JTextField stateField = new JTextField();
    JTextField roomsField = new JTextField();
    JTextField bedsField = new JTextField();

    //Button zum speichern
    JButton saveButton = new JButton("Save Hotel");

    public AddHotelForm() {
        //Frame Einstellungen
        setTitle("Add New Hotel");
        setSize(500, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Haupt-Layout: BorderLayout erlaubt die Trennung in Logo (Nord) und Formular (Mitte)
        setLayout(new BorderLayout());

        //Logo jetzt mal auskommentiert damit keine Fehler sind
        //add(new LogoComponet(), BorderLayout.NORTH);

        //Panel für die Formularfelder mit GridLayout (Label links, Textfeld rechts)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(12, 2,10, 10));

        //Gestaltung
        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10,10,10,10), BorderFactory.createTitledBorder("Hotel Details")));

        //Label zeigt an, dass ID automatisch und nicht editierbar
        mainPanel.add(new JLabel("Hotel ID"));
        mainPanel.add(new JLabel("Auto Generated"));

        //Stammdatenfelden hinzufügen
        mainPanel.add(new JLabel("Category"));
        mainPanel.add(categoryField);

        mainPanel.add(new JLabel("Name"));
        mainPanel.add(nameField);

        mainPanel.add(new JLabel("Owner"));
        mainPanel.add(ownerField);

        mainPanel.add(new JLabel("Contact"));
        mainPanel.add(contactField);

        mainPanel.add(new JLabel("Address"));
        mainPanel.add(addressField);

        mainPanel.add(new JLabel("City"));
        mainPanel.add(cityField);

        mainPanel.add(new JLabel("City Code"));
        mainPanel.add(cityCodeField);

        mainPanel.add(new JLabel("State"));
        mainPanel.add(stateField);

        mainPanel.add(new JLabel("Number Rooms"));
        mainPanel.add(roomsField);

        mainPanel.add(new JLabel("Number Beds"));
        mainPanel.add(bedsField);

        //Platzhalter und Speicherbutton
        mainPanel.add(new JLabel(""));
        mainPanel.add(saveButton);

        //Panel zum ausfüllen mittig
        this.add(mainPanel, BorderLayout.CENTER);

        //Speichervorgang beim Button
        saveButton.addActionListener(e -> saveHotel());

        //Fenster zentrieren und sichtbar machen
        setLocationRelativeTo(null);
        setVisible(true);

    }
    //Sammelt Daten aus den Feldern und gibt es zur Speicher-Methode
    private void saveHotel() {
        try {
            //Hotelname darf nicht leer sein
            if (nameField.getText().isBlank() ||
                    addressField.getText().isBlank() ||
                    categoryField.getText().isBlank() ||
                    roomsField.getText().isBlank() ||
                    bedsField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Error: All required fields (Name, Address, Category, Rooms, Beds) must be filled!");
                return;
            }
            //Prüfung auf existierenden Namen
            ArrayList<Hotel> existingHotels = HotelUtility.loadHotelsFromFile();
            for (Hotel h : existingHotels) {
                if (h.getName().equalsIgnoreCase(nameField.getText().trim())) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "A hotel with this name already exists. Do you want to save anyway?",
                            "Warning", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.NO_OPTION) {
                        return; // Abbrechen, wenn der User "Nein" klickt
                    }
                    break; // Bei "Ja" wird die Schleife verlassen und gespeichert
                }
            }
            //Konvertierung der Zahlenwerte
            int rooms = Integer.parseInt(roomsField.getText());
            int beds = Integer.parseInt(bedsField.getText());

            //Generierung einer neuen ID über die Hilfsklasse
            int newId = HotelUtility.generateNewHotelId();

            //neues Hotel Objekt
            Hotel newHotel = new Hotel(
                    newId,
                    categoryField.getText(),
                    nameField.getText(),
                    ownerField.getText(),
                    contactField.getText(),
                    addressField.getText(),
                    cityField.getText(),
                    cityCodeField.getText(),
                    stateField.getText(),
                    rooms,
                    beds
            );
            //Speichern über Utility Klasse
            HotelUtility.saveHotel(newHotel);
            //Bestätigung
            JOptionPane.showMessageDialog(this, "Hotel has been saved successfully " + newId);
            //Schließen und neues Fenster zum hinzufügen
            dispose();
            new AddHotelForm();

        }catch (NumberFormatException ex) {
            //Fehlermeldung falls keien gültigen Zahlen
            JOptionPane.showMessageDialog(this, "Error: Please enter valid numbers for Rooms and Beds.");

        }catch (Exception ex) {
            JOptionPane.showMessageDialog(this,"Error: " + ex.getMessage());
        }
    }

}
