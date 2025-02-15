package org.botgverreiro.classes;

import jakarta.persistence.Column;

public class Mode {
    @Column(name = "MODE_ID")
    private int id;
    @Column(name = "MODE_NAME")
    private String name;

    @Override
    public String toString() {
        return "("  + id+ "," + name  + ")";

    }
}