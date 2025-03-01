package org.botgverreiro.controllers.slashcommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.models.*;
import org.botgverreiro.utils.Cache;
import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.configuration.option.Number;
import com.github.josebambora.configuration.option.OptionNumber;
import com.github.josebambora.configuration.option.OptionString;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseAutoComplete;
import com.github.josebambora.responses.ResponseCommand;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GameAdd implements SlashEvent {

    // Cache Results from AutoCompleteOptions
    private final Cache<String, Team> cacheTeams;
    private final Cache<Integer, Season> cacheSeason;

    public GameAdd() {
        cacheTeams = new Cache<>(s -> Settings.commitTransaction(c -> Team.getSimilarTeams(c, s)));
        cacheSeason = new Cache<>(s -> Settings.commitTransaction(c -> Season.getSimilarSeasons(c, s)));
    }

    @Override
    public void configure(SlashCommand slashCommand) {
        OptionString optionMode = new OptionString("modo", "Modalidade do jogo", true);
        Settings.commitTransactionNoResult(c -> Mode.getAllModesSync(c).forEach(m -> optionMode.addChoice(m.toString(), m.toString())));
        OptionNumber optionSeason = new OptionNumber("epoca", "Temporada do jogo", false, Number.INTEGER)
                .setAutoComplete(this::seasonList);
        OptionNumber optionField = new OptionNumber("campo", "Campo do jogo", true, Number.INTEGER)
                .addChoice("Casa", 0)
                .addChoice("Fora", 1)
                .addChoice("Neutro", 2);
        OptionNumber optionMonth = new OptionNumber("mes", "Mês do jogo", true, Number.INTEGER);
        OptionNumber optionDay = new OptionNumber("dia", "Dia do jogo", true, Number.INTEGER);
        OptionNumber optionHour = new OptionNumber("hora", "Hora do jogo", true, Number.INTEGER);
        OptionNumber optionMinute = new OptionNumber("minuto", "Minutos do jogo", true, Number.INTEGER);
        OptionString optionTeam = new OptionString("adversario", "Adversário", true)
                .setAutoComplete(this::teamsList);

        slashCommand.setName("game_add")
                .setDescription("Calendarizar um jogo")
                .setSendThinking()
                .setEphemeral()
                .addOptions(optionMonth, optionDay, optionHour, optionMinute, optionTeam, optionSeason, optionMode, optionField)
                .addPermission(Permission.KICK_MEMBERS);
    }

    private void teamsList(CommandAutoCompleteInteractionEvent event, String input, ResponseAutoComplete responseAutoComplete) {
        cacheTeams.get(input)
                .thenApply(l -> responseAutoComplete.addChoice(l.stream().map(Team::toChoice).toList()))
                .thenAccept(ResponseAutoComplete::send);
    }

    private void seasonList(CommandAutoCompleteInteractionEvent event, String input, ResponseAutoComplete responseAutoComplete) {
        try {
            int season = Integer.parseInt(input);
            cacheSeason.get(season)
                    .thenApply(l -> responseAutoComplete.addChoice(l.stream().map(Season::toChoice).toList()))
                    .thenAccept(ResponseAutoComplete::send);
        } catch (NumberFormatException e) {
            responseAutoComplete.send();
        }
    }

    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        String team = (String) map.get("adversario");
        Integer season = (Integer) map.get("season");
        String mode = (String) map.get("modo");
        int field = (Integer) map.get("campo");
        int month = (Integer) map.get("mes");
        int day = (Integer) map.get("dia");
        int hours = (Integer) map.get("hora");
        int minutes = (Integer) map.get("minuto");
        Settings.commitTransaction(c ->
                        Team.insertTeam(c, team)
                        .thenCompose(_ -> season == null ? Season.getLastSeason(c).thenApply(Season::getSeasonId) : CompletableFuture.completedFuture(season))
                        .thenCompose(res -> Game.insertGame(c, res, mode, field, month, day, hours, minutes, team)))
                .thenApply(r -> r == 1 ? responseCommand.setTemplate("Success").setVariable("op", "Adicionar Jogo.") : responseCommand.setTemplate("500"))
                .thenAccept(ResponseCommand::send);
    }
}
