package org.botgverreiro.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class ExceptionsHandler {
    private static void write(Throwable throwable) {
        try {
            PrintWriter out = new PrintWriter(System.getenv("FILE_ERRORS"));
            String stringBuilder = "===============================================" +
                    LocalDateTime.now() +
                    "===============================================\n" +
                    throwable.toString() +
                    "\n\n";
            out.println(stringBuilder);
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

    public static Void storeException(Throwable throwable) {
        write(throwable);
        return null;
    }

    public static int storeExceptionInt(Throwable throwable) {
        write(throwable);
        return -1;
    }
}
