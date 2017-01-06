package com.bnymellon.hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Employee {

    private int    id;
    private String name;

    @Id
    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
