import org.botgverreiro.bot.threads.MyLocks;
import org.botgverreiro.facade.Facade;
import org.botgverreiro.model.classes.Game;
import org.botgverreiro.model.classes.Season;
import org.botgverreiro.model.classes.User;
import org.botgverreiro.model.enums.Field;
import org.botgverreiro.model.enums.Mode;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestFacade {
    private static final Facade facade = new Facade(true);

    @BeforeAll
    public static void beforeAll() {
        MyLocks.getInstance().addTestLocks();
        Assertions.assertEquals(facade.newSeason(), 0);
        Assertions.assertEquals(facade.addGames(List.of(
                Game.buildGameTest(Field.HOME, "Benfica", Mode.FOOTBALL, LocalDateTime.now()),
                Game.buildGameTest(Field.AWAY, "Porto", Mode.FOOTBALL, LocalDateTime.now().plusDays(1)),
                Game.buildGameTest(Field.NEUTRAL, "Vitória", Mode.FOOTBALL, LocalDateTime.now().plusDays(2)),
                Game.buildGameTest(Field.HOME, "Sporting", Mode.FUTSAL, LocalDateTime.now()),
                Game.buildGameTest(Field.AWAY, "Benfica", Mode.FUTSAL, LocalDateTime.now().plusDays(1)),
                Game.buildGameTest(Field.HOME, "Vitória", Mode.FUTSAL, LocalDateTime.now().plusDays(2))
        )), 0);
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

    @Test
    public void test1() {
        LocalDateTime now = LocalDateTime.now();
        List<Game> open = facade.openBets(now);
        List<Game> close = facade.closeBets(now);
        Assertions.assertEquals(facade.win(0, 0, Mode.FUTSAL).size(), 0);
        Assertions.assertEquals(facade.win(0, 0, Mode.FOOTBALL).size(), 0);
        Assertions.assertEquals(open.size(), 2);
        List<Game> assertGames = List.of(
                Game.buildGameTest(Field.HOME, "Sporting", Mode.FUTSAL, LocalDateTime.now()),
                Game.buildGameTest(Field.HOME, "Benfica", Mode.FOOTBALL, LocalDateTime.now())
        );
        open.forEach(a -> Assertions.assertTrue(assertGames.contains(a)));
        close.forEach(a -> Assertions.assertTrue(assertGames.contains(a)));
    }

    @Test
    public void test2() {
        LocalDateTime now = LocalDateTime.now().plusDays(1);
        List<Game> open = facade.openBets(now);
        Assertions.assertEquals(open.size(), 2);

        List<Game> assertGames = List.of(
                Game.buildGameTest(Field.AWAY, "Benfica", Mode.FUTSAL, LocalDateTime.now().plusDays(1)),
                Game.buildGameTest(Field.AWAY, "Porto", Mode.FOOTBALL, LocalDateTime.now().plusDays(1))
        );
        open.forEach(a -> Assertions.assertTrue(assertGames.contains(a)));

        Assertions.assertEquals(facade.newBet("test1", "test1", List.of(5, 7), List.of(0, 2)), 0);
        Assertions.assertEquals(facade.newBet("test2", "test2", List.of(5, 7), List.of(2, 2)), 0);
        Assertions.assertEquals(facade.newBet("test3", "test3", List.of(5, 7), List.of(4, 2)), 0);
        Assertions.assertEquals(facade.newBet("test4", "test4", List.of(5, 7), List.of(0, 2)), 0);
        Assertions.assertEquals(facade.newBet("test5", "test5", List.of(5, 7), List.of(8, 2)), 0);
        Assertions.assertEquals(facade.newBet("test6", "test6", List.of(5, 7), List.of(2, 2)), 0);

        List<Game> close = facade.closeBets(now);
        close.forEach(a -> Assertions.assertTrue(assertGames.contains(a)));

        Assertions.assertEquals(facade.newBet("test6", "test6", List.of(5), List.of(7)), 2);
        Assertions.assertEquals(facade.newBet("test7", "test7", List.of(5), List.of(8)), 2);
    }

    @Test
    public void test3() {
        Assertions.assertEquals(facade.win(5, 0, Mode.FOOTBALL).size(), 2);
        Assertions.assertEquals(facade.win(5, 0, Mode.FUTSAL).size(), 0);
        Assertions.assertEquals(facade.classification(Mode.FOOTBALL, null).size(), 6);
    }

    @Test
    public void test4() {
        LocalDateTime now = LocalDateTime.now().plusDays(2);

        List<Game> assertGames = List.of(
                Game.buildGameTest(Field.HOME, "Vitória", Mode.FUTSAL, LocalDateTime.now().plusDays(2)),
                Game.buildGameTest(Field.NEUTRAL, "Vitória", Mode.FOOTBALL, LocalDateTime.now().plusDays(2))
        );

        facade.openBets(now).forEach(a -> Assertions.assertTrue(assertGames.contains(a)));

        Assertions.assertEquals(facade.newBet("test1", "test1", List.of(5, 5), List.of(0, 0)), 0);
        Assertions.assertEquals(facade.newBet("test2", "test2", List.of(5, 5), List.of(2, 4)), 0);
        Assertions.assertEquals(facade.newBet("test3", "test3", List.of(5, 5), List.of(4, 2)), 0);
        Assertions.assertEquals(facade.newBet("test4", "test4", List.of(5, 5), List.of(0, 0)), 0);
        Assertions.assertEquals(facade.newBet("test5", "test5", List.of(5, 5), List.of(8, 8)), 0);
        Assertions.assertEquals(facade.newBet("test6", "test6", List.of(5, 5), List.of(2, 0)), 0);

        facade.closeBets(now).forEach(a -> Assertions.assertTrue(assertGames.contains(a)));

        Assertions.assertEquals(facade.win(5, 0, Mode.FOOTBALL).size(), 2);
        Assertions.assertEquals(facade.win(5, 0, Mode.FUTSAL).size(), 3);

        Assertions.assertEquals(facade.classification(Mode.FOOTBALL, null).size(), 6);
        Assertions.assertEquals(facade.classification(Mode.FUTSAL, null).size(), 6);
        Assertions.assertEquals(facade.classification(Mode.NATIONAL, null).size(), 0);
        Assertions.assertEquals(facade.classification(Mode.NONE, null).size(), 6);

        facade.classification(Mode.FOOTBALL, null).forEach(u -> {
            if (u.getMention().equals("test1") || u.getMention().equals("test4"))
                Assertions.assertEquals(u.getTotalPoints(), 6);
            else
                Assertions.assertEquals(u.getTotalPoints(), 2);
        });
        facade.classification(Mode.FOOTBALL, null).forEach(u -> Assertions.assertEquals(u.getTotalPredictions(), 2));

        facade.classification(Mode.FUTSAL, null).forEach(u -> {
            if (u.getMention().equals("test1") || u.getMention().equals("test4") || u.getMention().equals("test6"))
                Assertions.assertEquals(u.getTotalPoints(), 4);
            else
                Assertions.assertEquals(u.getTotalPoints(), 2);
        });
        facade.classification(Mode.FUTSAL, null).forEach(u -> Assertions.assertEquals(u.getTotalPredictions(), 2));

        facade.classification(Mode.NONE, null).forEach(u -> {
            if (u.getMention().equals("test1") || u.getMention().equals("test4"))
                Assertions.assertEquals(u.getTotalPoints(), 10);
            else if (u.getMention().equals("test6"))
                Assertions.assertEquals(u.getTotalPoints(), 6);
            else
                Assertions.assertEquals(u.getTotalPoints(), 4);
        });
        facade.classification(Mode.NONE, null).forEach(u -> Assertions.assertEquals(u.getTotalPredictions(), 4));

    }

    @Test
    public void test5() {
        User user1 = facade.statsUser("test1", Mode.FOOTBALL, null);
        User user2 = facade.statsUser("test1", Mode.NONE, null);
        User user3 = facade.statsUser("test2", Mode.FOOTBALL, null);
        User user4 = facade.statsUser("test2", Mode.NONE, null);
        User user5 = facade.statsUser("test6", Mode.FOOTBALL, null);
        User user6 = facade.statsUser("test6", Mode.NONE, null);
        User user7 = facade.statsUser("test6", Mode.FUTSAL, null);

        Assertions.assertEquals(user1.getTotalPredictions(), 2);
        Assertions.assertEquals(user1.getTotalPoints(), 6);
        Assertions.assertEquals(user2.getTotalPredictions(), 4);
        Assertions.assertEquals(user2.getTotalPoints(), 10);
        Assertions.assertEquals(user3.getTotalPredictions(), 2);
        Assertions.assertEquals(user3.getTotalPoints(), 2);
        Assertions.assertEquals(user4.getTotalPredictions(), 4);
        Assertions.assertEquals(user4.getTotalPoints(), 4);
        Assertions.assertEquals(user5.getTotalPredictions(), 2);
        Assertions.assertEquals(user5.getTotalPoints(), 2);
        Assertions.assertEquals(user6.getTotalPredictions(), 4);
        Assertions.assertEquals(user6.getTotalPoints(), 6);
        Assertions.assertEquals(user7.getTotalPredictions(), 2);
        Assertions.assertEquals(user7.getTotalPoints(), 4);
    }

    @Test
    public void test6() {
        Season season1 = facade.statsSeason(null, Mode.FOOTBALL);
        Assertions.assertEquals(season1.getTotalPredictions(), 12);
        Assertions.assertEquals(season1.getCorrectPredictions(), 4);
        Assertions.assertEquals(season1.getConceded(), 5);
        Assertions.assertEquals(season1.getScored(), 5);

        Season season2 = facade.statsSeason(null, Mode.NONE);
        Assertions.assertEquals(season2.getTotalPredictions(), 24);
        Assertions.assertEquals(season2.getCorrectPredictions(), 7);
        Assertions.assertEquals(season2.getConceded(), 10);
        Assertions.assertEquals(season2.getScored(), 10);
    }
}
