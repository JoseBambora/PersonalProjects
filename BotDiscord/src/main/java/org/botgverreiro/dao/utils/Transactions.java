package org.botgverreiro.dao.utils;

import org.botgverreiro.bot.threads.MyLocks;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * Class that handles transactions.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class Transactions {

    /**
     * Just used to see if an operation ended successfully (0) or not (1). (inserts, updates)
     *
     * @param db       Database name.
     * @param consumer Transaction.
     * @return 0 if success, otherwise 1.
     */
    public static int commitTransaction(String db, Consumer<Configuration> consumer) {
        MyLocks.getInstance().lockWrite(db);
        try (Connection connection = DriverManager.getConnection(db)) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
            create.transaction(consumer::accept);
        } catch (SQLException _) {
            return 1;
        } finally {
            MyLocks.getInstance().unlockWrite(db);
        }
        return 0;
    }

    /**
     * For operation to get the data.
     *
     * @param db          Database name.
     * @param consumer    Transaction.
     * @param errorReturn Return error when something went wrong.
     * @param <T>         Can be anything (Integer, Season, Prediction, ...).
     * @return The data that I want.
     */
    public static <T> T getData(String db, Function<Configuration, T> consumer, T errorReturn) {
        MyLocks.getInstance().lockWrite(db);
        try (Connection connection = DriverManager.getConnection(db)) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
            return create.transactionResult(consumer::apply);
        } catch (SQLException _) {
            return errorReturn;
        } finally {
            MyLocks.getInstance().unlockWrite(db);
        }
    }
}
