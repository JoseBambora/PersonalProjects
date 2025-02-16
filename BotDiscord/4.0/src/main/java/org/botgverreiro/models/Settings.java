package org.botgverreiro.models;

import org.jooq.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

public class Settings {
    private static final Logger log = LoggerFactory.getLogger(Settings.class);
    private static Connection connection = null;
    private static DSLContext context = null;

    private static void start() {
        try {
            connection = DriverManager.getConnection(System.getenv("DB_FILE"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        context = DSL.using(connection, SQLDialect.SQLITE);
    }

    private static DSLContext getContext() {
        if(connection == null)
            start();
        return context;
    }

    public static <T> T commitTransaction(Function<DSLContext,T> function) {
        return getContext().transactionResult(c -> function.apply(DSL.using(c)));
    }

    public static void commitTransactionNoResult(Consumer<DSLContext> function) {
        getContext().transaction(c -> function.accept(DSL.using(c)));
    }

    public static void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
                log.error("SQL Error Commit {}", String.valueOf(e));
            } catch (SQLException ex) {
                log.error("SQL Error RollBack {}", String.valueOf(ex));
            }
        }
    }

    public static void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("SQL Error {}", String.valueOf(e));
        }
    }
}
