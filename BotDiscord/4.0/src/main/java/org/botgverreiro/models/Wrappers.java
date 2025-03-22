package org.botgverreiro.models;

import org.jooq.Record;

public class Wrappers {

    public static Game toGame(Record g) {
        return g.into(Game.class).setGameOpponent(g.into(Team.class)).setMode(g.into(Mode.class)).setSeason(g.into(Season.class));
    }
}
