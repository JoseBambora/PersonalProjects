package org.botgverreiro.bot.utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.stream.Stream;

import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

/**
 * This is a class to build the actual commands.
 * In here I specify all the slash commands, including their names and arguments.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class CommandsBuilder {
    private final List<CommandData> usersCommands;
    private final List<CommandData> modsCommands;
    private final List<String> usersCommandsNames;

    public CommandsBuilder() {
        CommandData top = Commands.slash("top", "Classificação dos prognósticos")
                .addOptions(getModalidadeOption(false), getSeasonOption());
        CommandData statsUser = Commands.slash("stats", "Estatísticas do utilizador")
                .addOptions(getModalidadeOption(false), getSeasonOption());
        CommandData statsSeason = Commands.slash("season", "Estatísticas da temporada")
                .addOptions(getSeasonOption());
        CommandData deleteUser = Commands.slash("delete", "Eliminar dados do utilizador");
        CommandData addGame = getAddGameCommand();
        CommandData newSeason = Commands.slash("new", "Apenas para Mods. Nova temporada");
        CommandData inst = Commands.slash("inst", "Instruções de submissão de prognósticos");
        CommandData help = Commands.slash("help", "Lista de comandos e os seus significados");
        CommandData info = Commands.slash("info", "Estado atual do bot");
        CommandData end = Commands.slash("end", "Mensagem de fim da temporada");
        CommandData bot = Commands.slash("bot", "Mensagem introdutória do bot");
        CommandData remove = getRemoveGameCommand();
        CommandData win = getWinCommand();
        modsCommands = Stream.of(addGame, newSeason, win, remove, info, end).map(c -> c.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_SERVER))).toList();
        usersCommands = List.of(top, statsUser, statsSeason, deleteUser, inst, help, bot);
        usersCommandsNames = usersCommands.stream().map(CommandData::getName).toList();
    }

    private OptionData getFieldOption() {
        return new OptionData(STRING, "campo", "Campo.", true)
                .addChoice("Casa", "C")
                .addChoice("Fora", "F")
                .addChoice("Neutro", "N");
    }

    private OptionData getModalidadeOption(boolean required) {
        return new OptionData(STRING, "modalidade", "Modalidade.", required)
                .addChoice("Futebol", "F")
                .addChoice("Futsal", "I")
                .addChoice("Seleção", "P");
    }

    private OptionData getSeasonOption() {
        return new OptionData(STRING, "temporada", "Época no formato x/x", false);
    }

    private OptionData getMinuteOption() {
        return new OptionData(INTEGER, "minuto", "Minuto", true);
        // .addChoice("15", 15)
        // .addChoice("30", 30)
        // .addChoice("45", 45)
        // .addChoice("00", 0);
    }

    private OptionData getHourOption() {
        return new OptionData(INTEGER, "hora", "Hora", true)
                .addChoice("10", 10)
                .addChoice("11", 11)
                .addChoice("12", 12)
                .addChoice("13", 13)
                .addChoice("14", 14)
                .addChoice("15", 15)
                .addChoice("16", 16)
                .addChoice("17", 17)
                .addChoice("18", 18)
                .addChoice("19", 19)
                .addChoice("20", 20)
                .addChoice("21", 21)
                .addChoice("22", 22);
    }

    private OptionData getMonthOption() {
        return new OptionData(INTEGER, "mes", "Mês", true)
                .addChoice("Janeiro", 1)
                .addChoice("Fevereiro", 2)
                .addChoice("Março", 3)
                .addChoice("Abril", 4)
                .addChoice("Maio", 5)
                .addChoice("Junho", 6)
                .addChoice("Julho", 7)
                .addChoice("Agosto", 8)
                .addChoice("Setembro", 9)
                .addChoice("Outubro", 10)
                .addChoice("Novembro", 11)
                .addChoice("Dezembro", 12);
    }

    private CommandData getAddGameCommand() {
        return Commands.slash("add", "Apenas para Mods. Adicionar jogos.")
                .addOption(STRING, "adversario", "Adversário", true)
                .addOption(INTEGER, "dia", "Dia", true)
                .addOptions(
                        getMonthOption(),
                        getHourOption(),
                        getMinuteOption(),
                        getFieldOption(),
                        getModalidadeOption(true)
                );
    }

    private CommandData getRemoveGameCommand() {
        return Commands.slash("remove", "Apenas para Mods. Remover jogos.")
                .addOption(STRING, "adversario", "Adversário", true)
                .addOptions(getModalidadeOption(true))
                .addOption(INTEGER, "index", "Indice", false);
    }

    private CommandData getWinCommand() {
        return Commands.slash("win", "Apenas para Mods. Resultado final")
                .addOption(INTEGER, "goloscasa", "Golos marcados pela equipa da casa", true)
                .addOption(INTEGER, "golosfora", "Golos marcados pela equipa visitante", true)
                .addOptions(getModalidadeOption(true));
    }

    public List<CommandData> modsCommands() {
        return modsCommands;
    }

    public List<CommandData> usersCommands() {
        return usersCommands;
    }

    public boolean isNotModCommand(String command) {
        return usersCommandsNames.contains(command);
    }
}
