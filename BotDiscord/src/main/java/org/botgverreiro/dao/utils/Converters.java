package org.botgverreiro.dao.utils;

import org.botgverreiro.model.classes.Prediction;
import org.botgverreiro.model.classes.Season;
import org.botgverreiro.model.classes.User;
import org.jooq.Field;
import org.jooq.Record;

/**
 * Basic class to convert database entries to objects.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class Converters {
    public static Prediction toPredictions(Record r, Field<String> userColumn, Field<Integer> gameDayColumn, Field<Integer> homeColumn, Field<Integer> awayColumn) {
        return new Prediction(r.get(userColumn), r.get(homeColumn), r.get(awayColumn), r.get(gameDayColumn));
    }

    public static Season toSeason(Record r, Field<String> seasonColumn, Field<Integer> gamesColumn, Field<Integer> scoredColumn, Field<Integer> concededColumn, Field<Integer> totalPredictionsColumn, Field<Integer> totalCorrectColumn, Field<Integer> totalWinColumn, Field<Integer> totalDrawColumn, Field<Integer> totalLosesColumn) {
        if (r != null)
            return new Season(
                    r.get(seasonColumn),
                    r.get(gamesColumn),
                    r.get(scoredColumn),
                    r.get(concededColumn),
                    r.get(totalPredictionsColumn),
                    r.get(totalCorrectColumn),
                    r.get(totalWinColumn),
                    r.get(totalDrawColumn),
                    r.get(totalLosesColumn));
        else
            return null;
    }

    public static User toUser(Record r, Field<String> mentionColumn, Field<String> nameColumn, Field<Integer> totalPredictionColumn, Field<Integer> totalPointsColumn, int position) {
        if (r != null)
            return new User(
                    r.get(mentionColumn),
                    r.get(nameColumn),
                    r.get(totalPredictionColumn),
                    r.get(totalPointsColumn),
                    position);
        else
            return null;
    }
}
