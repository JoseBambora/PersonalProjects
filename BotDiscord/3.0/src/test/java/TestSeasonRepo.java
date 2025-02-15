import org.botgverreiro.bot.threads.MyLocks;
import org.botgverreiro.dao.repositories.SeasonRepo;
import org.botgverreiro.dao.utils.EnvDB;
import org.botgverreiro.model.classes.Season;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import java.util.List;
import java.util.Objects;

import static org.botgverreiro.dao.utils.Transactions.commitTransaction;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSeasonRepo {
    private static final SeasonRepo repo = new SeasonRepo(EnvDB.database_test);

    private static List<Season> generator() {
        return List.of(
                new Season("23-24", 0, 0, 0, 0, 0, 0, 0, 0),
                new Season("24-25", 0, 0, 0, 0, 0, 0, 0, 0),
                new Season("25-26", 0, 0, 0, 0, 0, 0, 0, 0),
                new Season("26-27", 0, 0, 0, 0, 0, 0, 0, 0),
                new Season("27-28", 0, 0, 0, 0, 0, 0, 0, 0)
        );
    }

    @BeforeAll
    public static void beforeAll() {
        MyLocks.getInstance().addTestLocks();
    }

    private boolean all(List<Boolean> booleans) {
        return booleans.stream().reduce((b1, b2) -> b1 && b2).orElse(false);
    }

    @Test
    public void test1() {
        int res = commitTransaction(EnvDB.database_test, (configuration -> {
            List<Season> seasons = generator();
            seasons.forEach(s -> repo.insertSeason(configuration, s));
            Assertions.assertTrue(all(seasons.stream().map(Season::getSeason).map(s -> repo.getSeason(configuration, s)).map(Objects::nonNull).toList()));
        }));
        Assertions.assertEquals(res, 0);
    }

    @Test
    public void test2() {
        int res = commitTransaction(EnvDB.database_test, (configuration -> {
            List<Season> seasons = generator();
            seasons = seasons.stream().map(s -> s.endGame(1, 1, 1, 1, 0)).toList();
            seasons.forEach(s -> repo.updateSeason(configuration, s));
            List<Season> getSeasons = seasons.stream().map(Season::getSeason).map(s -> repo.getSeason(configuration, s)).toList();
            Assertions.assertTrue(all(getSeasons.stream().map(Objects::nonNull).toList()));
            Assertions.assertTrue(all(getSeasons.stream().map(s -> s.getGames() == 1).toList()));
            Assertions.assertTrue(all(getSeasons.stream().map(s -> s.getScored() == 1).toList()));
            Assertions.assertTrue(all(getSeasons.stream().map(s -> s.getConceded() == 1).toList()));
            Assertions.assertTrue(all(getSeasons.stream().map(s -> s.getTotalPredictions() == 1).toList()));
            Assertions.assertTrue(all(getSeasons.stream().map(s -> s.getCorrectPredictions() == 1).toList()));
            Assertions.assertTrue(all(getSeasons.stream().map(s -> s.getTotalLoses() == 1).toList()));
            Assertions.assertTrue(all(getSeasons.stream().map(s -> s.getTotalDraws() == 0).toList()));
            Assertions.assertTrue(all(getSeasons.stream().map(s -> s.getTotalWins() == 0).toList()));
        }));
        Assertions.assertEquals(res, 0);
    }

    @Test
    public void test3() {
        int res = commitTransaction(EnvDB.database_test, (configuration -> {
            Season ls1 = repo.getLastSeason(configuration);
            Season ls2 = generator().get(4).endGame(1, 1, 1, 1, 0);
            Assertions.assertEquals(ls1, ls2);
        }));
        Assertions.assertEquals(res, 0);
    }

    @Test
    public void test4() {
        int res = commitTransaction(EnvDB.database_test, (configuration -> Assertions.assertTrue(all(generator().stream().map(Season::getSeason).map(s -> repo.deleteSeason(configuration, s)).map(i -> i == 1).toList()))));
        Assertions.assertEquals(res, 0);
    }
}
