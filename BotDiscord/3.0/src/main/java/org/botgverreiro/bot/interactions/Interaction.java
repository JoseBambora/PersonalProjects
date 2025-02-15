package org.botgverreiro.bot.interactions;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This class is pretty simple. This is a super class for interaction.
 * Both InteractionDelete and InteractionTop inherent from this class.
 *
 * @author JoséBambora
 * @version 1.0
 * @see InteractionTop
 * @see InteractionDelete
 */
public abstract class Interaction {
    private final String user;
    private final String command;
    private String id;
    private LocalDateTime lastUpdate;

    public Interaction(String user, String command) {
        this.id = "";
        this.user = user;
        lastUpdate = LocalDateTime.now();
        this.command = command;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void newInteraction() {
        lastUpdate = LocalDateTime.now();
    }

    public boolean isInactive(LocalDateTime now, int delay) {
        return lastUpdate.plusSeconds(delay).isBefore(now);
    }

    public abstract String clickedButtonMessage(ButtonInteractionEvent event);

    public abstract String getInitialMessage();

    public abstract List<Button> getButtons();

    public String getCommand() {
        return String.format("`%s`", command);
    }
}
