package com.backend.tasks.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

/**
 * Implement entity:
 * 1. Map to organization
 * 2. Add equals and hashCode methods
 */
@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;

    /**
     * Map user with organization by org_id field.
     * Use ManyToOne association.
     */
    @ManyToOne
    @JoinColumn(name = "org_id")
    private Organization organization;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(organization, user.organization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, organization);
    }
}
