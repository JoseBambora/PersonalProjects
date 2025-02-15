package org.botgverreiro.model.classes;

import java.util.Objects;

/**
 * Users class. Nothing special.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class User {
    private final String mention;
    private final String name;
    private int totalPredictions;
    private int totalPoints;
    private int position;

    public User(String mention, String name, int totalPredictions, int totalPoints, int position) {
        this.mention = mention;
        this.name = name;
        this.totalPredictions = totalPredictions;
        this.totalPoints = totalPoints;
        this.position = position;
    }

    public User(String mention) {
        this.mention = mention;
        this.name = "";
        this.totalPredictions = 0;
        this.totalPoints = 0;
        this.position = 0;
    }

    @Override
    public int hashCode() {
        return mention.hashCode();
    }


    @Override
    public String toString() {
        return String.format("User(%s,%s,%d,%d)", mention, name, totalPredictions, totalPoints);
    }

    public String getMention() {
        return mention;
    }

    public String getName() {
        return name;
    }

    public int getTotalPredictions() {
        return totalPredictions;
    }

    public int getTotalPoints() {
        return totalPoints;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(mention, user.mention);
    }

    public User joinData(int totalPredictions, int totalPoints) {
        this.totalPoints += totalPoints;
        this.totalPredictions += totalPredictions;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public User setPosition(int position) {
        this.position = position;
        return this;
    }
}
