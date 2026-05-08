package trial;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trial.Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mrs;

    private String name;
    private int age;
    private Benutzerrolle role;
}
enum Benutzerrolle {
    SENIOR, JUNIOR, ADMIN
}
