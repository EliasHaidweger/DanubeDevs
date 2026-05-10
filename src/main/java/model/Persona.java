package model;

import lombok.Data;

/**
 * Repraesentiert eine Persona aus dbo.persona.
 */
@Data
public class Persona {

    public static final String ROLE_SENIOR = "SENIOR";
    public static final String ROLE_HEAD   = "HEAD";
    public static final String ROLE_HOTEL  = "HOTEL";

    private int id;
    private String username;
    private String password;
    private String role;
    private boolean canDelete;

    public String getRoleLabel() {
        if (ROLE_SENIOR.equals(role)) return "Senior User";
        if (ROLE_HEAD.equals(role))   return "Head of NOE-TO";
        if (ROLE_HOTEL.equals(role))  return "Hotel Representative";
        return role;
    }
}
