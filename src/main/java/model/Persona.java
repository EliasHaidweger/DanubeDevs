package model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Hibernate entity for the persona table.
 *
 * can_delete (a bit in the database) is mapped to the boolean variable canDelete.
 */
@Entity
@Table(name = "persona")
@Data
public class Persona {

    public static final String ROLE_SENIOR = "SENIOR";
    public static final String ROLE_HEAD   = "HEAD";
    public static final String ROLE_HOTEL  = "HOTEL";

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "can_delete")
    private boolean canDelete;

    public String getRoleLabel() {
        if (ROLE_SENIOR.equals(role)) return "Senior User";
        if (ROLE_HEAD.equals(role))   return "Head of NOE-TO";
        if (ROLE_HOTEL.equals(role))  return "Hotel Representative";
        return role;
    }
}

