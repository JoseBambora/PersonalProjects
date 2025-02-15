package org.botgverreiro.bot.interactions;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.botgverreiro.facade.Facade;

import java.util.Collections;
import java.util.List;

/**
 * Simple class that handles interactions for the slash command <b>/delete</b>.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class InteractionDelete extends Interaction {
    private final String question;
    private final Facade facade;
    private boolean decided;

    public InteractionDelete(String user, Facade facade) {
        super(user, "/delete");
        this.question = "Tem a certeza que quer apagar os seus dados?";
        this.facade = facade;
        this.decided = false;
    }

    @Override
    public String clickedButtonMessage(ButtonInteractionEvent event) {
        decided = true;
        if (event.getComponentId().equals("yes"))
            return facade.deleteUser(getUser()) > 0 ? "Dados removidos com sucesso." : "Você não tem qualquer dados.";
        else
            return "Operação cancelada.";
    }

    @Override
    public String getInitialMessage() {
        return question;
    }

    @Override
    public List<Button> getButtons() {
        return decided ? Collections.emptyList() : List.of(
                Button.secondary("yes", Emoji.fromUnicode("✅")),
                Button.secondary("no", Emoji.fromUnicode("❌"))
        );
    }
}
