package org.botgverreiro;


import org.botgverreiro.models.ModeRepository;
import org.botgverreiro.models.SeasonRepository;

public class Main {
    public static void main(String[] args) {
        System.out.println(new ModeRepository().getAllModes());
        System.out.println(new SeasonRepository().getAllModes());
    }
}