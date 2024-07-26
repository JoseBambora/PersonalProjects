package org.botgverreiro.bot.frontend;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.botgverreiro.bot.interactions.InteractionDelete;
import org.botgverreiro.bot.interactions.InteractionTop;
import org.botgverreiro.bot.utils.CommandsBuilder;
import org.botgverreiro.bot.utils.Inputs;
import org.botgverreiro.bot.utils.Templates;
import org.botgverreiro.facade.Facade;
import org.botgverreiro.model.classes.Game;
import org.botgverreiro.model.classes.Season;
import org.botgverreiro.model.classes.User;
import org.botgverreiro.model.enums.ConverterString;
import org.botgverreiro.model.enums.Field;
import org.botgverreiro.model.enums.Mode;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * This class is responsible for handling commands related to bets.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class BetCommands {
    private final Set<String> commandsNames;
    private final Facade facade;
    private final CommandsBuilder commandsBuilder;
    private final InteractionService interactionManager;
    private final Pattern pattern = Pattern.compile("(\\d+) *[x\\-] *(\\d+)");

    public BetCommands(Facade facade) {
        this.facade = facade;
        this.interactionManager = new InteractionService();
        commandsBuilder = new CommandsBuilder();
        commandsNames = new HashSet<>(commandsBuilder.modsCommands().stream().map(CommandData::getName).toList());
        commandsNames.addAll(commandsBuilder.usersCommands().stream().map(CommandData::getName).toList());
    }

    /**
     * Checks if the given command is valid. A command is considered valid if its name corresponds to any of the available commands.
     *
     * @param event The command to verify.
     * @return True if the command is valid, false otherwise.
     */
    public boolean hasCommand(SlashCommandInteractionEvent event) {
        return !commandsNames.contains(event.getName());
    }

    /**
     * Standard function that returns a successful Discord message if the operation completes successfully.
     *
     * @param event     The command that was used.
     * @param code      The returned code of the operation. 0 means success; other values indicate an error.
     * @param operation The operation associated with the command. Since the messages are in Portuguese, this text is also in Portuguese.
     * @param extra     Additional context information.
     * @see Templates
     * @see MessageSender
     */
    private void successOrError(SlashCommandInteractionEvent event, int code, String operation, String extra) {
        if (code == 0)
            MessageSender.sendMessage(event, Templates.messageSuccessOperation(operation, extra));
        else
            MessageSender.sendMessage(event, Templates.messageErrorOperation(operation));
    }

    /**
     * Verifies if the user's season input is valid.
     * <p>
     * A correct format is "number1/number2" or null. In case of null, last season of the database will be considered.
     * </p>
     *
     * @param season The user's input season.
     * @param event  The command that the user used.
     * @return True if the season string format is correct, false otherwise.
     * @see MessageSender
     */
    private boolean seasonCorrect(String season, SlashCommandInteractionEvent event) {
        boolean res = season == null || (season.contains("/") && season.split("/").length == 2);
        if (!res)
            MessageSender.sendMessage(event, "Temporada no formato errado. Formato certo x/x. Exemplo: 24/25");
        return res;
    }

    /**
     * Handles the <b>/stats</b> command. This command returns user stats for a specific season and modality.
     * <p>
     * Command arguments are exactly the same as the command <b>/season</b> and <b>/top</b> commands:
     *      <ul>
     *          <li>This command can receive two arguments (they may be null).</li>
     *          <li><b>"temporada"</b>: The season for which users want to see their stats. If null, the last season will be considered.</li>
     *          <li><b>"modalidade"</b>: The modality for which users want to see their stats. If null, all modalities will be considered.</li>
     *      </ul>
     * </p>
     *
     * @param event The event command.
     * @see Inputs
     * @see ConverterString
     * @see Mode
     * @see Facade
     * @see User
     */
    private void statsUserCommand(SlashCommandInteractionEvent event) {
        String season = Inputs.getString(event, "temporada");
        if (seasonCorrect(season, event)) {
            Mode mode = ConverterString.fromStringMode(Inputs.getString(event, "modalidade"));
            User user = facade.statsUser(event.getUser().getAsMention(), mode, season);
            String modeStr = ConverterString.toStringMode(mode);
            if (user != null) {
                EmbedBuilder embed = Templates.embedTemplate("Estatísticas de " + user.getName(), event.getUser().getAvatarUrl(), "Estas são as estatísticas de " + user.getName() + ". Aqui contém o número total de previsões e acertos para uma modalidade e temporada.");
                embed.addField("Modalidade", modeStr, true);
                embed.addField("Temporada", season == null ? "Atual" : season, true);
                embed.addField("Posição", String.format("%dº", user.getPosition()), false);
                embed.addField("Total de previsões", Integer.toString(user.getTotalPredictions()), true);
                embed.addField("Total de pontos", Integer.toString(user.getTotalPoints()), true);
                embed.setFooter("Se pretende apagar estas informações, utilize o comando /delete.");
                MessageSender.sendEmbed(event, embed);
            } else {
                String hasSeason = season == null ? "" : ", ou a temporada " + season + " não existe";
                MessageSender.sendMessage(event, "Para a modalidade " + modeStr + ", você não tem qualquer registo" + hasSeason + ".");
            }
        }
    }

    /**
     * Handles the <b>/season</b> command. This command returns the statistics for a specific season and modality.
     * <p>
     * The command arguments are the same as those for the <b>/stats</b> and <b>/top</b> commands:
     *      <ul>
     *          <li>This command can receive two arguments (they may be null).</li>
     *          <li><b>"temporada"</b>: The season for which users want to see their stats. If null, the last season will be considered.</li>
     *          <li><b>"modalidade"</b>: The modality for which users want to see their stats. If null, all modalities will be considered.</li>
     *      </ul>
     * </p>
     *
     * @param event The event command.
     * @see Inputs
     * @see ConverterString
     * @see Mode
     * @see Facade
     * @see Season
     */
    private void statsSeasonCommand(SlashCommandInteractionEvent event) {
        String seasonStr = Inputs.getString(event, "temporada");
        if (seasonCorrect(seasonStr, event)) {
            Mode mode = ConverterString.fromStringMode(Inputs.getString(event, "modalidade"));
            Season season = facade.statsSeason(seasonStr, mode);
            if (season != null) {
                EmbedBuilder embed = Templates.embedTemplate("Estatísticas da temporada " + season.getSeason(), "Estas são as estatísticas da " + season.getSeason() + ". Aqui contém o número total de previsões, acertos, jogos, golos marcados, sofridos para todas as modalidade.");
                embed.addField("Total de jogos *", Integer.toString(season.getGames()), true);
                embed.addField("Previsões *", Integer.toString(season.getTotalPredictions()), true);
                embed.addField("Acertos *", Integer.toString(season.getCorrectPredictions()), true);
                embed.addField("Golos marcados *", Integer.toString(season.getScored()), true);
                embed.addField("Golos sofridos *", Integer.toString(season.getConceded()), true);
                if (Mode.NONE == mode)
                    embed.setFooter("* Em todas as modalidades");
                else
                    embed.setFooter("* Apenas para a modalidade " + ConverterString.toStringMode(mode));
                MessageSender.sendEmbed(event, embed);
            } else
                MessageSender.sendMessage(event, "Temporada " + seasonStr + " sem qualquer registo.");
        }
    }

    /**
     * Handles the <b>/delete</b> command. This command deletes user information. Notes:
     * <ul>
     *     <li>There are no arguments for this command.</li>
     *     <li>User information is only deleted after users confirm they want to delete their information.</li>
     *     <li>The confirmation process is conducted through an interaction with two buttons.</li>
     * </ul>
     *
     * @param event The event command.
     * @see Facade
     * @see InteractionDelete
     * @see InteractionService
     */
    private void deleteCommand(SlashCommandInteractionEvent event) {
        String mention = event.getUser().getAsMention();
        InteractionDelete interactionDelete = new InteractionDelete(mention, facade);
        interactionManager.addAndSendMessage(event, interactionDelete);
    }

    /**
     * Used for the table in the <b>/top</b> command.
     * This function shortens the username to reduce the string length if necessary.
     *
     * @param u The user to convert.
     * @return An array with the user's position, username (or shortened username, if applicable), and user points.
     * @see User
     */
    private List<String> converter(User u) {
        String name = u.getName().length() > 15 ? u.getName().substring(0, 12) + "..." : u.getName();
        return List.of(Integer.toString(u.getPosition()),
                name,
                Integer.toString(u.getTotalPoints())
        );
    }

    /**
     * Splits the table response for <b>/top</b> into multiple pages, with each page containing 10 rows.
     * Additionally, retrieves the fields shown in the table using the converter function.
     *
     * @param top The table with user classifications.
     * @return The split table.
     * @see User
     */
    private List<List<List<String>>> getPages(List<User> top) {
        int pageSize = 10;
        return IntStream.range(0, (top.size() + pageSize - 1) / pageSize)
                .mapToObj(i -> top.stream()
                        .skip((long) i * pageSize)
                        .limit(pageSize)
                        .map(this::converter)
                        .toList())
                .toList();
    }

    /**
     * Responds to the <b>/top</b> command for a specific season and modality. The response includes a table split into multiple pages.
     * Users can navigate through these pages using an interaction with two buttons.
     * <p>
     * The command arguments are the same as those for the <b>/stats</b> and <b>/season</b> commands:
     *      <ul>
     *          <li>This command can receive two arguments (they may be null).</li>
     *          <li><b>"temporada"</b>: The season for which users want to see their stats. If null, the last season will be considered.</li>
     *          <li><b>"modalidade"</b>: The modality for which users want to see their stats. If null, all modalities will be considered.</li>
     *      </ul>
     * </p>
     *
     * @param event The event command.
     * @see Inputs
     * @see ConverterString
     * @see Mode
     * @see Facade
     * @see MessageSender
     * @see InteractionTop
     * @see InteractionService
     */
    private void topCommand(SlashCommandInteractionEvent event) {
        String seasonStr = Inputs.getString(event, "temporada");
        if (seasonCorrect(seasonStr, event)) {
            Mode mode = ConverterString.fromStringMode(Inputs.getString(event, "modalidade"));
            String modeStr = ConverterString.toStringMode(mode);
            List<User> top = facade.classification(mode, seasonStr);
            seasonStr = (seasonStr == null ? "atual" : seasonStr);
            if (top.isEmpty())
                MessageSender.sendMessage(event, "Não há classificação para o modo " + modeStr + " e temporada " + seasonStr + ".");
            else {
                InteractionTop interactionTop = new InteractionTop(event.getUser().getAsMention(), List.of("", "Nome", "Pontos"), getPages(top), seasonStr, modeStr);
                interactionManager.addAndSendMessage(event, interactionTop);
            }
        }
    }

    /**
     * Handles button click interactions. This functionality is used for the <b>/delete</b> and <b>/top</b> commands.
     *
     * @param event The button interaction event.
     * @see InteractionService
     */
    public void clickedButton(ButtonInteractionEvent event) {
        interactionManager.newInteraction(event);
    }

    /**
     * Handles the <b>/new</b> command, which is responsible for creating a new season.
     *
     * @param event The event command.
     * @see Season
     * @see Facade
     */
    private void newCommand(SlashCommandInteractionEvent event) {
        successOrError(event, facade.newSeason(), "Nova Temporada", null);
    }

    /**
     * Handles the <b>/win</b> command.
     * This command displays the users who predicted correctly for a specific modality.
     * <p>
     * This command accepts three arguments:
     *     <ul>
     *         <li><b>"goloscasa"</b>: Goals scored by the home team.</li>
     *         <li><b>"golosfora"</b>: Goals scored by the away team.</li>
     *         <li><b>"modalidade"</b>: Modality. <b>Must not</b> be null.</li>
     *     </ul>
     * </p>
     *
     * @param event The event command.
     * @see Inputs
     * @see ConverterString
     * @see Mode
     * @see Facade
     * @see Game
     * @see Templates
     * @see MessageSender
     */
    private void winCommand(SlashCommandInteractionEvent event) {
        int home = Inputs.getInteger(event, "goloscasa");
        int away = Inputs.getInteger(event, "golosfora");
        Mode mode = ConverterString.fromStringMode(Inputs.getString(event, "modalidade"));
        Game game = facade.getFstWaitingResultGame(mode);
        List<String> winners = facade.win(home, away, mode);
        if (winners == null)
            MessageSender.sendMessage(event, Templates.messageWinners(null, null));
        else {
            successOrError(event, 0, "win", null);
            MessageSender.sendMessage(event.getChannel().asTextChannel(), Templates.messageWinners(winners, game));
        }
    }

    /**
     * Handles the <b>/add</b> command, which adds a game to the bot.
     * <p>
     * This command accepts multiple arguments:
     *     <ul>
     *         <li><b>"hora"</b>: Game hour.</li>
     *         <li><b>"minuto"</b>: Game minute.</li>
     *         <li><b>"dia"</b>: Game day.</li>
     *         <li><b>"mes"</b>: Game month.</li>
     *         <li><b>"adversario"</b>: Opponent.</li>
     *         <li><b>"modalidade"</b>: Modality.</li>
     *         <li><b>"campo"</b>: Game field (Home/Away/Neutral).</li>
     *     </ul>
     * </p>
     *
     * @param event The event command.
     * @see Inputs
     * @see ConverterString
     * @see Mode
     * @see Field
     * @see Facade
     * @see Game
     */
    private void addCommand(SlashCommandInteractionEvent event) {
        int hora = Inputs.getInteger(event, "hora");
        int minuto = Inputs.getInteger(event, "minuto");
        int dia = Inputs.getInteger(event, "dia");
        int mes = Inputs.getInteger(event, "mes");
        String opponent = Inputs.getString(event, "adversario");
        Mode mode = ConverterString.fromStringMode(Inputs.getString(event, "modalidade"));
        Field field = ConverterString.fromStringField(Inputs.getString(event, "campo"));
        LocalDateTime now = LocalDateTime.now();
        try {
            LocalDateTime aux = LocalDateTime.of(now.getYear(), mes, dia, hora, minuto);
            Game game = Game.buildGame(field, opponent, mode, now.isBefore(aux) ? aux : aux.plusYears(1));
            int code = facade.addGame(game);
            successOrError(event, code, "Adicionar Jogo", "Jogo " + game + " adicionado.");
        } catch (DateTimeException ignored) {
            MessageSender.sendMessage(event, "Jogo não adicionado. Data não existe.");
        }
    }

    /**
     * Function to handle <b>/inst</b> command.
     * This command just returns a message with instruction to submit predictions.
     *
     * @param event The event command.
     * @see Templates
     * @see MessageSender
     */
    private void instCommand(SlashCommandInteractionEvent event) {
        MessageSender.sendMessage(event, Templates.messageInstructions());
    }

    /**
     * Function to handle <b>/help</b> command.
     * This command just returns a message with all the command that the user can use.
     *
     * @param event The event command.
     * @see Templates
     * @see MessageSender
     */
    private void helpCommand(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        boolean isMod = member != null && member.hasPermission(Permission.MANAGE_SERVER);
        MessageSender.sendMessage(event, Templates.messageHelp(isMod));
    }

    /**
     * Handles the <b>/remove</b> command, which removes a scheduled game.
     * <p>
     * This command accepts the following arguments:
     *     <ul>
     *         <li><b>"modalidade"</b>: Modality.</li>
     *         <li><b>"adversario"</b>: Opponent.</li>
     *         <li><b>"index"</b>: Index of the game (can be null). Only necessary when there are two scheduled games for the same modality and opponent.</li>
     *     </ul>
     * </p>
     *
     * @param event The event command.
     * @see Inputs
     * @see ConverterString
     * @see Game
     * @see Facade
     * @see MessageSender
     */
    private void removeCommand(SlashCommandInteractionEvent event) {
        String opponent = Inputs.getString(event, "adversario");
        Mode mode = ConverterString.fromStringMode(Inputs.getString(event, "modalidade"));
        int index = Inputs.getInteger(event, "index");
        Game game = facade.removeGame(mode, opponent, index);
        if (game != null)
            MessageSender.sendMessage(event, "Jogo " + game + " removido com sucesso");
        else
            MessageSender.sendMessage(event, "Jogo não existe");
    }

    /**
     * This function is responsible for handle the command <b>/info</b>.
     * This is like a debug command, since it will return next games that are schedule, open games, and so on.
     *
     * @param event The event command.
     * @see Game
     * @see Facade
     * @see Templates
     * @see MessageSender
     */
    private void infoCommand(SlashCommandInteractionEvent event) {
        LocalDateTime now = LocalDateTime.now();
        List<Game> nextGames = facade.nextGames();
        List<Game> openGames = facade.openGamesInfo();
        List<Game> happeningGames = facade.onGoingGames(now);
        List<Game> endedGames = facade.endedGames(now);
        MessageSender.sendMessage(event, Templates.messageInfo(nextGames, openGames, happeningGames, endedGames));
    }

    /**
     * Method that responses to the command <b>/end</b>.
     * This command just returns an end season message, presenting the winners and some season statistics.
     *
     * @param event The event command.
     * @see Season
     * @see User
     * @see Facade
     * @see Templates
     * @see MessageSender
     */
    private void endCommand(SlashCommandInteractionEvent event) {
        Season season = facade.statsSeason(null, Mode.NONE);
        List<User> winners = facade.classification(Mode.NONE, null).stream().filter(u -> u.getPosition() == 1).toList();
        successOrError(event, 0, "Fim de temporada", null);
        MessageSender.sendMessage(event.getChannel().asTextChannel(), Templates.messageEndSeason(season, winners));
    }

    /**
     * Checks if a user has permission to execute a certain command.
     *
     * @param event The event command.
     * @return True if the user has permissions; false otherwise.
     * @see CommandsBuilder
     */
    public boolean hasPermission(SlashCommandInteractionEvent event) {
        return !commandsBuilder.isNotModCommand(event.getName()) && (event.getMember() == null || !event.getMember().hasPermission(Permission.MANAGE_SERVER));
    }

    /**
     * Handles the command <b>/bot</b>. This command just returns an introduction bot message, explaining its existing and so on.
     *
     * @param event The event command.
     */
    private void botCommand(SlashCommandInteractionEvent event) {
        MessageSender.sendMessage(event, Templates.messageBot());
    }

    /**
     * Function that executes a command. It will check if the command exists and if the user has permissions to use it.
     * If both conditions are satisfiable, this command will call the respective method to handle the response.
     *
     * @param event The event command.
     */
    public void execCommand(SlashCommandInteractionEvent event) {
        String command = event.getName();
        switch (command) {
            case "stats" -> statsUserCommand(event);
            case "season" -> statsSeasonCommand(event);
            case "delete" -> deleteCommand(event);
            case "top" -> topCommand(event);
            case "new" -> newCommand(event);
            case "win" -> winCommand(event);
            case "add" -> addCommand(event);
            case "inst" -> instCommand(event);
            case "help" -> helpCommand(event);
            case "remove" -> removeCommand(event);
            case "info" -> infoCommand(event);
            case "end" -> endCommand(event);
            case "bot" -> botCommand(event);
        }
    }

    /**
     * Returns available commands for moderator users.
     *
     * @return Moderator user commands.
     * @see CommandsBuilder
     */
    public List<CommandData> getCommandsMods() {
        return commandsBuilder.modsCommands();
    }

    /**
     * Returns available commands for standard users.
     *
     * @return Standard user commands.
     * @see CommandsBuilder
     */
    public List<CommandData> getCommandsUser() {
        return commandsBuilder.usersCommands();
    }

    /**
     * Function to handle new predictions. It will check if the message is not from a bot and if it in the correct channels.
     * If both conditions are true, the bot will read users predictions, and then it will store them.
     *
     * @param event Event message.
     * @see Facade
     */
    public void receiveMessage(MessageReceivedEvent event) {
        int code = 2;
        try {
            List<Integer> homeGoals = new ArrayList<>(3);
            List<Integer> awayGoals = new ArrayList<>(3);
            Matcher matcher = pattern.matcher(event.getMessage().getContentDisplay());
            while (matcher.find()) {
                homeGoals.add(Integer.parseInt(matcher.group(1)));
                awayGoals.add(Integer.parseInt(matcher.group(2)));
            }
            if (!homeGoals.isEmpty()) {
                String idUser = event.getAuthor().getAsMention();
                String username = event.getAuthor().getName();
                code = facade.newBet(idUser, username, homeGoals, awayGoals);
            }
        } catch (NumberFormatException ignored) {
        }
        if (code == 0)
            MessageSender.sendEmoji(event, Emoji.fromUnicode("✅"));
        else if (code == 1)
            MessageSender.sendEmoji(event, Emoji.fromUnicode("U+1F504"));
        else
            MessageSender.sendEmoji(event, Emoji.fromUnicode("❌"));
    }
}
