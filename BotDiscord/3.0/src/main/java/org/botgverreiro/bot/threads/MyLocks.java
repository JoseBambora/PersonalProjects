package org.botgverreiro.bot.threads;

import org.botgverreiro.dao.utils.EnvDB;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is just the class that manage the locks through the application.
 * Singleton design pattern is used here.
 * All the locks that I need are created in the constructor.
 * The locks that are used are ReadWrite locks, in order to achieve a better performance.
 * Due to the fact that SQLite does not handle concurrency, I have also added locks for it.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class MyLocks {
    private static MyLocks instance;
    private final Map<String, ReadWriteLock> locks;

    private MyLocks() {
        locks = new HashMap<>();
        locks.put("nextGames", new ReentrantReadWriteLock());
        locks.put("currentGame", new ReentrantReadWriteLock());
        locks.put("open", new ReentrantReadWriteLock());
        locks.put("waitingResults", new ReentrantReadWriteLock());
        locks.put("openMods", new ReentrantReadWriteLock());
        locks.put("interactions", new ReentrantReadWriteLock());
        locks.put("seasonBets", new ReentrantReadWriteLock());
        locks.put("openSeason", new ReentrantReadWriteLock());
        locks.put(EnvDB.database_portugal, new ReentrantReadWriteLock());
        locks.put(EnvDB.database_football, new ReentrantReadWriteLock());
        locks.put(EnvDB.database_futsal, new ReentrantReadWriteLock());
    }

    public static MyLocks getInstance() {
        if (instance == null)
            instance = new MyLocks();
        return instance;
    }

    public void addTestLocks() {
        locks.put(EnvDB.database_test_portugal, new ReentrantReadWriteLock());
        locks.put(EnvDB.database_test_football, new ReentrantReadWriteLock());
        locks.put(EnvDB.database_test_futsal, new ReentrantReadWriteLock());
        locks.put(EnvDB.database_test, new ReentrantReadWriteLock());
    }

    public void lockWrite(String... names) {
        for (String name : names)
            locks.get(name).writeLock().lock();
    }

    public void unlockWrite(String... names) {
        for (String name : names)
            locks.get(name).writeLock().unlock();
    }

    public void lockRead(String... names) {
        for (String name : names)
            locks.get(name).readLock().lock();
    }

    public void unlockRead(String... names) {
        for (String name : names)
            locks.get(name).readLock().unlock();
    }
}
