package org.botgverreiro;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParserInfo {
    private static ParserInfo instance;
    private final Map<String, String> content;

    private ParserInfo() {
        content = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("files/config/config.txt"))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] keyvalue = line.split(" ");
                content.put(keyvalue[0], keyvalue[1]);
            }
        } catch (IOException ignored) {
        }
    }

    public static ParserInfo getInstance() {
        if (instance == null)
            instance = new ParserInfo();
        return instance;
    }

    public String getValueString(String key) {
        return content.get(key);
    }

    public Integer getValueInteger(String key) {
        return Integer.parseInt(content.get(key));
    }
}
