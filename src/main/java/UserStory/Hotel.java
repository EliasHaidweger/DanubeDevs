package UserStory;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data model representing a Hotel entity.
 * Uses Lombok to generate getters, setters, toString and constructors.
 */
@Data
@AllArgsConstructor
public class Hotel {

    private int id;
    private String category;
    private String name;
    private String owner;
    private String contact;
    private String address;
    private String city;
    private String cityCode;
    private String phone;
    private int noRooms;
    private int noBeds;

}
