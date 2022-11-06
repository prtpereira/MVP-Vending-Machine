package com.kabisa.vendingmachine.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;
import static javax.persistence.GenerationType.AUTO;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = AUTO)
    private UUID id;
    @Column(unique=true)
    private String name;

    public Role() {
        super();
    }

    public Role(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEquals(Role role) {
        return (this.name.equals(role.getName()));
    }
}
