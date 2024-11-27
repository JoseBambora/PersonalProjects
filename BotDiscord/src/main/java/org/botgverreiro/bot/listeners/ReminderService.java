package org.botgverreiro.bot.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.botgverreiro.ParserInfo;
import org.botgverreiro.bot.frontend.MessageSender;
import org.botgverreiro.bot.utils.Templates;
import org.botgverreiro.facade.Facade;
import org.botgverreiro.model.classes.Game;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class handles reminders, so that the admin does not forget to schedule games and result.
 * In addition to that, this class is also responsible for opening and closing predictions.
 *
 * @author JoséBambora
 * @version 1.0
 * @see Facade
 */
public class ReminderService extends ListenerAdapter {
    private final Facade facade;
    private final ScheduledExecutorService scheduler;
    private TextChannel channelBets;
    private TextChannel channelReminder;

    public ReminderService(Facade facade) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.facade = facade;
    }

    /**
     * Auxiliary method to schedule an alarm using the ScheduledExecutorService class variable.
     * It is important to note that the alarm is set based on seconds.
     *
     * @param runnable The task to execute.
     * @param now      The current time.
     * @param limit    The time at which we want the alarm to trigger.
     */
    private void createStaticScheduler(Runnable runnable, LocalDateTime now, LocalDateTime limit) {
        scheduler.schedule(runnable, ChronoUnit.SECONDS.between(now, limit.plusMinutes(1)), TimeUnit.SECONDS);
    }

    /**
     * Auxiliary method to manage the scheduling of alarms for games.
     * This method retrieves LocalDateTime instances from games, removes duplicate dates, sorts them, and returns a list.
     * The main goal of this method is to schedule alarms such that if there are two different games for which we want to
     * schedule an alarm at the same time, only one alarm is created; otherwise, separate alarms are created.
     *
     * @param games The games for which to schedule alarms.
     * @param fun   The function to get a LocalDateTime from a Game. This function will be either getStartGame or getEndGame
     *              from the Game class.
     * @return A list of unique LocalDateTimes to schedule alarms.
     * @see Game
     */

    private List<LocalDateTime> getAlarms(List<Game> games, Function<Game, LocalDateTime> fun) {
        return games.stream().map(fun).collect(Collectors.toSet()).stream().sorted().toList();
    }

    /**
     * Manages the opening and closing of predictions for the bot.
     * When all predictions are closed, only moderators can send messages; otherwise, anyone can send messages.
     *
     * @param canWrite Indicates if normal users can send messages to channelBets.
     *                 In other words, if there is still a game with open predictions.
     */
    private void changePermissionsWrite(boolean canWrite) {
        if (canWrite)
            channelBets.getGuild().getRoles().forEach(r -> channelBets.upsertPermissionOverride(r).setAllowed(Permission.MESSAGE_SEND).queue());
        else {
            List<Role> rolesDeny = channelBets.getGuild().getRoles().stream().filter(r -> !r.hasPermission(Permission.MANAGE_SERVER)).toList();
            List<Role> rolesAllow = channelBets.getGuild().getRoles().stream().filter(r -> r.hasPermission(Permission.MANAGE_SERVER)).toList();
            rolesDeny.forEach(r -> channelBets.upsertPermissionOverride(r).setDenied(Permission.MESSAGE_SEND).queue());
            rolesAllow.forEach(r -> channelBets.upsertPermissionOverride(r).setAllowed(Permission.MESSAGE_SEND).queue());
        }
    }

    /**
     * Handles the opening of predictions.
     * This method is called once every day.
     *
     * @see Game
     * @see Facade
     */
    private void openBets() {
        LocalDateTime now = LocalDateTime.now();
        List<Game> openGames = facade.openBets(now);
        System.out.println("Open Bets acordou. Open games: " + openGames);
        if (!openGames.isEmpty()) {
            if (facade.closeSeasonsBets() == 0)
                MessageSender.sendMessage(channelBets, Templates.messageSeasonBetsClose());
            changePermissionsWrite(true);
            MessageSender.sendMessage(channelBets, Templates.messageOpenBets(openGames));
            List<LocalDateTime> alarms = getAlarms(openGames, Game::startGame);
            System.out.println("Alarmes para close bets: " + alarms);
            alarms.forEach(d -> createStaticScheduler(this::closeBets, now, d));
        }
    }

    /**
     * Handles the closing of predictions.
     * This method is called based on the alarms scheduled by the <i>openBets</i> method.
     *
     * @see Game
     * @see Facade
     */
    private void closeBets() {
        LocalDateTime now = LocalDateTime.now();
        List<Game> closeGames = facade.closeBets(now);
        System.out.println("Close Bets acordou. Close games: " + closeGames);
        if (!closeGames.isEmpty()) {
            List<Game> openGames = facade.openGamesInfo();
            if (openGames.isEmpty())
                changePermissionsWrite(false);
            MessageSender.sendMessage(channelBets, Templates.messageCloseBets(closeGames, openGames));
            List<LocalDateTime> alarms = getAlarms(closeGames, Game::endGame);
            System.out.println("Alarmes para ended games: " + alarms);
            alarms.forEach(d -> createStaticScheduler(this::reminderEndedGames, now, d));
        }
    }

    /**
     * Just a method that reminds admin that a game has ended and there is not a result attributed yet.
     * This method will be called when a game has finished.
     */
    private void reminderEndedGames() {
        LocalDateTime now = LocalDateTime.now();
        List<Game> endedGames = facade.endedGames(now);
        System.out.println("Reminder Ended games acordou. Ended games " + endedGames);
        if (!endedGames.isEmpty())
            MessageSender.sendMessage(channelReminder, Templates.messageReminderEndedGames(endedGames));
    }

    /**
     * Just a reminder to inform admin what are the next schedule games.
     * This method will be called daily, at the same time as <i>openBets</i> method.
     */
    private void reminderNextGames() {
        List<Game> nextGames = facade.nextGames();
        MessageSender.sendMessage(channelReminder, Templates.messageReminderNextGames(nextGames));
    }

    /**
     * Method that runs once a day.
     */
    private void runDaily() {
        openBets();
        reminderNextGames();
    }

    /**
     * This method will get the channels that we want and create a daily schedule.
     *
     * @param event Event Ready.
     */
    @Override
    public void onReady(ReadyEvent event) {
        channelBets = event.getJDA().getTextChannelById(ParserInfo.getInstance().getValueString("channelBetsTest"));
        channelReminder = event.getJDA().getTextChannelById(ParserInfo.getInstance().getValueString("channelReminder"));
        if (channelBets != null && channelReminder != null) {
            changePermissionsWrite(false);
            scheduler.scheduleAtFixedRate(this::runDaily, 0, 1, TimeUnit.MINUTES);
        } else
            System.out.println("One or both channels doesn't exist.");
    }

    /**
     * Stops the service. Used when the bot is shutdown.
     */
    public void onStop() {
        this.scheduler.shutdown();
    }
}
