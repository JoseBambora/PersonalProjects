package org.botgverreiro.model.classes;

/**
 * Just a class/record for predictions.
 *
 * @param user      User mention.
 * @param homeGoals Home goals.
 * @param awayGoals Away goals.
 * @param gameDay   Game day.
 * @author JoséBambora
 * @version 1.0
 */
public record Prediction(String user, int homeGoals, int awayGoals, int gameDay) {
    public boolean isRight(int homeGoals, int awayGoals) {
        return this.homeGoals == homeGoals && this.awayGoals == awayGoals;
    }

    @Override
    public int hashCode() {
        return user.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prediction bet)) return false;
        return bet.user.equals(user);
    }

    @Override
    public String toString() {
        return String.format("Bet(%s,%d,%d)", user, homeGoals, awayGoals);
    }
}
