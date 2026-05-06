import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hotelnew")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotelnew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "owner", nullable = false, length = 100)
    private String owner;

    @Column(name = "phone", unique = true, length = 150)
    private String phone;

    @Column(name = "city", nullable = false, length = 100)
    private String city;
}