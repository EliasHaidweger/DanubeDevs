import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data

public class Occupancies {

    int id;
    int rooms;
    int usedRooms;
    int beds;
    int usedBeds;
    int year;
    int month;
}
