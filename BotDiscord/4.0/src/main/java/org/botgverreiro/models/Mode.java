package org.botgverreiro.models;

import jakarta.persistence.Column;
import org.botgverreiro.tables.Modes;
import org.jooq.DSLContext;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class Mode {
    @Column(name = "MODE_ID")
    private int modeId;
    @Column(name = "MODE_NAME")
    private String modeName;

    @Override
    public String toString() {
        return "("  + modeId+ "," + modeName  + ")";
    }


    /*
     * ===================
     * Repository Methods
     * ===================
     */
    /**
     * Get all the available modes.
     * @return A list containing all the modes.
     */
    public static CompletionStage<List<Mode>> getAllModes(DSLContext context) {
        return context
                .selectFrom(Modes.MODES)
                .fetchAsync()
                .thenApply(r -> r.into(Mode.class));
    }

    /**
     * Inserts a new mode.
     * @param mode Mode name to insert.
     * @return 1 if success, 0 otherwise.
     */
    public static CompletionStage<Integer> insertMode(DSLContext context,String mode) {
        return context
                .insertInto(Modes.MODES)
                .set(Modes.MODES.MODE_NAME, mode)
                .executeAsync();
    }
}