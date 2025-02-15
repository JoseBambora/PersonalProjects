package org.botgverreiro.models;

import org.botgverreiro.classes.Mode;
import org.botgverreiro.classes.Season;
import org.botgverreiro.tables.Modes;
import org.botgverreiro.tables.Seasons;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class SeasonRepository {
    public List<Season> getAllModes() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:files/dbteste.db")) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
            return create.select().from(Seasons.SEASONS)
                    .join(Modes.MODES)
                    .on(Seasons.SEASONS.SEASON_MODE.eq(Modes.MODES.MODE_ID))
                    .fetch()
                    .stream()
                    .map(r -> r.into(Season.class).setMode(r.into(Mode.class)))
                    .toList();
        } catch (SQLException _) {
            return Collections.emptyList();
        }
    }
}
