package org.botgverreiro.model.classes;

import java.util.Objects;

/**
 * Class for a season. Nothing important to point out.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class Season {
    private final String season;
    private int games;
    private int scored;
    private int conceded;
    private int totalPredictions;
    private int correctPredictions;
    private int totalWins;
    private int totalLoses;
    private int totalDraws;

    public Season(String season, int games, int scored, int conceded, int totalPredictions, int correctPredictions, int totalWins, int totalDraws, int totalLoses) {
        this.season = season;
        this.games = games;
        this.scored = scored;
        this.conceded = conceded;
        this.totalPredictions = totalPredictions;
        this.correctPredictions = correctPredictions;
        this.totalDraws = totalDraws;
        this.totalLoses = totalLoses;
        this.totalWins = totalWins;
    }

    public Season(String season) {
        this.season = season;
        this.games = 0;
        this.scored = 0;
        this.conceded = 0;
        this.totalPredictions = 0;
        this.correctPredictions = 0;
        this.totalDraws = 0;
        this.totalLoses = 0;
        this.totalWins = 0;
    }

    public String getSeason() {
        return season;
    }

    public int getGames() {
        return games;
    }

    public int getScored() {
        return scored;
    }

    public int getConceded() {
        return conceded;
    }

    public int getTotalPredictions() {
        return totalPredictions;
    }

    public int getCorrectPredictions() {
        return correctPredictions;
    }

    public Season endGame(int scored, int conceded, int totalPredictions, int correctPredictions, int points) {
        this.scored += scored;
        this.conceded += conceded;
        this.totalPredictions += totalPredictions;
        this.correctPredictions += correctPredictions;
        this.games++;
        if (points == 3)
            this.totalWins++;
        else if (points == 1)
            this.totalDraws++;
        else
            this.totalLoses++;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Season season1)) return false;
        return games == season1.games && scored == season1.scored && conceded == season1.conceded && totalPredictions == season1.totalPredictions && correctPredictions == season1.correctPredictions && Objects.equals(season, season1.season);
    }

    @Override
    public String toString() {
        return String.format("Season(%s,%d,%d,%d,%d,%d,%d,%d,%d)", season, games, scored, conceded, totalPredictions, correctPredictions, totalWins, totalDraws, totalLoses);
    }

    public Season nextSeason() {
        String[] parts = season.split("/");
        int firstNumber = Integer.parseInt(parts[0]) + 1;
        int secondNumber = Integer.parseInt(parts[1]) + 1;
        return new Season(String.format("%s/%s", firstNumber, secondNumber));
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getTotalLoses() {
        return totalLoses;
    }

    public int getTotalDraws() {
        return totalDraws;
    }
}
