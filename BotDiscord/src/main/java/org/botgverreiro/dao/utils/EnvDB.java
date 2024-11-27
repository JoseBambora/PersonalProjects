package org.botgverreiro.dao.utils;

/**
 * Name of the databases.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class EnvDB {
    public static final String database_football = "jdbc:sqlite:" + System.getenv("DATABASE_FOOTBALL");
    public static final String database_futsal = "jdbc:sqlite:" + System.getenv("DATABASE_FUTSAL");
    public static final String database_portugal = "jdbc:sqlite:" + System.getenv("DATABASE_PT");
    public static final String database_test = "jdbc:sqlite:" + System.getenv("DATABASE_TEST");
    public static final String database_test_football = "jdbc:sqlite:" + System.getenv("DATABASE_TEST_FOOTBALL");
    public static final String database_test_futsal = "jdbc:sqlite:" + System.getenv("DATABASE_TEST_FUTSAL");
    public static final String database_test_portugal = "jdbc:sqlite:" + System.getenv("DATABASE_TEST_PORTUGAL");
    public static final String file_season_bets = System.getenv("FILE_SEASON_BETS");
}
