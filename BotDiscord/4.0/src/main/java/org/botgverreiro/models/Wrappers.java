package org.botgverreiro.models;

import org.jooq.Record;

import java.util.List;
import java.util.function.Function;

public class Wrappers {

    public static Game toGame(Record g) {
        return g.into(Game.class).setGameOpponent(g.into(Team.class)).setMode(g.into(Mode.class)).setSeason(g.into(Season.class));
    }

    public static Prediction toPrediction(Record p) {
        return p.into(Prediction.class).setInfo(p.into(Game.class), p.into(User.class));
    }

    public static <T> List<T> converter(List<Record> recordList, Function<Record, T> converter) {
        return recordList.stream().map(converter).toList();
    }
}
