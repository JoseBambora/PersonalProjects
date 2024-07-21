package org.botgverreiro.facade;

import org.botgverreiro.bot.threads.MyLocks;
import org.botgverreiro.dao.utils.EnvDB;
import org.botgverreiro.model.classes.Game;
import org.botgverreiro.model.classes.Season;
import org.botgverreiro.model.classes.User;
import org.botgverreiro.model.enums.Mode;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A normal facade. Every data access passes here.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class Facade {
    private final Map<Mode, GameMode> gameMode;
    private final List<Mode> openMods;

    public Facade(boolean test) {
        gameMode = new HashMap<>();
        openMods = new ArrayList<>();
        if (test) {
            gameMode.put(Mode.FOOTBALL, new GameMode(EnvDB.database_test_football));
            gameMode.put(Mode.FUTSAL, new GameMode(EnvDB.database_test_futsal));
            gameMode.put(Mode.NATIONAL, new GameMode(EnvDB.database_test_portugal));
        } else {
            gameMode.put(Mode.FOOTBALL, new GameMode(EnvDB.database_football));
            gameMode.put(Mode.FUTSAL, new GameMode(EnvDB.database_futsal));
            gameMode.put(Mode.NATIONAL, new GameMode(EnvDB.database_portugal));
        }
    }

    /**
     * Similar to <b>addGame</b>. Add a list of games. This function is just used for tests.
     *
     * @param games List of games to add.
     * @return 0 if everything happened successfully, 1 otherwise.
     */
    public int addGames(List<Game> games) {
        return games.stream().map(this::addGame).reduce(0, (acc, res) -> acc | res);
    }

    /**
     * Similar to <b>addGames</b>, but instead of adding multiple games, add just a single game.
     *
     * @param game Game to add.
     * @return 0 if everything happened successfully, 1 otherwise.
     * @see GameMode
     */
    public int addGame(Game game) {
        return gameMode.get(game.mode()).addGame(game);
    }

    /**
     * Open bets to any games that are on their game day for all game modes.
     *
     * @param now The current time.
     * @return The list of games that are open to users predictions.
     * @see GameMode
     */
    public List<Game> openBets(LocalDateTime now) {
        MyLocks.getInstance().lockWrite("openMods");
        List<Game> res = gameMode.values().stream().map(gm -> gm.open(now)).filter(Objects::nonNull).sorted(Game::sort).toList();
        openMods.addAll(res.stream().map(Game::mode).toList());
        MyLocks.getInstance().unlockWrite("openMods");
        return res;
    }


    /**
     * Close bets for the games that have are open for bets and already started. Calling for all game modes.
     *
     * @param now The current time.
     * @return The list of games that closes users predictions.
     * @see GameMode
     */
    public List<Game> closeBets(LocalDateTime now) {
        MyLocks.getInstance().lockWrite("openMods");
        List<Game> res = gameMode.values().stream().map(gm -> gm.close(now)).filter(Objects::nonNull).sorted(Game::sort).toList();
        openMods.removeAll(res.stream().map(Game::mode).toList());
        MyLocks.getInstance().unlockWrite("openMods");
        return res;
    }

    /**
     * Slash command <b>/win</b>. Basically associate a result to a game a returns a list of mention winners.
     *
     * @param homeGoals Home goals.
     * @param awayGoals Away goals.
     * @param mode      Modality.
     * @return List of mention winners.
     * @see GameMode
     */
    public List<String> win(int homeGoals, int awayGoals, Mode mode) {
        List<String> winners = gameMode.get(mode).win(homeGoals, awayGoals);
        return winners == null ? null : winners.stream().sorted().toList();
    }

    /**
     * Just for debug purposes. It gets the first game of the mode that is waiting for a result.
     *
     * @param mode Modality.
     * @return The first game for modality mode that is waiting for result.
     * @see GameMode
     */
    public Game getFstWaitingResultGame(Mode mode) {
        return gameMode.get(mode).getFstWaitingResultGame();
    }

    /**
     * Adds a user prediction.
     *
     * @param mention   User mention.
     * @param name      Username.
     * @param homeGoals Home goals for all open games.
     * @param awayGoals Away goals for all open games.
     * @return 2 if something is wrong with the input, 1 if the user have already predicted before and 0 if it is the first user prediction.
     * @see GameMode
     */
    public int newBet(String mention, String name, List<Integer> homeGoals, List<Integer> awayGoals) {
        MyLocks.getInstance().lockRead("openMods");
        int res = 2;
        if (homeGoals.size() == awayGoals.size() && openMods.size() == homeGoals.size()) {
            res = 0;
            for (int i = 0; i < openMods.size(); i++) {
                Mode mode = openMods.get(i);
                int homeGoal = homeGoals.get(i);
                int awayGoal = awayGoals.get(i);
                res |= gameMode.get(mode).newBet(mention, name, homeGoal, awayGoal);
            }
        }
        MyLocks.getInstance().unlockRead("openMods");
        return res;
    }

    /**
     * Just a function that compares the two users, in order to sort them.
     * Firstly the difference between points is considered.
     * If the points are the same,total predictions is considered.
     * If both factors above are equal (differences == 0), users mentions are considered.
     *
     * @param u1 User 1.
     * @param u2 User 2.
     * @return Users comparison.
     * @see User
     */
    private int sortMethod(User u1, User u2) {
        int difPoints = u2.getTotalPoints() - u1.getTotalPoints();
        int difPredictions = u2.getTotalPredictions() - u1.getTotalPredictions();
        return difPoints != 0 ? difPoints : difPredictions != 0 ? difPredictions : u1.getMention().compareTo(u2.getMention());
    }

    /**
     * Get the classification for a specific modality and season.
     * If season is null, last season is considered. If Mode is None, all modalities are considered.
     *
     * @param mode   Modality.
     * @param season Season ID.
     * @return Classification.
     * @see GameMode
     */
    public List<User> classification(Mode mode, String season) {
        if (mode == Mode.NONE) {
            AtomicInteger position = new AtomicInteger(0);
            AtomicInteger previousPoints = new AtomicInteger(-1);
            return gameMode.values()
                    .stream()
                    .flatMap(gm -> gm.getClassification(season).stream())
                    .collect(Collectors.toMap(
                            User::getMention,
                            Function.identity(),
                            (u1, u2) -> u1.joinData(u2.getTotalPredictions(), u2.getTotalPoints())
                    ))
                    .values()
                    .stream()
                    .sorted(this::sortMethod)
                    .map(u -> u.setPosition(previousPoints.getAndSet(u.getTotalPoints()) == u.getTotalPoints() ? position.get() : position.incrementAndGet()))
                    .toList();
        } else
            return gameMode.get(mode).getClassification(season);
    }

    /**
     * Adds a new season for all game modes.
     *
     * @return 0 if success, 1 otherwise.
     * @see GameMode
     */
    public int newSeason() {
        return gameMode.values().stream().map(GameMode::newSeason).reduce(0, (acc, p) -> acc | p);
    }

    /**
     * Deletes a user for all game modes.
     *
     * @return Greater than 0 if success, 0 otherwise.
     * @see GameMode
     */
    public int deleteUser(String user) {
        return gameMode.values().stream().map(gm -> gm.deleteUser(user)).reduce(0, (acc, v) -> acc | v);
    }

    /**
     * Just an auxiliary function that sums all the values of a specific field of a Season from a list.
     *
     * @param values   List with the data type.
     * @param function Function to convert the data type to integer.
     * @return The final sum.
     */
    private int sumFieldValues(List<Season> values, Function<Season, Integer> function) {
        return values.stream().map(function).reduce(0, Integer::sum);
    }

    /**
     * Gets the statistics of a season.
     * If modality is null, all game modes are considered.
     * If Season is null, last season is considered.
     *
     * @param season Season ID.
     * @param mode   Modality.
     * @return Season statistics.
     * @see GameMode
     */
    public Season statsSeason(String season, Mode mode) {
        if (mode == Mode.NONE) {
            List<Season> seasons = gameMode.values().stream().map(gm -> gm.statsSeason(season)).filter(Objects::nonNull).toList();
            if (seasons.isEmpty())
                return null;
            int games = sumFieldValues(seasons, Season::getGames);
            int scored = sumFieldValues(seasons, Season::getScored);
            int conceded = sumFieldValues(seasons, Season::getConceded);
            int totalPredictions = sumFieldValues(seasons, Season::getTotalPredictions);
            int correct = sumFieldValues(seasons, Season::getCorrectPredictions);
            int totalWins = sumFieldValues(seasons, Season::getTotalWins);
            int totalDraws = sumFieldValues(seasons, Season::getTotalDraws);
            int totalLoses = sumFieldValues(seasons, Season::getTotalLoses);
            return new Season(seasons.getFirst().getSeason(), games, scored, conceded, totalPredictions, correct, totalWins, totalDraws, totalLoses);
        } else
            return gameMode.get(mode).statsSeason(season);
    }

    /**
     * Statistics of a user for all Modalities. If season is null, last season is considered.
     *
     * @param mention User mention.
     * @param season  Season ID.
     * @return User with all its stats.
     */
    private User statsUserAllModes(String mention, String season) {
        List<User> userList = classification(Mode.NONE, season);
        int index = userList.indexOf(new User(mention));
        return index != -1 ? userList.get(index) : null;
    }

    /**
     * Stats of a user.
     * If modality is null, all game modes are considered.
     * If Season is null, last season is considered.
     *
     * @param mention User mention.
     * @param mode    Modality.
     * @param season  Season ID.
     * @return User with all its stats.
     * @see GameMode
     */
    public User statsUser(String mention, Mode mode, String season) {
        return mode == Mode.NONE ? statsUserAllModes(mention, season) : gameMode.get(mode).statsUser(mention, season);
    }

    /**
     * Gets a list of games that already ended at a specific moment.
     *
     * @param now Current time.
     * @return A list of games that already ended.
     * @see GameMode
     */
    public List<Game> endedGames(LocalDateTime now) {
        return gameMode.values().stream().flatMap(gm -> gm.alreadyEnded(now).stream()).sorted(Game::sort).toList();
    }

    /**
     * Just gets the next games that are schedule.
     *
     * @return A list of games that are schedule.
     * @see GameMode
     */
    public List<Game> nextGames() {
        return gameMode.values().stream().map(GameMode::nextGames).flatMap(List::stream).sorted(Game::sort).toList();
    }

    /**
     * Removes a game from a Modality.
     *
     * @param mode     Modality.
     * @param opponent Opponent.
     * @param index    Index, for cases when we have two schedule games for the same opponent in the same modality.
     * @return The removed game.
     * @see GameMode
     */
    public Game removeGame(Mode mode, String opponent, int index) {
        return gameMode.get(mode).removeGame(opponent, index);
    }

    /**
     * Gets a list of games that are open for new predictions, for all modalities.
     *
     * @return A list of games that are open for new predictions
     * @see GameMode
     */
    public List<Game> openGamesInfo() {
        return gameMode.values().stream().map(GameMode::getCurrentGame).filter(Objects::nonNull).sorted(Game::sort).toList();
    }

    /**
     * Gets the ongoing games at a specific moment.
     *
     * @param now Current time.
     * @return A list of ongoing games.
     * @see GameMode
     */
    public List<Game> onGoingGames(LocalDateTime now) {
        return gameMode.values().stream().flatMap(gm -> gm.onGoingGames(now).stream()).sorted(Game::sort).toList();
    }
}

