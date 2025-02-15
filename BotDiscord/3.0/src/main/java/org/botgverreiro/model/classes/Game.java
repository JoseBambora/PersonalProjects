package org.botgverreiro.model.classes;

import org.botgverreiro.model.enums.ConverterString;
import org.botgverreiro.model.enums.Field;
import org.botgverreiro.model.enums.Mode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Just a record / class that represents one game.
 * The only important to thing to mention is that endgame is automatically assigned to
 * 2.5 hours after the game has started. <b>For test purposes</b>, each game ends 30 second after starting.
 *
 * @param field     Game field.
 * @param opponent  Game opponent.
 * @param mode      Modality.
 * @param startGame The time when the game will start.
 * @param endGame   The time when the game will end.
 * @author JoséBambora
 * @version 1.0
 */
public record Game(Field field, String opponent, Mode mode, LocalDateTime startGame, LocalDateTime endGame) {

    public static Game buildGame(Field field, String opponent, Mode mode, LocalDateTime localDateTime) {
        return new Game(field, opponent, mode, localDateTime, localDateTime.plusHours(2).plusMinutes(30));
    }

    public static Game buildGameTest(Field field, String opponent, Mode mode, LocalDateTime localDateTime) {
        return new Game(field, opponent, mode, localDateTime, localDateTime.plusSeconds(30));
    }

    public int getScored(int homeGoals, int awayGoals) {
        return field == Field.AWAY ? awayGoals : homeGoals;
    }

    public int getConceded(int homeGoals, int awayGoals) {
        return field == Field.AWAY ? homeGoals : awayGoals;
    }

    public String toString(boolean day, boolean markdown) {
        String team = mode == Mode.NATIONAL ? "Portugal" : "SC Braga";
        String fieldStr = field == Field.AWAY ? opponent + " - " + team : team + " - " + opponent;
        fieldStr = markdown ? "**" + fieldStr + "**" : fieldStr;
        String dayStr = day ? startGame.format(DateTimeFormatter.ofPattern("d/M H:mm")) : startGame.format(DateTimeFormatter.ofPattern("H:mm"));
        String modeStr = ConverterString.toStringMode(mode);
        return fieldStr + " (" + modeStr + ") (" + dayStr + ")" + startGame + " " + endGame;
    }

    @Override
    public String toString() {
        return toString(true, false);
    }

    public boolean hasStarted(LocalDateTime now) {
        return startGame.isBefore(now);
    }

    public boolean hasEnded(LocalDateTime now) {
        return endGame.isBefore(now);
    }

    private boolean sameDay(LocalDateTime date1, LocalDateTime date2) {
        return date1.getDayOfMonth() == date2.getDayOfMonth() && date1.getMonth() == date2.getMonth() && date1.getYear() == date2.getYear();
    }

    public boolean isToday(LocalDateTime now) {
        return sameDay(startGame, now);
    }

    public boolean sameDay(Game game) {
        return sameDay(game.startGame(), this.startGame);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(opponent, game.opponent) && mode == game.mode && field == game.field && sameDay(startGame, game.startGame) && sameDay(endGame, game.endGame);
    }

    public int sort(Game game) {
        return this.startGame.isBefore(game.startGame) ? -1 : 1;
    }

    public Game clone() {
        return new Game(field, opponent, mode, startGame, endGame);
    }
}
