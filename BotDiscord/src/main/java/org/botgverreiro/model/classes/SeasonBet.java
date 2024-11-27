package org.botgverreiro.model.classes;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.util.Objects;

public class SeasonBet {
    private final String userMention;
    private final String username;
    private final int leaguePosition;
    private final String europeCompetition;
    private final int europePosition;
    private final int cupTPPosition;
    private final int cupTLPosition;

    private final String starPlayer;
    private final String surprisePlayer;
    private final String worstPlayer;
    private final String revelationPlayer;

    public SeasonBet(String userMention, String username, int leaguePosition, String europeCompetition, int europePosition, int cupTPPosition, int cupTLPosition, String starPlayer, String surprisePlayer, String worstPlayer, String revelationPlayer) {
        this.userMention = userMention;
        this.username = username;
        this.leaguePosition = leaguePosition;
        this.europeCompetition = europeCompetition;
        this.europePosition = europePosition;
        this.cupTPPosition = cupTPPosition;
        this.cupTLPosition = cupTLPosition;
        this.starPlayer = starPlayer;
        this.surprisePlayer = surprisePlayer;
        this.worstPlayer = worstPlayer;
        this.revelationPlayer = revelationPlayer;
    }

    public SeasonBet() {
        this.userMention = null;
        this.username = null;
        this.leaguePosition = -1;
        this.europeCompetition = null;
        this.europePosition = -1;
        this.cupTPPosition = -1;
        this.cupTLPosition = -1;
        this.starPlayer = null;
        this.surprisePlayer = null;
        this.worstPlayer = null;
        this.revelationPlayer = null;
    }

    public static ObjectReader getReaderCSV() {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(SeasonBet.class).withHeader();
        return mapper.readerFor(SeasonBet.class).with(schema);
    }

    public static ObjectWriter getWriterCSV() {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(SeasonBet.class).withHeader();
        return mapper.writerFor(SeasonBet.class).with(schema);
    }

    public String getUserMention() {
        return userMention;
    }

    public String getUsername() {
        return username;
    }

    public int getLeaguePosition() {
        return leaguePosition;
    }

    public String getEuropeCompetition() {
        return europeCompetition;
    }

    public int getEuropePosition() {
        return europePosition;
    }

    public int getCupTPPosition() {
        return cupTPPosition;
    }

    public int getCupTLPosition() {
        return cupTLPosition;
    }

    public String getStarPlayer() {
        return starPlayer;
    }

    public String getSurprisePlayer() {
        return surprisePlayer;
    }

    public String getWorstPlayer() {
        return worstPlayer;
    }

    public String getRevelationPlayer() {
        return revelationPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeasonBet seasonBet)) return false;
        return Objects.equals(userMention, seasonBet.userMention) && Objects.equals(username, seasonBet.username);
    }

    @Override
    public String toString() {
        return "SeasonBet(" + userMention +
                ", " + username +
                ", " + leaguePosition +
                ", " + europeCompetition +
                ", " + europePosition +
                ", " + cupTPPosition +
                ", " + cupTLPosition +
                ", " + starPlayer +
                ", " + surprisePlayer +
                ", " + worstPlayer +
                ", " + revelationPlayer +
                ')';
    }

    @Override
    public int hashCode() {
        return Objects.hash(userMention, username);
    }
}
