import com.jakewharton.fliptables.FlipTable;
import mocks.MockButtonReaction;
import mocks.MockMessage;
import mocks.MockSlashCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.botgverreiro.bot.frontend.BetCommands;
import org.botgverreiro.bot.listeners.MessageReceiveListener;
import org.botgverreiro.bot.threads.MyLocks;
import org.botgverreiro.bot.utils.RateLimiter;
import org.botgverreiro.bot.utils.Templates;
import org.botgverreiro.facade.Facade;
import org.botgverreiro.model.classes.Game;
import org.botgverreiro.model.enums.Field;
import org.botgverreiro.model.enums.Mode;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * /win - X
 * XxX - X
 * /stats - X
 * /new - X
 * /season - X
 * /add - X
 * /remove - X
 * <p>
 * /top
 * /delete
 * /info
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBot {
    private static final Facade facade = new Facade(true);
    private static Game game1;
    private static Game game2;
    private final MessageReceiveListener messageReceiveListener = new MessageReceiveListener(new BetCommands(facade));

    @BeforeAll
    public static void beforeAll() {
        MyLocks.getInstance().addTestLocks();
        RateLimiter.setRateLimitedTest();
        facade.newSeason();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime localDateTime = now.plusMinutes(3).withSecond(0).withNano(0);
        game1 = Game.buildGameTest(Field.HOME, "SL Benfica", Mode.FOOTBALL, localDateTime);
        game2 = Game.buildGameTest(Field.AWAY, "Sporting CP", Mode.FUTSAL, localDateTime.plusMinutes(1));
    }

    @AfterAll
    public static void afterAll() {
        Path directory = Paths.get("files/dbtests/");
        File[] files = directory.toFile().listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    private void assertCommand(String command, String message, Map<String, Object> arguments) {
        MockSlashCommand mock = new MockSlashCommand(command, "test", "test", messageReceiveListener, arguments, true);
        mock.executeCommandWait();
        Assertions.assertEquals(message, mock.getResultMessage());
    }

    private MockSlashCommand execWaitCommand(String command, Map<String, Object> arguments) {
        MockSlashCommand mock = new MockSlashCommand(command, "test", "test", messageReceiveListener, arguments, true);
        mock.executeCommandWait();
        return mock;
    }

    private MockSlashCommand execCommand(String command, Map<String, Object> arguments, String mention, String username) {
        MockSlashCommand mock = new MockSlashCommand(command, mention, username, messageReceiveListener, arguments, true);
        mock.executeCommandNoWait();
        return mock;
    }

    private MockSlashCommand execCommand(String command, Map<String, Object> arguments) {
        MockSlashCommand mock = new MockSlashCommand(command, "test", "test", messageReceiveListener, arguments, true);
        mock.executeCommandNoWait();
        return mock;
    }

    private void assertCommand(String command, String message) {
        assertCommand(command, message, null);
    }

    private void assertResultEmbed(MockSlashCommand mock, String title, String description, String footer, List<MessageEmbed.Field> fields) {
        MessageEmbed messageEmbed = mock.getResultEmbed();
        Assertions.assertEquals(messageEmbed.getColor(), Color.RED);
        Assertions.assertEquals(messageEmbed.getAuthor().getName(), "Bot Gverreiro");
        Assertions.assertEquals(messageEmbed.getTitle(), title);
        Assertions.assertEquals(messageEmbed.getDescription(), description);
        Assertions.assertEquals(messageEmbed.getFooter().getText(), footer);
        if (fields != null) {
            List<MessageEmbed.Field> list = messageEmbed.getFields().stream().sorted(Comparator.comparing(MessageEmbed.Field::getName)).toList();
            List<MessageEmbed.Field> expected = fields.stream().sorted(Comparator.comparing(MessageEmbed.Field::getName)).toList();
            Assertions.assertEquals(list, expected);
        }
    }

    private void assertMessage(String messageContent, String mention, String name, String emojiCode) {
        MockMessage message = new MockMessage(messageContent, mention, name, messageReceiveListener);
        message.executeCommandWait();
        Assertions.assertEquals(message.getEmoji(), Emoji.fromUnicode(emojiCode));
    }

    private MockMessage assertMessageNoWait(String messageContent, String mention, String name) {
        MockMessage message = new MockMessage(messageContent, mention, name, messageReceiveListener);
        message.executeCommandNoWait();
        return message;
    }

    @Test
    public void test01() {
        assertCommand("exist", "Comando desconhecido.");
    }

    @Test
    public void test02() {
        assertCommand("stats", "Para a modalidade X, você não tem qualquer registo.");
        assertCommand("top", "Não há classificação para o modo X e temporada atual.");
        assertCommand("win", "Não há jogo para associar o resultado.", Map.of("modalidade", "F", "goloscasa", 3, "golosfora", 0));
    }

    @Test
    public void test03() {
        assertResultEmbed(
                execWaitCommand("season", null),
                "Estatísticas da temporada 24/25",
                "Estas são as estatísticas da 24/25. Aqui contém o número total de previsões, acertos, jogos, golos marcados, sofridos para todas as modalidade.",
                "* Em todas as modalidades",
                List.of(
                        new MessageEmbed.Field("Total de jogos *", "0", true),
                        new MessageEmbed.Field("Previsões *", "0", true),
                        new MessageEmbed.Field("Acertos *", "0", true),
                        new MessageEmbed.Field("Golos marcados *", "0", true),
                        new MessageEmbed.Field("Golos sofridos *", "0", true)
                ));
    }

    @Test
    public void test04() {
        LocalDate localDate = LocalDate.now();
        Game game = Game.buildGameTest(Field.HOME, "SL Benfica", Mode.FOOTBALL, localDate.atTime(15, 0));
        assertCommand("add", "Jogo não adicionado. Data não existe.", Map.of(
                "hora", 15,
                "minuto", 0,
                "dia", localDate.getDayOfMonth(),
                "mes", localDate.getMonthValue() + 12,
                "adversario", "SL Benfica",
                "modalidade", "F",
                "campo", "C"
        ));
        assertCommand("add", "Operação 'Adicionar Jogo' realizada com sucesso. Jogo " + game + " adicionado.", Map.of(
                "hora", 15,
                "minuto", 0,
                "dia", localDate.getDayOfMonth(),
                "mes", localDate.getMonthValue(),
                "adversario", "SL Benfica",
                "modalidade", "F",
                "campo", "C"
        ));
        assertCommand("remove", "Jogo não existe", Map.of(
                "adversario", "SL Benfica",
                "modalidade", "I"
        ));
        assertCommand("remove", "Jogo " + game + " removido com sucesso", Map.of(
                "adversario", "SL Benfica",
                "modalidade", "F"
        ));
        assertCommand("remove", "Jogo não existe", Map.of(
                "adversario", "SL Benfica",
                "modalidade", "F"
        ));
    }

    @Test
    public void test05() {
        assertMessage("1x0", "teste", "teste", "❌");
        assertMessage("1x2", "teste2", "teste2", "❌");
        assertCommand("add", "Operação 'Adicionar Jogo' realizada com sucesso. Jogo " + game1 + " adicionado.", Map.of(
                "hora", game1.startGame().getHour(),
                "minuto", game1.startGame().getMinute(),
                "dia", game1.startGame().getDayOfMonth(),
                "mes", game1.startGame().getMonthValue(),
                "adversario", game1.opponent(),
                "modalidade", "F",
                "campo", "C"
        ));
        assertCommand("add", "Operação 'Adicionar Jogo' realizada com sucesso. Jogo " + game2 + " adicionado.", Map.of(
                "hora", game2.startGame().getHour(),
                "minuto", game2.startGame().getMinute(),
                "dia", game2.startGame().getDayOfMonth(),
                "mes", game2.startGame().getMonthValue(),
                "adversario", game2.opponent(),
                "modalidade", "I",
                "campo", "F"
        ));
        List<Game> openGames = facade.openBets(game1.startGame().minusMinutes(5));
        Assertions.assertFalse(openGames.isEmpty());
        Assertions.assertEquals(openGames.size(), 2);
        Assertions.assertEquals(openGames.getFirst(), game1);
        Assertions.assertEquals(openGames.getLast(), game2);

        List<MockMessage> list1 = List.of(
                assertMessageNoWait("1x0", "teste1", "teste1"),
                assertMessageNoWait("1x2", "teste2", "teste2"),
                assertMessageNoWait("1x2", "teste3", "teste3"),
                assertMessageNoWait("3-0", "teste4", "teste4")
        );
        messageReceiveListener.waitFinish();
        list1.forEach(m -> Assertions.assertEquals(m.getEmoji(), Emoji.fromUnicode("❌")));
        List<MockMessage> list2 = List.of(
                assertMessageNoWait("2x0 0-1", "teste1", "teste1"),
                assertMessageNoWait("1x2 1-1", "teste2", "teste2"),
                assertMessageNoWait("1x2 0x1", "teste3", "teste3"),
                assertMessageNoWait("3-0 0-1", "teste4", "teste4"),
                assertMessageNoWait("2x0 1-2", "teste5", "teste5"),
                assertMessageNoWait("1x2 3-0", "teste6", "teste6"),
                assertMessageNoWait("1x3 5-1", "teste7", "teste7"),
                assertMessageNoWait("1x1 1-1", "teste8", "teste8"),
                assertMessageNoWait("0x0 2-1", "teste9", "teste9"),
                assertMessageNoWait("1x2 2-2", "teste10", "teste10"),
                assertMessageNoWait("2x3 3-1", "teste11", "teste11"),
                assertMessageNoWait("2x0 2-3", "teste12", "teste12"),
                assertMessageNoWait("1x0 3-3", "teste13", "teste13"),
                assertMessageNoWait("2x0 2-2", "teste14", "teste14")
        );
        messageReceiveListener.waitFinish();
        list2.forEach(m -> Assertions.assertEquals(m.getEmoji(), Emoji.fromUnicode("✅")));
        List<MockMessage> list3 = List.of(
                assertMessageNoWait("1x2 5-0", "teste8", "teste8"),
                assertMessageNoWait("0x2 1-2", "teste9", "teste9"),
                assertMessageNoWait("0x0 2-2", "teste10", "teste10"),
                assertMessageNoWait("2x1 4-4", "teste11", "teste11"),
                assertMessageNoWait("4x3 4-3", "teste12", "teste12"),
                assertMessageNoWait("2x0 2-4", "teste13", "teste13"),
                assertMessageNoWait("2x2 1-1", "teste14", "teste14")
        );
        List<MockMessage> list4 = List.of(
                assertMessageNoWait("1x2 5-0", "teste15", "teste15"),
                assertMessageNoWait("0x2 1-2", "teste16", "teste16"),
                assertMessageNoWait("0x0 2-2", "teste17", "teste17"),
                assertMessageNoWait("2x1 4-4", "teste18", "teste18"),
                assertMessageNoWait("4x3 4-3", "teste19", "teste19"),
                assertMessageNoWait("2x0 2-4", "teste20", "teste20"),
                assertMessageNoWait("2x0 1-2", "teste21", "teste21")
        );
        messageReceiveListener.waitFinish();
        List<MockMessage> list5 = new ArrayList<>();
        for (int i = 22; i < 200; i++)
            list5.add(assertMessageNoWait("10-0 10-0", "teste" + i, "teste" + i));
        messageReceiveListener.waitFinish();
        list3.forEach(m -> Assertions.assertEquals(m.getEmoji(), Emoji.fromUnicode("U+1F504")));
        list4.forEach(m -> Assertions.assertEquals(m.getEmoji(), Emoji.fromUnicode("✅")));
        list5.forEach(m -> Assertions.assertEquals(m.getEmoji(), Emoji.fromUnicode("✅")));
        List<Game> closeGames = facade.closeBets(game1.endGame().plusMinutes(1));
        Assertions.assertFalse(closeGames.isEmpty());
        Assertions.assertEquals(closeGames, openGames);
        assertMessage("1x0 0-0", "teste", "teste", "❌");
        assertMessage("1x2 0-0", "teste2", "teste2", "❌");
    }

    @Test
    public void test06() {
        MockSlashCommand mock1 = execCommand("win", Map.of(
                "goloscasa", 2,
                "golosfora", 0,
                "modalidade", "F"
        ));
        MockSlashCommand mock2 = execCommand("win", Map.of(
                "goloscasa", 1,
                "golosfora", 2,
                "modalidade", "I"
        ));
        messageReceiveListener.waitFinish();
        Assertions.assertEquals(mock1.getResultMessage(), "Operação 'win' realizada com sucesso.");
        Assertions.assertEquals(mock2.getResultMessage(), "Operação 'win' realizada com sucesso.");
        // System.out.println(mock1.getMessageSent());
        // System.out.println(mock2.getMessageSent());
        Assertions.assertEquals(mock1.getMessageSent(), Templates.messageWinners(Stream.of("teste1", "teste5", "teste13", "teste20", "teste21").sorted().toList(), game1));
        Assertions.assertEquals(mock2.getMessageSent(), Templates.messageWinners(Stream.of("teste5", "teste9", "teste16", "teste21").sorted().toList(), game2));
    }

    private void testSeason(MockSlashCommand mock, String temporada, String mode, int total, int previsions, int correct, int scored, int conceded) {
        assertResultEmbed(
                mock,
                "Estatísticas da temporada " + temporada,
                "Estas são as estatísticas da " + temporada + ". Aqui contém o número total de previsões, acertos, jogos, golos marcados, sofridos para todas as modalidade.",
                mode == null ? "* Em todas as modalidades" : "* Apenas para a modalidade " + mode,
                List.of(
                        new MessageEmbed.Field("Total de jogos *", Integer.toString(total), true),
                        new MessageEmbed.Field("Previsões *", Integer.toString(previsions), true),
                        new MessageEmbed.Field("Acertos *", Integer.toString(correct), true),
                        new MessageEmbed.Field("Golos marcados *", Integer.toString(scored), true),
                        new MessageEmbed.Field("Golos sofridos *", Integer.toString(conceded), true)
                ));
    }

    @Test
    public void test07() {
        MockSlashCommand mock1 = execCommand("season", null);
        MockSlashCommand mock2 = execCommand("season", Map.of("modalidade", "F"));
        MockSlashCommand mock3 = execCommand("season", Map.of("modalidade", "I"));
        messageReceiveListener.waitFinish();
        testSeason(mock1, "24/25", null, 2, 398, 9, 4, 1);
        testSeason(mock2, "24/25", "Futebol", 1, 199, 5, 2, 0);
        testSeason(mock3, "24/25", "Futsal", 1, 199, 4, 2, 1);
    }

    private void testUserStat(MockSlashCommand mock, String name, int position, int correct, float percentage, String temporada) {
        assertResultEmbed(
                mock,
                "Estatísticas de " + name,
                "Estas são as estatísticas de " + name + ". Aqui contém o número total de previsões e acertos para uma modalidade e temporada.",
                "Se pretende apagar estas informações, utilize o comando /delete.",
                List.of(
                        new MessageEmbed.Field("Modalidade", "X", true),
                        new MessageEmbed.Field("Temporada", temporada, true),
                        new MessageEmbed.Field("Posição", position + "º", false),
                        new MessageEmbed.Field("Total de previsões", "2", true),
                        new MessageEmbed.Field("Total de acertos", Integer.toString(correct), true),
                        new MessageEmbed.Field("Percentagem", String.format("%.2f%%", percentage), true)
                )
        );
    }

    private void testUserStat(MockSlashCommand mock, String name, int position, int correct, float percentage) {
        testUserStat(mock,name,position,correct,percentage,"Atual");
    }

    @Test
    public void test08() {
        MockSlashCommand mock1 = execCommand("stats", null, "teste2", "teste2");
        MockSlashCommand mock2 = execCommand("stats", null, "teste25", "teste25");
        MockSlashCommand mock3 = execCommand("stats", null, "teste1", "teste1");
        MockSlashCommand mock4 = execCommand("stats", null, "teste9", "teste9");
        MockSlashCommand mock5 = execCommand("stats", null, "teste5", "teste5");
        messageReceiveListener.waitFinish();
        testUserStat(mock1, "teste2", 3, 0, 0);
        testUserStat(mock2, "teste25", 3, 0, 0);
        testUserStat(mock3, "teste1", 2, 1, 50);
        testUserStat(mock4, "teste9", 2, 1, 50);
        testUserStat(mock5, "teste5", 1, 2, 100);
    }

    private void assertTopTable(String message, String[][] content, String temporada, String modalidade, List<Button> buttonList, int size) {
        String compare1 = "A classificação para a época " + (temporada != null ? temporada : "atual") + " e para a modalidade " + modalidade + " é:\n" + "```" +
                FlipTable.of(new String[]{"", "Nome", "Pontos"}, content) +
                "```";
        Assertions.assertEquals(message, compare1);
        if (size == 1) {
            Assertions.assertEquals(buttonList.size(), 1);
            Assertions.assertEquals(buttonList.getFirst().getId(), "next");
        } else {
            Assertions.assertEquals(buttonList.size(), 2);
            Assertions.assertEquals(buttonList.getFirst().getId(), "back");
            Assertions.assertEquals(buttonList.get(1).getId(), "next");
        }
    }

    private MockButtonReaction reactButton(String button, String mention) {
        MockButtonReaction mockButtonReaction = new MockButtonReaction(button, mention, messageReceiveListener);
        mockButtonReaction.sendMessage();
        messageReceiveListener.waitFinish();
        return mockButtonReaction;
    }

    private void testTop(MockSlashCommand mock, String mention, String temporada, String modalidade, List<String[][]> pages) {
        String page1 = mock.getResultMessage();
        Assertions.assertTrue(page1.length() <= 2000);
        List<Button> buttonList = mock.getButtons();
        assertTopTable(page1, pages.getFirst(), temporada, modalidade, buttonList, 1);
        MockButtonReaction mockButtonReaction1 = reactButton("next", mention);
        assertTopTable(mockButtonReaction1.getMessageSent(), pages.get(1), temporada, modalidade, mockButtonReaction1.getButtons(), 2);
        MockButtonReaction mockButtonReaction2 = reactButton("back", mention);
        assertTopTable(mockButtonReaction2.getMessageSent(), pages.getFirst(), temporada, modalidade, mockButtonReaction2.getButtons(), 1);
    }

    @Test
    public void test09() {
        MockSlashCommand mock1 = execCommand("top", null, "teste2", "teste2");
        MockSlashCommand mock2 = execCommand("top", Map.of("modalidade", "F"), "teste3", "teste3");
        MockSlashCommand mock3 = execCommand("top", Map.of("modalidade", "I"), "teste4", "teste4");
        messageReceiveListener.waitFinish();
        testTop(mock1, "teste2", null, "X", List.of(
                new String[][]{
                        {"1", "teste21", "2"},
                        {"1", "teste5", "2"},
                        {"2", "teste1", "1"},
                        {"2", "teste13", "1"},
                        {"2", "teste16", "1"},
                        {"2", "teste20", "1"},
                        {"2", "teste9", "1"},
                        {"3", "teste10", "0"},
                        {"3", "teste100", "0"},
                        {"3", "teste101", "0"},
                },
                new String[][]{
                        {"3", "teste102", "0"},
                        {"3", "teste103", "0"},
                        {"3", "teste104", "0"},
                        {"3", "teste105", "0"},
                        {"3", "teste106", "0"},
                        {"3", "teste107", "0"},
                        {"3", "teste108", "0"},
                        {"3", "teste109", "0"},
                        {"3", "teste11", "0"},
                        {"3", "teste110", "0"}
                }
        ));
        testTop(mock2, "teste3", null, "Futebol", List.of(
                new String[][]{
                        {"1", "teste1", "1"},
                        {"1", "teste13", "1"},
                        {"1", "teste20", "1"},
                        {"1", "teste21", "1"},
                        {"1", "teste5", "1"},
                        {"2", "teste10", "0"},
                        {"2", "teste100", "0"},
                        {"2", "teste101", "0"},
                        {"2", "teste102", "0"},
                        {"2", "teste103", "0"}
                },
                new String[][]{
                        {"2", "teste104", "0"},
                        {"2", "teste105", "0"},
                        {"2", "teste106", "0"},
                        {"2", "teste107", "0"},
                        {"2", "teste108", "0"},
                        {"2", "teste109", "0"},
                        {"2", "teste11", "0"},
                        {"2", "teste110", "0"},
                        {"2", "teste111", "0"},
                        {"2", "teste112", "0"}
                }
        ));
        testTop(mock3, "teste4", null, "Futsal", List.of(
                new String[][]{
                        {"1", "teste16", "1"},
                        {"1", "teste21", "1"},
                        {"1", "teste5", "1"},
                        {"1", "teste9", "1"},
                        {"2", "teste1", "0"},
                        {"2", "teste10", "0"},
                        {"2", "teste100", "0"},
                        {"2", "teste101", "0"},
                        {"2", "teste102", "0"},
                        {"2", "teste103", "0"},
                },
                new String[][]{
                        {"2", "teste104", "0"},
                        {"2", "teste105", "0"},
                        {"2", "teste106", "0"},
                        {"2", "teste107", "0"},
                        {"2", "teste108", "0"},
                        {"2", "teste109", "0"},
                        {"2", "teste11", "0"},
                        {"2", "teste110", "0"},
                        {"2", "teste111", "0"},
                        {"2", "teste112", "0"}
                }
        ));
    }

    @Test
    public void test10() {
        MockSlashCommand mock1 = execCommand("end", null);
        messageReceiveListener.waitFinish();
        Assertions.assertEquals(mock1.getResultMessage(), "Operação 'Fim de temporada' realizada com sucesso.");
        Assertions.assertEquals(mock1.getMessageSent(), "# Fim da temporada 24/25\n" +
                "A temporada **24/25** chegou ao fim. Nesta temporada houve um total de __398__ previsões, sendo que __9__ foram previsões corretas. (taxa de acerto: __2,26%__).\n" +
                "\n" +
                "**Vencedores**:\n" +
                "- teste21 (2 pontos, 2 previsões, __100%__).\n" +
                "- teste5 (2 pontos, 2 previsões, __100%__).\n" +
                "\n**Estatísticas da temporada**:\n" +
                "- Golos marcados: 4.\n" +
                "- Golos sofridos: 1.\n" +
                "- Vitórias: 2.\n" +
                "- Empates: 0.\n" +
                "- Derrotas: 0.\n" +
                "\n" +
                "Muitos parabéns aos vencedores :partying_face: e muito obrigado a todos os participantes.\nAté à próxima temporada **Gverreiros** :saluting_face: :crossed_swords: :red_circle: :white_circle:.\n" +
                ":notes: Foi no ano 21 :notes:");
    }

    @Test
    public void test11() {
        assertCommand("new","Operação 'Nova Temporada' realizada com sucesso.");
        assertCommand("stats", "Para a modalidade X, você não tem qualquer registo.");
        assertCommand("top", "Não há classificação para o modo X e temporada atual.");
        assertCommand("win", "Não há jogo para associar o resultado.", Map.of("modalidade", "F", "goloscasa", 3, "golosfora", 0));

        MockSlashCommand mock1 = execCommand("stats", Map.of("temporada","24/25"), "teste2", "teste2");
        MockSlashCommand mock2 = execCommand("top", Map.of("temporada","24/25"), "teste2", "teste2");
        messageReceiveListener.waitFinish();
        testUserStat(mock1, "teste2", 3, 0, 0, "24/25");
        testTop(mock2, "teste2", "24/25", "X", List.of(
                new String[][]{
                        {"1", "teste21", "2"},
                        {"1", "teste5", "2"},
                        {"2", "teste1", "1"},
                        {"2", "teste13", "1"},
                        {"2", "teste16", "1"},
                        {"2", "teste20", "1"},
                        {"2", "teste9", "1"},
                        {"3", "teste10", "0"},
                        {"3", "teste100", "0"},
                        {"3", "teste101", "0"},
                },
                new String[][]{
                        {"3", "teste102", "0"},
                        {"3", "teste103", "0"},
                        {"3", "teste104", "0"},
                        {"3", "teste105", "0"},
                        {"3", "teste106", "0"},
                        {"3", "teste107", "0"},
                        {"3", "teste108", "0"},
                        {"3", "teste109", "0"},
                        {"3", "teste11", "0"},
                        {"3", "teste110", "0"}
                }
        ));
    }

    @Test
    public void test12() {
        List<MockSlashCommand> deletes = new ArrayList<>(200);
        for (int i = 1; i < 200; i++)
            deletes.add(execCommand("delete", null, "teste" + i, "teste" + i));
        messageReceiveListener.waitFinish();
        deletes.forEach(m -> {
            List<Button> buttonList = m.getButtons();
            Assertions.assertEquals(buttonList.size(), 2);
            Assertions.assertEquals(buttonList.getFirst().getId(), "yes");
            Assertions.assertEquals(buttonList.get(1).getId(), "no");
        });
        List<MockButtonReaction> mockButtonReactions1 = deletes.stream().map(m -> new MockButtonReaction("no", m.getUser().getAsMention(), messageReceiveListener)).toList();
        mockButtonReactions1.forEach(MockButtonReaction::sendMessage);
        messageReceiveListener.waitFinish();
        mockButtonReactions1.forEach(m -> {
            Assertions.assertEquals(m.getMessageSent(), "Operação cancelada.");
            Assertions.assertTrue(m.getButtons2().isEmpty());
        });
        List<MockButtonReaction> mockButtonReactions2 = deletes.stream().map(m -> new MockButtonReaction("yes", m.getUser().getAsMention(), messageReceiveListener)).toList();
        mockButtonReactions2.forEach(MockButtonReaction::sendMessage);
        messageReceiveListener.waitFinish();
        mockButtonReactions2.forEach(m -> {
            Assertions.assertEquals(m.getMessageSent(), "Dados removidos com sucesso.");
            Assertions.assertTrue(m.getButtons2().isEmpty());
        });
        List<MockButtonReaction> mockButtonReactions3 = deletes.stream().map(m -> new MockButtonReaction("yes", m.getUser().getAsMention(), messageReceiveListener)).toList();
        mockButtonReactions3.forEach(MockButtonReaction::sendMessage);
        messageReceiveListener.waitFinish();
        mockButtonReactions3.forEach(m -> {
            Assertions.assertEquals(m.getMessageSent(), "Você não tem qualquer dados.");
            Assertions.assertTrue(m.getButtons2().isEmpty());
        });
    }


    @Test
    public void test13() {
        assertCommand("info", "# Estado do bot\n**Jogos Agendados**:\nNão há jogos a considerar\n\n**Jogos com prognósticos abertos**:\nNão há jogos a considerar\n\n**Jogos a decorrer**:\nNão há jogos a considerar\n\n**Jogos à espera de resultado**:\nNão há jogos a considerar\n");
        assertCommand("add", "Operação 'Adicionar Jogo' realizada com sucesso. Jogo " + game1 + " adicionado.", Map.of(
                "hora", game1.startGame().getHour(),
                "minuto", game1.startGame().getMinute(),
                "dia", game1.startGame().getDayOfMonth(),
                "mes", game1.startGame().getMonthValue(),
                "adversario", game1.opponent(),
                "modalidade", "F",
                "campo", "C"
        ));
        assertCommand("add", "Operação 'Adicionar Jogo' realizada com sucesso. Jogo " + game2 + " adicionado.", Map.of(
                "hora", game2.startGame().getHour(),
                "minuto", game2.startGame().getMinute(),
                "dia", game2.startGame().getDayOfMonth(),
                "mes", game2.startGame().getMonthValue(),
                "adversario", game2.opponent(),
                "modalidade", "I",
                "campo", "F"
        ));
        String game1Str = game1.toString(true, true);
        String game2Str = game2.toString(true, true);
        String games = "- " + game1Str + ".\n- " + game2Str + ".\n";
        assertCommand("info", "# Estado do bot\n**Jogos Agendados**:\n" + games + "\n**Jogos com prognósticos abertos**:\nNão há jogos a considerar\n\n**Jogos a decorrer**:\nNão há jogos a considerar\n\n**Jogos à espera de resultado**:\nNão há jogos a considerar\n");
        facade.openBets(game1.startGame().minusMinutes(5));
        assertCommand("info", "# Estado do bot\n**Jogos Agendados**:\nNão há jogos a considerar\n\n**Jogos com prognósticos abertos**:\n" + games + "\n**Jogos a decorrer**:\nNão há jogos a considerar\n\n**Jogos à espera de resultado**:\nNão há jogos a considerar\n");
        facade.closeBets(game1.startGame().plusSeconds(30));
        assertCommand("info", "# Estado do bot\n**Jogos Agendados**:\nNão há jogos a considerar\n\n**Jogos com prognósticos abertos**:\n- " + game2Str + ".\n\n**Jogos a decorrer**:\n- " + game1Str + ".\n\n**Jogos à espera de resultado**:\nNão há jogos a considerar\n");
        facade.closeBets(game1.startGame().plusMinutes(30));
        assertCommand("info", "# Estado do bot\n**Jogos Agendados**:\nNão há jogos a considerar\n\n**Jogos com prognósticos abertos**:\nNão há jogos a considerar\n\n**Jogos a decorrer**:\n" + games + "\n**Jogos à espera de resultado**:\nNão há jogos a considerar\n");
    }

    @Test
    public void test14() {
        MockSlashCommand mock = new MockSlashCommand("new", "test", "test", messageReceiveListener, null, false);
        mock.executeCommandWait();
        Assertions.assertEquals("Não tem permissões para executar o comando.", mock.getResultMessage());
    }

    @Test
    public void test15() {
        MockSlashCommand mock1 = new MockSlashCommand("help", "test", "test", messageReceiveListener, null, false);
        mock1.executeCommandWait();
        Assertions.assertTrue(mock1.getResultMessage().length() <= 2000);
        MockSlashCommand mock2 = new MockSlashCommand("help", "test", "test", messageReceiveListener, null, true);
        mock2.executeCommandWait();
        Assertions.assertTrue(mock2.getResultMessage().length() <= 2000);
    }
}