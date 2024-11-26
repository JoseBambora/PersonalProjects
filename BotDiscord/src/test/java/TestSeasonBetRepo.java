import org.botgverreiro.dao.repositories.SeasonBetRepo;
import org.botgverreiro.model.classes.SeasonBet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestSeasonBetRepo {
    @Test
    public void test1() {
        List<SeasonBet> seasonBets = List.of(
                new SeasonBet("test_4", "test_2", 1, "LE", 3, 3, 3, "abc", "abc", "abc", "abc"),
                new SeasonBet("test_2", "test_2", 1, "LE", 3, 3, 3, "abc", "abc", "abc", "abc"),
                new SeasonBet("test_1", "test_2", 1, "LE", 3, 3, 3, "abc", "abc", "abc", "abc")
        );
        int code = SeasonBetRepo.writeBets(seasonBets);
        Assertions.assertEquals(code, 0);
        Assertions.assertEquals(SeasonBetRepo.readBets(), seasonBets);
    }
}
