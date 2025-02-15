package org.botgverreiro.models;

import org.botgverreiro.classes.Mode;
import org.botgverreiro.tables.Modes;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ModeRepository {
    public List<Mode> getAllModes() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:files/dbteste.db")) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
            return create.select().from(Modes.MODES).fetchInto(Mode.class);
        } catch (SQLException _) {
            return Collections.emptyList();
        }
    }
}
