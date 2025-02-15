package org.botgverreiro.model.enums;

/**
 * Just a class that converts Enums to String and vice-versa.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class ConverterString {
    public static String toStringMode(Mode mode) {
        return switch (mode) {
            case FOOTBALL -> "Futebol";
            case FUTSAL -> "Futsal";
            case NATIONAL -> "Seleção";
            case NONE -> "X";
        };
    }

    public static Mode fromStringMode(String string) {
        if (string == null) return Mode.NONE;
        return switch (string) {
            case "F" -> Mode.FOOTBALL;
            case "I" -> Mode.FUTSAL;
            case "P" -> Mode.NATIONAL;
            default -> Mode.NONE;
        };
    }

    public static String toStringField(Field field) {
        return switch (field) {
            case HOME -> "Casa";
            case AWAY -> "Fora";
            case NEUTRAL -> "Neutro";
            default -> "X";
        };
    }

    public static Field fromStringField(String string) {
        if (string == null) return Field.ERROR;
        return switch (string) {
            case "C" -> Field.HOME;
            case "F" -> Field.AWAY;
            case "N" -> Field.NEUTRAL;
            default -> Field.ERROR;
        };
    }
}
