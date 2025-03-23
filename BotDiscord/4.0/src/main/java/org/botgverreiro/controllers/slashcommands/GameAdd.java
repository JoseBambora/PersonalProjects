package org.botgverreiro.controllers.slashcommands;

import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.configuration.option.Number;
import com.github.josebambora.configuration.option.OptionNumber;
import com.github.josebambora.configuration.option.OptionString;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.controllers.autocompleters.SeasonList;
import org.botgverreiro.controllers.autocompleters.TeamList;
import org.botgverreiro.models.*;
import org.botgverreiro.utils.ExceptionsHandler;

import java.util.Map;

public class GameAdd implements SlashEvent {

    @Override
    public void configure(SlashCommand slashCommand) {
        OptionString optionMode = new OptionString("modo", "Modalidade do jogo", true);
        Settings.commitTransactionNoResult(c -> Mode.selectAllModesSync(c).forEach(m -> optionMode.addChoice(m.toString(), m.toString())));
        OptionNumber optionSeason = new OptionNumber("epoca", "Temporada do jogo", false, Number.INTEGER)
                .setAutoComplete(SeasonList::seasonList);
        OptionNumber optionField = new OptionNumber("campo", "Campo do jogo", true, Number.INTEGER)
                .addChoice("Casa", 0)
                .addChoice("Fora", 1)
                .addChoice("Neutro", 2);
        OptionNumber optionMonth = new OptionNumber("mes", "Mês do jogo", true, Number.INTEGER)
                .addChoice("Jan", 1)
                .addChoice("Fev", 2)
                .addChoice("Mar", 3)
                .addChoice("Abr", 4)
                .addChoice("Mai", 5)
                .addChoice("Jun", 6)
                .addChoice("Jul", 7)
                .addChoice("Ago", 8)
                .addChoice("Set", 9)
                .addChoice("Out", 10)
                .addChoice("Nov", 11)
                .addChoice("Dez", 12);
        OptionNumber optionDay = new OptionNumber("dia", "Dia do jogo", true, Number.INTEGER);
        OptionNumber optionHour = new OptionNumber("hora", "Hora do jogo", true, Number.INTEGER);
        OptionNumber optionMinute = new OptionNumber("minuto", "Minutos do jogo", true, Number.INTEGER);
        OptionString optionTeam = new OptionString("adversario", "Adversário", true)
                .setAutoComplete(TeamList::teamsList);

        slashCommand.setName("game-add")
                .setDescription("Calendarizar um jogo")
                .setSendThinking()
                .setEphemeral()
                .addOptions(optionMonth, optionDay, optionHour, optionMinute, optionTeam, optionSeason, optionMode, optionField)
                .addPermission(Permission.KICK_MEMBERS);
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
                        Team.insertTeam(c, new Team(team))
                                .thenCompose(_ -> season == null ? Season.selectLastSeason(c) : Season.selectSeason(c, season))
                                .thenCompose(res -> Game.insertGame(c, new Game(res, new Mode(mode), field, month, day, hours, minutes, new Team(team)))))
                .thenApply(r -> r == 1 ? responseCommand.setTemplate("Success").setVariable("op", "Adicionar Jogo.") : responseCommand.setTemplate("500"))
                .thenAccept(ResponseCommand::send)
                .exceptionally(ExceptionsHandler::storeException);
    }
}
