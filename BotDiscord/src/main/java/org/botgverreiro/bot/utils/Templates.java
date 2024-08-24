package org.botgverreiro.bot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import org.botgverreiro.model.classes.Game;
import org.botgverreiro.model.classes.Season;
import org.botgverreiro.model.classes.User;

import java.awt.*;
import java.util.List;

/**
 * This is just a class to format the messages that are sent.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class Templates {
    public static EmbedBuilder embedTemplate(String title, String avatarURL, String description) {
        return embedTemplate(title, description).setThumbnail(avatarURL);
    }

    public static EmbedBuilder embedTemplate(String title, String description) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setAuthor("Bot Gverreiro");
        embed.setTitle(title);
        embed.setDescription(description);
        return embed;
    }

    private static String title(String title) {
        return "# " + title + "\n";
    }

    public static String messageWinners(List<String> winners, Game game) {
        if (winners == null)
            return "Não há jogo para associar o resultado.";
        StringBuilder stringBuilder = new StringBuilder(title("Vencedores " + game.toString(true, false)));
        if (winners.isEmpty())
            stringBuilder.append(String.format("Ninguém acertou os prognósticos do jogo %s.\n", game.toString(false, true)));
        else {
            String numberPeople = winners.size() == 1 ? "apenas uma pessoa acertou" : String.format("%d pessoas acertaram", winners.size());
            String aux = winners.size() == 1 ? "Vencedor" : "Vencedores";
            stringBuilder.append(String.format("No total %s os prognósticos do jogo %s. %s:\n", numberPeople, game.toString(false, true), aux));
            winners.forEach(id -> stringBuilder.append(String.format("- %s\n", id)));
        }
        stringBuilder.append('\n');
        stringBuilder.append("A tabela classificativa já foi atualizada.\n");
        stringBuilder.append("- Usa o comando `/top` para ver a classificação.\n");
        stringBuilder.append("- Usa o comando `/stats` para ver as tuas estatísticas.\n");
        stringBuilder.append("- Usa o comando `/season` para ver as estatísticas da temporada.\n");
        return stringBuilder.toString();
    }

    private static void addListGames(StringBuilder stringBuilder, List<Game> list, boolean day) {
        if (list.isEmpty())
            stringBuilder.append("Não há jogos a considerar\n");
        else
            list.forEach(g -> stringBuilder.append(String.format("- %s.\n", g.toString(day, true))));
    }

    private static String messageIntro(boolean open) {
        String openClose = open ? "abertos" : "fechados";
        return String.format("%sOs prognósticos estão **%s** para os seguintes jogos:\n", title("Prognósticos " + openClose), openClose);
    }

    public static String messageOpenBets(List<Game> list) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messageIntro(true));
        addListGames(stringBuilder, list, false);
        stringBuilder.append("\n@everyone\n");
        stringBuilder.append("**Nota**: Consultar instruções para a submissão de prognósticos através do comando `/inst`.");
        return stringBuilder.toString();
    }

    public static String messageCloseBets(List<Game> list, List<Game> openGames) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messageIntro(false));
        addListGames(stringBuilder, list, false);
        if (!openGames.isEmpty()) {
            stringBuilder.append("No entanto, os seguintos jogos continuam com os prognósticos abertos:\n");
            addListGames(stringBuilder, openGames, false);
        }
        return stringBuilder.toString();
    }

    public static String messageReminderEndedGames(List<Game> endedGames) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Jogos à espera de resultado:\n");
        addListGames(stringBuilder, endedGames, true);
        return stringBuilder.toString();
    }

    public static String messageReminderNextGames(List<Game> nextGames) {
        StringBuilder stringBuilder = new StringBuilder();
        if (nextGames.isEmpty()) {
            stringBuilder.append("Não há jogos agendados.\n");
        } else {
            stringBuilder.append("Próximos jogos:\n");
            addListGames(stringBuilder, nextGames, true);
        }
        return stringBuilder.toString();
    }

    public static String messageSuccessOperation(String operation, String extra) {
        return extra == null ? messageSuccessOperation(operation) : String.format("Operação '%s' realizada com sucesso. %s", operation, extra);
    }

    public static String messageSuccessOperation(String operation) {
        return String.format("Operação '%s' realizada com sucesso.", operation);
    }

    public static String messageErrorOperation(String operation) {
        return String.format("Operação '%s' falhou. Tente novamente ou contacte o JoséBambora.", operation);
    }

    public static String messageInstructions() {
        return title("Instruções") + "Instruções para submissão de prognósticos:\n" +
                "1. Só é possível fazer submissões quando o bot abre os prognósticos.\n" +
                "2. Atenção aos jogos que estão em aberto.\n" +
                "3. Submeter prognósticos para **todos** os jogos em aberto.\n" +
                "4. Se o número de prognósticos submetidos for diferente do número de jogos em aberto, a aposta não é registada.\n" +
                "\n**Exemplos**:\n" +
                "1. São abertos prognósticos para o jogo SC Braga x Dumiense FC (Futebol). Os seguintes prognósticos são válidos:\n" +
                " - 2-0\n" +
                " - 0-2\n" +
                " - 2x0\n" +
                "2. São abertos prognósticos para os jogos SC Braga x Dumiense FC (Futebol) e SCU Torrense x SC Braga (Futsal). Os seguintes prognósticos são válidos:\n" +
                " - 3-0 1x2\n" +
                " - 0-2 2-0\n" +
                " - 2x0 0-0\n";
    }

    private static String messageHelpArguments(boolean isMod) {
        String res = "Argumentos:\n" +
                "1. **temporada**: Especificar temporada.\n" +
                "2. **modalidade**: Especificar modalidade.\n";
        return isMod ? res + messageHelpArgumentsMods() : res;
    }

    private static String messageHelpArgumentsMods() {
        return "3. **hora**: Hora marcada para o jogo\n" +
                "4. **minuto**: Minutos do jogo\n" +
                "5. **dia**: Dia do jogo\n" +
                "6. **mes**: Mês do jogo\n" +
                "7. **adversário**: Adversário\n" +
                "8. **goloscasa**: Golos marcados pela equipa da casa\n" +
                "9. **golosfora**: Golos marcados pela equipa visitante\n" +
                "9. **index**: Indíce do jogo do adversário\n";
    }

    public static String messageHelp(boolean isMod) {
        List<String> header = List.of("Comando", "Explicação");
        List<List<String>> content = List.of(
                List.of("/help", "Comandos disponíveis"),
                List.of("/stats", "Estatísticas do utilizador"),
                List.of("/season", "Estatísticas de uma temporada"),
                List.of("/top", "Tabela classificativa"),
                List.of("/inst", "Instruções para prognósticos"),
                List.of("/delete", "Apagar dados do utilizador"),
                List.of("/bot", "Mensagem introdutória"),
                List.of("/add", "Adicionar um jogo"),
                List.of("/win", "Adicionar um resultado"),
                List.of("/new", "Criar nova temporada"),
                List.of("/remove", "Remover um jogo"),
                List.of("/info", "Estado do bot"),
                List.of("/end", "Mensagem de fim de temporada")
        );
        List<List<String>> finalContent = isMod ? content : content.subList(0, 6);
        return title("Lista de comandos") + "```\n" + TablePrinter.formatTable(header, finalContent) + "```\n" + Templates.messageHelpArguments(isMod);
    }

    public static String messageInfo(List<Game> nextGames, List<Game> openGames, List<Game> closeGames, List<Game> waitingResult) {
        StringBuilder stringBuilder = new StringBuilder(title("Estado do bot"));
        stringBuilder.append("**Jogos Agendados**:\n");
        addListGames(stringBuilder, nextGames, true);
        stringBuilder.append("\n**Jogos com prognósticos abertos**:\n");
        addListGames(stringBuilder, openGames, true);
        stringBuilder.append("\n**Jogos a decorrer**:\n");
        addListGames(stringBuilder, closeGames, true);
        stringBuilder.append("\n**Jogos à espera de resultado**:\n");
        addListGames(stringBuilder, waitingResult, true);
        return stringBuilder.toString();
    }

    public static String messageEndSeason(Season season, List<User> winners) {
        StringBuilder stringBuilder = new StringBuilder(title("Fim da temporada " + season.getSeason()));
        stringBuilder.append("A temporada **").append(season.getSeason()).append("** chegou ao fim. ");
        stringBuilder.append("Nesta temporada houve um total de __")
                .append(season.getTotalPredictions())
                .append("__ previsões, sendo que __")
                .append(season.getCorrectPredictions())
                .append("__ foram previsões corretas. (taxa de acerto: ")
                .append(String.format("__%.2f", (float) season.getCorrectPredictions() / (season.getTotalPredictions() != 0 ? season.getTotalPredictions() : 1) * 100))
                .append("%__).\n\n");
        stringBuilder.append("**Vencedores**:\n");
        winners.forEach(u -> stringBuilder.append("- ")
                .append(u.getMention())
                .append(" (")
                .append(u.getTotalPoints())
                .append(" pontos, ")
                .append(u.getTotalPredictions())
                .append(" previsões).\n"));
        stringBuilder.append("\n**Estatísticas da temporada**:")
                .append("\n- Golos marcados: ")
                .append(season.getScored())
                .append(".\n- Golos sofridos: ")
                .append(season.getConceded())
                .append(".\n- Vitórias: ")
                .append(season.getTotalWins())
                .append(".\n- Empates: ")
                .append(season.getTotalDraws())
                .append(".\n- Derrotas: ")
                .append(season.getTotalLoses())
                .append(".\n\n");
        stringBuilder.append("Muitos parabéns aos vencedores :partying_face: e muito obrigado a todos os participantes.\n");
        stringBuilder.append("Até à próxima temporada **Gverreiros** :saluting_face: :crossed_swords: :red_circle: :white_circle:.\n");
        stringBuilder.append(":notes: Foi no ano 21 :notes:");
        return stringBuilder.toString();
    }

    public static String messageBot() {
        return """
                Olá :hugging:, eu sou o **Bot Guerreiro**.

                Eu sou responsável pelos prognósticos da comunidade de adeptos do SC Braga no Discord. Fui desenvolvido pelo José Bambora, então qualquer erro que encontrares, contacta-o.

                Se tens dúvidas sobre questões de privacidade, eu **apenas** guardo os nomes e os @ dos utilizadores. Por isso, não te preocupes, pois informações sensíveis não são armazenadas. Se pretenderes apagar os teus dados, podes fazê-lo usando o comando `/delete`.

                Caso tenhas alguma dúvida sobre o funcionamento do bot, podes usar o comando `/help` para consultar os comandos disponíveis e `/inst` para instruções de submissão de prognósticos.

                **Curiosidades:**
                - Esta é a terceira versão do bot.
                - Esta versão foi feita em Java, usando o [JDA](https://github.com/discord-jda/JDA).
                - O Bot está disponível 24 horas todos os dias.
                                
                Viva o Sporting clube de Braga!!
                """;
    }

    public static String messageRateLimit() {
        return "Está a sobrecarregar demasiado o bot, aguarde um pouco.";
    }

    public static String messageUnkownCommand() {
        return "Comando desconhecido.";
    }

    public static String messageNoPermissions() {
        return "Não tem permissões para executar o comando.";
    }

    public static String messageSeasonBetsOpen(List<String> europeCompetitions) {
        return String.format("""
                A nova temporada está prestes a **começar**.
                                
                Deixa já a tua previsão sobre o que esperas para desta nova temporada, através do comando /bet.
                As previsões iram fechar daqui aquando o primeiro jogo oficial.
                                
                **Isto se aplica apenas à modalidade futebol senior**.
                                
                Se por ventura acertarem algum fator, os pontos recebidos são:
                                
                - Posição final Liga: 10 pontos
                - %s: 20 pontos
                - Taça da Liga: 5 pontos
                - Taça de Portugal: 10 pontos
                """, europeCompetitions.isEmpty() ? "Competições Europeias (não vai haver)" : String.join("/", europeCompetitions));
    }

    public static String messageSeasonBetsClose() {
        return "As previsões para a nova temporada estão fechadas.";
    }
}