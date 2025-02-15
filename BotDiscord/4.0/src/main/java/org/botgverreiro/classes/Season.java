package org.botgverreiro.classes;

import jakarta.persistence.*;

public class Season {
    @Column(name = "SEASON_ID")
    private int seasonId;

    private Mode mode;
    @Column(name = "SEASON_NAME")
    private String seasonName;

    public Season setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    @Override
    public String toString() {
        return "("  + seasonId + "," + seasonName  + ") Modo " + mode;
    }
}
