import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * View window that displays transactional data (occupancy records) per hotel with a month/year range filter.
 * US10
 *   AC-10-1: Hotel dropdown to select a specific hotel
 *   AC-10-2: FROM and TO month/year filters to limit the results shown
 *   AC-10-3: All shown records match the data for the selected hotel and date range
 * Structure layout documentation:
 *   GUI (lines 21-51) -> initComponents (lines 53-99) -> addComponents (liens 101-129)
 *  -> Filter (lines 131-190)-> Scanners for txt files (lines 197-276)
 *
 * ToDo: swap local data to the database
 */
public class TransactionalDataView extends JFrame {

    private ArrayList<Hotel> hotels;
    private ArrayList<Occupancies> occupancies;

    private JComboBox<String> hotelDropdown;
    private JComboBox<String> fromMonthBox;
    private JComboBox<String> fromYearBox;
    private JComboBox<String> toMonthBox;
    private JComboBox<String> toYearBox;
    private JButton filterButton;

    private JTable table;
    private DefaultTableModel model;

    public TransactionalDataView() {
        loadHotels();// Read hotels & occupancies from the txt files
        loadOccupancies();
        defineFrame();
        initComponents();
        addComponents();

        applyFilter();
    }

    private void defineFrame() {
        setTitle("Transactional Data - Occupancy per Hotel");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {

        // Build hotel dropdown boxes based on the loaded data from hotels.txt
        String[] hotelNames = new String[hotels.size() + 1];
        hotelNames[0] = "All Hotels";
        for (int i = 0; i < hotels.size(); i++) {
            hotelNames[i + 1] = hotels.get(i).id + " - " + hotels.get(i).name;
        }
        hotelDropdown = new JComboBox<>(hotelNames);
        hotelDropdown.setSelectedIndex(0);

        String[] months = {"01", "02", "03", "04", "05", "06",
                "07", "08", "09", "10", "11", "12"};
        String[] years  = {"2012", "2013", "2014", "2015", "2016",
                "2017", "2018", "2019", "2020", "2021",
                "2022", "2023", "2024", "2025", "2026"};

        fromMonthBox = new JComboBox<>(months);
        fromYearBox  = new JComboBox<>(years);

        toMonthBox = new JComboBox<>(months);
        toMonthBox.setSelectedIndex(11);
        toYearBox  = new JComboBox<>(years);
        toYearBox.setSelectedIndex(years.length - 1);

        filterButton = new JButton("Apply Filter");
        filterButton.addActionListener(e -> applyFilter());

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("Hotel ID");
        model.addColumn("Hotel Name");
        model.addColumn("Year");
        model.addColumn("Month");
        model.addColumn("Rooms");
        model.addColumn("Used Rooms");
        model.addColumn("Beds");
        model.addColumn("Used Beds");

        table = new JTable();
        table.setModel(model);
    }

    private void addComponents() {
        //fill/formate the table with the data
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel filterPanel = new JPanel(new GridLayout(2, 7, 5, 5));

        filterPanel.add(new JLabel("Hotel:"));
        filterPanel.add(new JLabel("From Month:"));
        filterPanel.add(new JLabel("From Year:"));
        filterPanel.add(new JLabel(""));
        filterPanel.add(new JLabel("To Month:"));
        filterPanel.add(new JLabel("To Year:"));
        filterPanel.add(new JLabel(""));

        filterPanel.add(hotelDropdown);
        filterPanel.add(fromMonthBox);
        filterPanel.add(fromYearBox);
        filterPanel.add(new JLabel("  ->  "));
        filterPanel.add(toMonthBox);
        filterPanel.add(toYearBox);
        filterPanel.add(filterButton);

        JScrollPane scrollPane = new JScrollPane(table);

        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void applyFilter() {
        model.setRowCount(0);

        int selectedIndex = hotelDropdown.getSelectedIndex();
        int selectedHotelId = -1;
        if (selectedIndex != 0) {
            selectedHotelId = hotels.get(selectedIndex - 1).id;
        }

        String fromMonthText = (String) fromMonthBox.getSelectedItem();
        String fromYearText  = (String) fromYearBox.getSelectedItem();
        String toMonthText   = (String) toMonthBox.getSelectedItem();
        String toYearText    = (String) toYearBox.getSelectedItem();

        int fromMonth = Integer.parseInt(fromMonthText);
        int fromYear  = Integer.parseInt(fromYearText);
        int toMonth   = Integer.parseInt(toMonthText);
        int toYear    = Integer.parseInt(toYearText);

        int fromDate = fromYear * 100 + fromMonth;
        int toDate   = toYear   * 100 + toMonth;

        for (Occupancies o : occupancies) {

            boolean hotelMatch = false;
            if (selectedHotelId == -1) {
                hotelMatch = true;
            } else {
                if (o.id == selectedHotelId) {
                    hotelMatch = true;
                }
            }

            boolean dateMatch = false;
            int recordDate = o.year * 100 + o.month;
            if (recordDate >= fromDate) {
                if (recordDate <= toDate) {
                    dateMatch = true;
                }
            }

            if (hotelMatch == true) {
                if (dateMatch == true) {

                    String hotelName = "Unknown";
                    for (Hotel h : hotels) {
                        if (h.id == o.id) {
                            hotelName = h.name;
                            break;
                        }
                    }

                    model.addRow(new Object[]{
                            o.id, hotelName, o.year, o.month,
                            o.rooms, o.usedRooms, o.beds, o.usedBeds
                    });
                }
            }
        }
    }

    /*
     * Reads hotels from hotels.txt and fills the hotels list.
     * Skips the first line (header) and removes quotes from each field.
     * Same Scanner approach as HelloWorld.java.
     */
    private void loadHotels() {
        hotels = new ArrayList<>();

        File file = new File("src/main/resources/hotels.txt");
        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
            scanner.nextLine(); // Skip header line: id,category,name,...

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                // remove all quotes from the line
                // the "/" could lead to splitting problems during the read
                line = line.replace("\"", "");

                // split by comma to get each field
                // as an alternative to rplace "," with ";" in the txt
                String[] parts = line.split(",");

                // hotels with not enough data types will not be contained
                // else these hotels would set free a cascade which
                // would shift all lines following them
                if (parts.length >= 11) {
                    Hotel h = new Hotel();
                    h.id       = Integer.parseInt(parts[0].trim());
                    h.category = parts[1].trim();
                    h.name     = parts[2].trim();
                    h.owner    = parts[3].trim();
                    h.contact  = parts[4].trim();
                    h.address  = parts[5].trim();
                    h.city     = parts[6].trim();
                    h.cityCode = parts[7].trim();
                    // parts[8] = phone (not in Hotel class)
                    h.noRooms  = Integer.parseInt(parts[9].trim());
                    h.noBeds   = Integer.parseInt(parts[10].trim());
                    hotels.add(h);
                }
            }
        //failsafe if the hotel txt is not present
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "hotels.txt not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


     // Same scanner as before only now form the occupancies txt

    private void loadOccupancies() {
        occupancies = new ArrayList<>();

        File file = new File("src/main/resources/occupancies.txt");
        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
            scanner.nextLine(); // Skip header

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                if (parts.length >= 7) {
                    Occupancies o = new Occupancies();
                    o.id       = Integer.parseInt(parts[0].trim());
                    o.rooms    = Integer.parseInt(parts[1].trim());
                    o.usedRooms = Integer.parseInt(parts[2].trim());
                    o.beds     = Integer.parseInt(parts[3].trim());
                    o.usedBeds = Integer.parseInt(parts[4].trim());
                    o.year     = Integer.parseInt(parts[5].trim());
                    o.month    = Integer.parseInt(parts[6].trim());
                    occupancies.add(o);
                }
            }
        //same failsafe as before
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "occupancies.txt not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}