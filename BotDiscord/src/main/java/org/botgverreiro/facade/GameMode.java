package org.botgverreiro.facade;

import org.botgverreiro.bot.threads.MyLocks;
import org.botgverreiro.dao.repositories.PositionsRepo;
import org.botgverreiro.dao.repositories.PredictionsRepo;
import org.botgverreiro.dao.repositories.SeasonRepo;
import org.botgverreiro.model.classes.Game;
import org.botgverreiro.model.classes.Prediction;
import org.botgverreiro.model.classes.Season;
import org.botgverreiro.model.classes.User;
import org.jooq.Configuration;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.botgverreiro.dao.utils.Transactions.getData;

/**
 * Just a class responsible for a single modality. This is the class that realizes the database access.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class GameMode {
    private final String dbName;
    private final PositionsRepo positionsRepo;
    private final SeasonRepo seasonRepo;
    private final PredictionsRepo predictionsRepo;
    private final List<Game> nextGames;
    private final List<Game> waitingResult;
    private boolean open;
    private Game currentGame = null;

    public GameMode(String dbName) {
        this.dbName = dbName;
        positionsRepo = new PositionsRepo(dbName);
        seasonRepo = new SeasonRepo(dbName);
        predictionsRepo = new PredictionsRepo(dbName);
        nextGames = new LinkedList<>();
        waitingResult = new LinkedList<>();
        open = false;
    }

    /**
     * Adds a new game.
     *
     * @param game Game to add.
     * @return 0 if success, 1 otherwise.
     * @see MyLocks
     */
    public int addGame(Game game) {
        MyLocks.getInstance().lockWrite("nextGames");
        MyLocks.getInstance().lockRead("currentGame");
        int res = 1;
        if ((currentGame == null || !currentGame.sameDay(game)) && nextGames.stream().noneMatch(g -> g.sameDay(game))) {
            int i = 0;
            for (; i < nextGames.size(); i++)
                if (nextGames.get(i).sort(game) > 0)
                    break;
            nextGames.add(i, game.clone());
            res = 0;
        }
        MyLocks.getInstance().unlockWrite("nextGames");
        MyLocks.getInstance().unlockRead("currentGame");
        return res;
    }

    /**
     * Opens a game for a specific moment. We cannot have more than 1 game open in each moment.
     * If there is not a game that was open, the return is null.
     *
     * @param now Current time.
     * @return Opened game.
     */
    public Game open(LocalDateTime now) {
        Game res = null;
        MyLocks.getInstance().lockWrite("nextGames", "currentGame", "open");
        if (!nextGames.isEmpty() && nextGames.getFirst().isToday(now)) {
            currentGame = nextGames.removeFirst();
            open = true;
            res = currentGame;
        }
        MyLocks.getInstance().unlockWrite("nextGames", "currentGame", "open");
        return res;
    }

    /**
     * Closes a game for a specific moment.
     * If there is not an open game, the return is null.
     *
     * @param now Current time.
     * @return Closed game.
     */
    public Game close(LocalDateTime now) {
        Game res = null;
        MyLocks.getInstance().lockWrite("waitingResults", "currentGame", "open");
        if (open && currentGame != null && currentGame.hasStarted(now)) {
            this.open = false;
            res = currentGame.clone();
            waitingResult.add(res);
            currentGame = null;
        }
        MyLocks.getInstance().unlockWrite("waitingResults", "currentGame", "open");
        return res;
    }

    /**
     * Adds a new bet.
     *
     * @param mention   User mention.
     * @param name      Username.
     * @param homeGoals Home goals.
     * @param awayGoals Away goals.
     * @return 2 if there is not a game with open bets, 1 if anything has happened, 0 if success.
     * @see PositionsRepo
     * @see org.botgverreiro.dao.utils.Transactions
     */
    public int newBet(String mention, String name, int homeGoals, int awayGoals) {
        MyLocks.getInstance().lockRead("open");
        int res = 2;
        if (open)
            res = getData(dbName, configuration -> {
                positionsRepo.insertUser(configuration, mention, name, seasonRepo.getLastSeason(configuration).getSeason());
                return predictionsRepo.insertOrUpdatePrediction(configuration, new Prediction(mention, homeGoals, awayGoals, currentGame.startGame().getDayOfMonth()));
            }, 1);
        MyLocks.getInstance().unlockRead("open");
        return res;
    }

    /**
     * Auxiliary function to updates a specific season.
     * This function is used with the slash command <b>/win</b>, in order to update seasons stats.
     *
     * @param configuration Database configuration.
     * @param scored        Scored goals for a game.
     * @param conceded      Goals conceded.
     * @param plSize        Number of predictions.
     * @param wuSize        Number of winners.
     * @param points        Points gotten from the game (win 3 points, draw 1 point, loss 0 point)-
     * @return Season ID.
     * @see SeasonRepo
     * @see Season
     */
    private String updateSeason(Configuration configuration, int scored, int conceded, int plSize, int wuSize, int points) {
        Season season = seasonRepo.getLastSeason(configuration);
        season.endGame(scored, conceded, plSize, wuSize, points);
        seasonRepo.updateSeason(configuration, season);
        return season.getSeason();
    }

    /**
     * Increments the points for the winners and increments predictions column for all the users that made predictions.
     *
     * @param configuration   Database configuration.
     * @param predictionUsers Set of the users that made predictions.
     * @param winnersUsers    Users that predicted correctly.
     * @param season          Season ID.
     * @see PositionsRepo
     */
    private void incrementPoints(Configuration configuration, Set<String> predictionUsers, Set<String> winnersUsers, String season) {
        Set<String> predictionUsersFilter = new HashSet<>(predictionUsers);
        predictionUsersFilter.removeIf(winnersUsers::contains);
        positionsRepo.incrementPoints(configuration, predictionUsersFilter, season, 1);
        positionsRepo.incrementPoints(configuration, winnersUsers, season, 3);
    }

    /**
     * Get the predictions from a game and delete them.
     *
     * @param configuration Game predictions.
     * @param gameDay       Game day.
     * @return A list with all the predictions.
     * @see PredictionsRepo
     */
    private List<Prediction> getPredictions(Configuration configuration, int gameDay) {
        List<Prediction> predictionList = predictionsRepo.getPredictions(configuration, gameDay);
        predictionsRepo.deletePredictions(configuration, gameDay);
        return predictionList;
    }

    /**
     * Function that gets the users that made the users that made the correct predictions.
     *
     * @param predictionList Predictions list.
     * @param homeGoals      Home goals.
     * @param awayGoals      Away goals.
     * @return An array with two lists. The first one is a list of the users mentions that made predictions. The second one is users mention that predicted correctly.
     */
    private Set<String>[] getPredictWinners(List<Prediction> predictionList, int homeGoals, int awayGoals) {
        Set<String> predictionUsers = predictionList.stream().map(Prediction::user).collect(Collectors.toSet());
        Set<String> winnersUsers = predictionList.stream()
                .filter(p -> p.isRight(homeGoals, awayGoals))
                .map(Prediction::user)
                .collect(Collectors.toSet());
        return new Set[]{predictionUsers, winnersUsers};
    }

    /**
     * Function associated with <b>/win</b> slash command.
     *
     * @param homeGoals Goals scored from home team.
     * @param awayGoals Goals scored from away team.
     * @return A list of string with the mentions of the winners.
     * @see org.botgverreiro.dao.utils.Transactions
     */
    public List<String> win(int homeGoals, int awayGoals) {
        MyLocks.getInstance().lockWrite("waitingResults");
        List<String> res = null;
        if (!waitingResult.isEmpty()) {
            Game game = waitingResult.removeFirst();
            MyLocks.getInstance().unlockWrite("waitingResults");
            res = getData(dbName, configuration -> {
                List<Prediction> predictionList = getPredictions(configuration, game.startGame().getDayOfMonth());
                Set<String>[] predictionsWinners = getPredictWinners(predictionList, homeGoals, awayGoals);
                Set<String> predictionUsers = predictionsWinners[0];
                Set<String> winnersUsers = predictionsWinners[1];
                int scored = game.getScored(homeGoals, awayGoals);
                int conceded = game.getConceded(homeGoals, awayGoals);
                int points = scored == conceded ? 1 : scored > conceded ? 3 : 0;
                String season = updateSeason(configuration, scored, conceded, predictionList.size(), winnersUsers.size(), points);
                incrementPoints(configuration, predictionUsers, winnersUsers, season);
                return new ArrayList<>(winnersUsers);
            }, null);
        } else
            MyLocks.getInstance().unlockWrite("waitingResults");
        return res;
    }

    /**
     * Gets the first game that is waiting for result. If there is not a single game, the return value is null.
     *
     * @return The first game that is waiting for result.
     */
    public Game getFstWaitingResultGame() {
        MyLocks.getInstance().lockRead("waitingResults");
        Game res = waitingResult.isEmpty() ? null : waitingResult.getFirst();
        MyLocks.getInstance().unlockRead("waitingResults");
        return res;
    }

    /**
     * Auxiliary function to get a season.
     * If season argument is null, it gets the last season, otherwise it gets the specified season.
     *
     * @param configuration Database configuration.
     * @param season        Season ID.
     * @return Season object.
     * @see Season
     * @see SeasonRepo
     */
    private Season getSeason(Configuration configuration, String season) {
        return season == null ? seasonRepo.getLastSeason(configuration) : seasonRepo.getSeason(configuration, season);
    }

    /**
     * Creates a new season
     *
     * @return 0 if success, 1 otherwise.
     * @see SeasonRepo
     * @see Season
     * @see org.botgverreiro.dao.utils.Transactions
     */
    public int newSeason() {
        return getData(dbName, configuration -> {
            Season season = seasonRepo.getLastSeason(configuration);
            if (season != null)
                seasonRepo.insertSeason(configuration, season.nextSeason());
            else {
                int year1 = Calendar.getInstance().get(Calendar.YEAR);
                int year2 = year1 + 1;
                String year = Integer.toString(year1).substring(2) + "/" + Integer.toString(year2).substring(2);
                seasonRepo.insertSeason(configuration, new Season(year));
            }
            return 0;
        }, 1);
    }

    /**
     * Deletes a user from the database.
     *
     * @param user User mention.
     * @return 0 if the user does not exist, greater than 0 if success, -1 if something happened.
     * @see PositionsRepo
     * @see org.botgverreiro.dao.utils.Transactions
     */
    public int deleteUser(String user) {
        return getData(dbName, configuration -> positionsRepo.deleteUser(configuration, user), -1);
    }

    /**
     * Gets a user list form a specific season. If season is null, then last season is considered.
     *
     * @param season        Season ID.
     * @param configuration Database configuration
     * @return The list of users.
     * @see PositionsRepo
     */
    private List<User> getUsers(String season, Configuration configuration) {
        Season seasonObj = getSeason(configuration, season);
        return seasonObj != null ? positionsRepo.getPositionsSeason(configuration, seasonObj.getSeason()) : Collections.emptyList();
    }

    /**
     * Gets the classification for a specific season. If season is null, then last season is considered.
     *
     * @param season Season ID.
     * @return Classification.
     */
    public List<User> getClassification(String season) {
        return getData(dbName, configuration -> getUsers(season, configuration), Collections.emptyList());
    }

    /**
     * Gets the stats for a specific season. If season is null, then last season is considered.
     *
     * @param season Season ID.
     * @return Season object.
     */
    public Season statsSeason(String season) {
        return getData(dbName, configuration -> getSeason(configuration, season), null);
    }

    /**
     * User stats for a specific season. If season is null, then last season is considered.
     *
     * @param mention User mention.
     * @param season  Season ID.
     * @return User object.
     * @see org.botgverreiro.dao.utils.Transactions
     */
    public User statsUser(String mention, String season) {
        return getData(dbName, configuration -> {
            List<User> userList = getUsers(season, configuration);
            int index = userList.indexOf(new User(mention));
            return index != -1 ? userList.get(index) : null;
        }, null);
    }

    /**
     * Gets a list of games that already ended.
     *
     * @param now Current time.
     * @return List of games that have ended.
     */
    public List<Game> alreadyEnded(LocalDateTime now) {
        MyLocks.getInstance().lockRead("waitingResults");
        List<Game> res = waitingResult.stream().filter(g -> g.hasEnded(now)).map(Game::clone).toList();
        MyLocks.getInstance().unlockRead("waitingResults");
        return res;
    }

    /**
     * List of ongoing games.
     *
     * @param now Current time.
     * @return List with ongoing games.
     */
    public List<Game> onGoingGames(LocalDateTime now) {
        MyLocks.getInstance().lockRead("waitingResults");
        List<Game> res = waitingResult.stream().filter(g -> !g.hasEnded(now)).map(Game::clone).toList();
        MyLocks.getInstance().unlockRead("waitingResults");
        return res;
    }

    /**
     * Removes a game.
     *
     * @param opponent Opponent name.
     * @param index    Index, useful for when we have two or more games with the same opponent.
     * @return The deleted game.
     */
    public Game removeGame(String opponent, int index) {
        MyLocks.getInstance().lockWrite("nextGames");
        List<Game> aux = nextGames.stream().filter(g -> g.opponent().equals(opponent)).toList();
        Game game = null;
        if (aux.size() > index && index >= 0) {
            game = aux.get(index);
            nextGames.remove(game);
        }
        MyLocks.getInstance().unlockWrite("nextGames");
        return game;
    }

    /**
     * Gets the current open game.
     *
     * @return Current open game.
     */
    public Game getCurrentGame() {
        MyLocks.getInstance().lockRead("currentGame");
        Game game = currentGame != null ? currentGame.clone() : null;
        MyLocks.getInstance().unlockRead("currentGame");
        return game;
    }

    /**
     * Gets the next schedule games.
     *
     * @return Next schedule games.
     */
    public List<Game> nextGames() {
        MyLocks.getInstance().lockRead("nextGames");
        List<Game> res = nextGames.stream().map(Game::clone).toList();
        MyLocks.getInstance().unlockRead("nextGames");
        return res;
    }
}
