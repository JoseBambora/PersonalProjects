import org.botgverreiro.bot.threads.MyLocks;
import org.botgverreiro.dao.repositories.PositionsRepo;
import org.botgverreiro.dao.utils.EnvDB;
import org.botgverreiro.model.classes.User;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

import static org.botgverreiro.dao.utils.Transactions.commitTransaction;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPositionsRepo {
    private static final PositionsRepo repo = new PositionsRepo(EnvDB.database_test);

    private static List<String> generatorSeason() {
        return List.of(
                "23-24",
                "24-25",
                "25-26",
                "26-27",
                "27-28"
        );
    }

    private static List<String> generatorUser() {
        return List.of(
                "a",
                "b",
                "c",
                "d",
                "e",
                "f"
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
        int res = commitTransaction(EnvDB.database_test, configuration -> {
            List<String> users = generatorUser();
            String season = generatorSeason().getFirst();
            users.forEach(u -> repo.insertUser(configuration, u, "test", season));
            repo.noPoints(configuration, users, season);
            repo.incrementPoints(configuration, users, season);
            Assertions.assertTrue(all(repo.getPositionsSeason(configuration, season).stream().map(User::getMention).map(users::contains).toList()));
            repo.noPoints(configuration, users, season);
            repo.incrementPoints(configuration, users, season);
            Assertions.assertTrue(all(users.stream().map(u -> repo.getPointsUser(configuration, u, season)).map(p -> p == 2).toList()));
            List<String> subList = users.subList(0, 2);
            List<String> rest = users.subList(2, users.size());
            repo.noPoints(configuration, users, season);
            repo.incrementPoints(configuration, subList, season);
            Assertions.assertTrue(all(subList.stream().map(u -> repo.getPointsUser(configuration, u, season)).map(p -> p == 3).toList()));
            Assertions.assertTrue(all(rest.stream().map(u -> repo.getPointsUser(configuration, u, season)).map(p -> p == 2).toList()));
            Assertions.assertTrue(all(repo.getPositionsSeason(configuration, season).subList(0, 2).stream().map(User::getMention).map(subList::contains).toList()));
            Assertions.assertTrue(all(repo.getPositionsSeason(configuration, season).subList(2, users.size()).stream().map(User::getMention).map(rest::contains).toList()));
        });
        Assertions.assertEquals(res, 0);
    }

    @Test
    public void test2() {
        int res = commitTransaction(EnvDB.database_test, (configuration ->
                Assertions.assertTrue(all(generatorUser().stream().map(u -> repo.deleteUser(configuration, u)).map(i -> i == 1).toList()))));
        Assertions.assertEquals(res, 0);
    }

}
