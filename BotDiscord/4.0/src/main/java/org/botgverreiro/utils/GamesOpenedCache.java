package org.botgverreiro.utils;


import org.botgverreiro.models.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GamesOpenedCache {
    private final ReadWriteLock readWriteLock;
    private final List<Game> gamesList;
    private GamesOpenedCache() {
        gamesList = new ArrayList<>();
        readWriteLock = new ReentrantReadWriteLock();
    }

    public void addOpenGames(List<Game> gameList) {
        readWriteLock.writeLock().lock();
        this.gamesList.addAll(gameList);
        readWriteLock.writeLock().unlock();
    }

    public void removeOpenGames(List<Game> gameList) {
        readWriteLock.writeLock().lock();
        this.gamesList.removeAll(gameList);
        readWriteLock.writeLock().unlock();
    }

    public List<Integer> getOpenGamesIds() {
        readWriteLock.readLock().lock();
        List<Integer> res = gamesList.stream().map(Game::getGameId).toList();
        readWriteLock.readLock().unlock();
        return res;
    }

    public List<Game> getOpenGames() {
        readWriteLock.readLock().lock();
        List<Game> res = new ArrayList<>(gamesList);
        readWriteLock.readLock().unlock();
        return res;
    }


    private static GamesOpenedCache gamesOpenedCache;
    public static GamesOpenedCache getInstance() {
        if(gamesOpenedCache == null)
            gamesOpenedCache = new GamesOpenedCache();
        return gamesOpenedCache;
    }
}
