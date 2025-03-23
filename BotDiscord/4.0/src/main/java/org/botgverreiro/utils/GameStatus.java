package org.botgverreiro.utils;

public enum GameStatus {
    TO_OPEN("Futuro",0),
    OPEN("Aberto",1),
    CLOSE("Fechado",2),
    FINISHED("Terminado",3),
    ;
    private final String stateString;
    private final int numberStatus;

    GameStatus(String stateString, int numberStatus) {
        this.stateString = stateString;
        this.numberStatus = numberStatus;
    }

    public int getStatus() {
        return numberStatus;
    }
    @Override
    public String toString() {
        return stateString;
    }
}
