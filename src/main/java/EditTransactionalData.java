import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EditTransactionalData extends JFrame {
    //window --> JFrame

    //attribute der klasse = Fields
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField roomField = new JTextField();
    private final JTextField bedField = new JTextField();
    private final JTextField yearField = new JTextField();
    private final JTextField monthField = new JTextField();
    private final JTextField usedRoomsField = new JTextField();
    private final JTextField usedBedField = new JTextField();
    private final JButton saveButton = new JButton("Save");

    public EditTransactionalData(Hotel hotel) {
        //Screen dem Hotelobjekt übergeben wird
        setTitle("Edit Transaction Data");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null); // in der Mitte vom Screen

        setupUI(hotel);//Methodenaufruf und übergabe

        setupListeners();
    }

    //Layout vom Fenster
    private void setupUI(Hotel hotel) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); //Händelt Absatzabstand
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));// macht rahmen abstand nicht pickenden text)

        //werte einfügen
        idField.setText(String.valueOf(hotel.id));
        nameField.setText(hotel.name);
        roomField.setText(String.valueOf(hotel.noRooms));
        bedField.setText(String.valueOf(hotel.noBeds));

        //unveränderbare Felder:
        idField.setEditable(false);
        nameField.setEditable(false);
        roomField.setEditable(false);
        bedField.setEditable(false);

        //fenster aufgebaut und die felder hinzugefügt
        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 10, 10));//eine row fürs bild
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

        mainPanel.add(new LogoComponent(),  BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(saveButton, BorderLayout.SOUTH);

        add(mainPanel);
    }

    //zuerst zuhören dann login
    private void setupListeners() {
        // Validation listener ensures that fields aren't empty/too short
        saveButton.addActionListener(e -> validateInputs());// wenn eingabe passt, button und weiter zur handle safe methode
    }

    private void handleSave() {
        //TODO: save occupancy to Database
        //
    }

    //login window: es können nicht mehr betten belegt werden, als vorhanden. string in integer umwandeln zum vergleich
    private void validateInputs() {//weil in der klasse, nicht nötig zu +bergeben
        try {
            if (Integer.parseInt(usedRoomsField.getText()) > Integer.parseInt(roomField.getText())) {
                JOptionPane.showMessageDialog(this, "The number of usedRooms has to be lower than the existing one's.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else if (Integer.parseInt(usedBedField.getText()) > Integer.parseInt(bedField.getText())) {
                JOptionPane.showMessageDialog(this, "The number of usedBeds has to be lower than the existing one's.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else if (Integer.parseInt(yearField.getText()) < 1970) {
                JOptionPane.showMessageDialog(this, "Year has to be at least 1970", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else if (Integer.parseInt(monthField.getText())<1||Integer.parseInt(monthField.getText())>12) {
                JOptionPane.showMessageDialog(this, "This month does not exist.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else {
                handleSave();
            }
        } catch (NumberFormatException e) {//wenn parseInt nciht zahlen sieht wird exception geworfen nämlich numberformatexception
            JOptionPane.showMessageDialog(this, "Please enter a number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        }

    }
}
