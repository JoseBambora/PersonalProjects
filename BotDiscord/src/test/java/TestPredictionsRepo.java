import org.botgverreiro.bot.threads.MyLocks;
import org.botgverreiro.dao.repositories.PredictionsRepo;
import org.botgverreiro.dao.utils.EnvDB;
import org.botgverreiro.model.classes.Prediction;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

import static org.botgverreiro.dao.utils.Transactions.commitTransaction;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPredictionsRepo {
    private final static PredictionsRepo repo = new PredictionsRepo(EnvDB.database_test);

    @BeforeAll
    public static void beforeAll() {
        MyLocks.getInstance().addTestLocks();
    }

    @BeforeAll
    @AfterAll
    public static void deleteEverything() {
        int res1 = commitTransaction(EnvDB.database_test, ((configuration) -> repo.deletePredictions(configuration, 1)));
        int res2 = commitTransaction(EnvDB.database_test, ((configuration) -> repo.deletePredictions(configuration, 2)));
        Assertions.assertEquals(res1, 0);
        Assertions.assertEquals(res2, 0);
    }

    private List<Prediction> generator() {
        return List.of(
                new Prediction("a", 2, 2, 1),
                new Prediction("b", 1, 0, 1),
                new Prediction("c", 3, 1, 1),
                new Prediction("d", 4, 3, 1),
                new Prediction("e", 5, 1, 1),
                new Prediction("f", 0, 2, 1)
        );
    }

    private List<Prediction> generator2() {
        return List.of(
                new Prediction("a", 2, 2, 2),
                new Prediction("b", 1, 0, 2),
                new Prediction("c", 3, 1, 2),
                new Prediction("d", 4, 3, 2),
                new Prediction("e", 5, 1, 2),
                new Prediction("f", 0, 2, 2)
        );
    }

    private boolean all(List<Boolean> booleans) {
        return booleans.stream().reduce((b1, b2) -> b1 && b2).orElse(false);
    }

    @Test
    public void test1() {
        int res = commitTransaction(EnvDB.database_test, (configuration -> {
            List<Prediction> pres = generator();
            Assertions.assertTrue(all(pres.stream().map(pred -> repo.insertOrUpdatePrediction(configuration, pred)).map(n -> n == 0).toList()));
            Assertions.assertTrue(all(generator2().stream().map(pred -> repo.insertOrUpdatePrediction(configuration, pred)).map(n -> n == 0).toList()));
            Assertions.assertTrue(all(repo.getPredictions(configuration, 1).stream().map(pres::contains).toList()));
        }));
        Assertions.assertEquals(res, 0);
    }

    @Test
    public void test2() {
        int res = commitTransaction(EnvDB.database_test, (configuration -> {
            List<Prediction> pres = generator();
            Assertions.assertTrue(all(pres.stream().map(pred -> repo.insertOrUpdatePrediction(configuration, pred)).map(n -> n == 1).toList()));
            Assertions.assertTrue(all(repo.getPredictions(configuration, 1).stream().map(pres::contains).toList()));
        }));
        Assertions.assertEquals(res, 0);
    }

    @Test
    public void test3() {
        int res = commitTransaction(EnvDB.database_test, (configuration -> {
            Assertions.assertEquals(repo.getPredictions(configuration, 1).size(), 6);
            Assertions.assertEquals(repo.getPredictions(configuration, 2).size(), 6);
            Assertions.assertEquals(repo.deletePredictions(configuration, 1), 6);
            Assertions.assertEquals(repo.getPredictions(configuration, 1).size(), 0);
            Assertions.assertEquals(repo.getPredictions(configuration, 2).size(), 6);
            Assertions.assertEquals(repo.deletePredictions(configuration, 2), 6);
            Assertions.assertEquals(repo.getPredictions(configuration, 2).size(), 0);
        }));
        Assertions.assertEquals(res, 0);
    }
}
