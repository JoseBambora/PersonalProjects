package org.botgverreiro.controllers.slashcommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.models.*;
import org.botgverreiro.utils.Cache;
import org.botgverreiro.utils.LimitList;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.configuration.option.Number;
import org.jdaextension.configuration.option.OptionNumber;
import org.jdaextension.configuration.option.OptionString;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.ResponseAutoComplete;
import org.jdaextension.responses.ResponseCommand;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class GameAdd implements SlashEvent {

    // Cache Results from AutoCompleteOptions
    private final Cache<String, Team> cacheTeams = new Cache<>(this::fetchTeams);
    private final Cache<Integer, Season> cacheSeason = new Cache<>(this::fetchSeasons);

    private CompletionStage<List<Team>> fetchTeams(String input) {
        return Settings.commitTransaction(c -> Team.getSimilarTeams(c, input))
                .thenApply(l -> LimitList.subList(l, 25));
    }

    private CompletionStage<List<Season>> fetchSeasons(Integer input) {
        return Settings.commitTransaction(c -> Season.getSimilarSeasons(c, input))
                .thenApply(l -> LimitList.subList(l, 25));
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
                .thenAccept(l -> l.forEach(t -> responseAutoComplete.addChoice(t.toChoice())))
                .thenAccept(_ -> responseAutoComplete.send());
    }

    private void seasonList(CommandAutoCompleteInteractionEvent event, String input, ResponseAutoComplete responseAutoComplete) {
        try {
            int season = Integer.parseInt(input);
            cacheSeason.get(season)
                    .thenAccept(l -> l.forEach(t -> responseAutoComplete.addChoice(t.toChoice())))
                    .thenAccept(_ -> responseAutoComplete.send());
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
        Settings.commitTransaction(c -> Team.insertTeam(c, team)
                        .thenCompose(_ -> season == null ? Season.getLastSeason(c).thenApply(Season::getSeasonId) : CompletableFuture.completedFuture(season))
                        .thenCompose(res -> Game.insertGame(c, res, mode, field, month, day, hours, minutes, team)))
                .thenApply(r -> r == 1 ? responseCommand.setTemplate("Success").setVariable("op", "Adicionar Jogo.") : responseCommand.setTemplate("500"))
                .thenAccept(ResponseCommand::send);
    }
}
