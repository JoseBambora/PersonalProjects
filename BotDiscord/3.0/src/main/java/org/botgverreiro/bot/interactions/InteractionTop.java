package org.botgverreiro.bot.interactions;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.botgverreiro.bot.utils.TablePrinter;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple class that handles interactions for the slash command <b>/top</b>.
 *
 * @author JoséBambora
 * @version 1.0
 */
public class InteractionTop extends Interaction {
    private final List<String> headers;
    private final List<List<List<String>>> pages;
    private final String headerMessage;
    private int page;

    public InteractionTop(String user, List<String> headers, List<List<List<String>>> pages, String temporada, String modalidade) {
        super(user, "/top");
        this.headers = headers;
        this.pages = pages;
        page = 0;
        headerMessage = "A classificação para a época " + temporada + " e para a modalidade " + modalidade + " é:\n";
    }

    public String createMessage(int move) {
        page += move;
        StringBuilder stringBuilder = new StringBuilder(headerMessage);
        List<List<String>> data = pages.get(page);
        stringBuilder.append("```");
        stringBuilder.append(TablePrinter.formatTable(headers, data));
        stringBuilder.append("```");
        return stringBuilder.toString();
    }

    @Override
    public String clickedButtonMessage(ButtonInteractionEvent event) {
        super.newInteraction();
        int num = switch (event.getComponentId()) {
            case "back" -> -1;
            case "next" -> 1;
            default -> 0;
        };
        return this.createMessage(num);
    }

    @Override
    public String getInitialMessage() {
        return createMessage(0);
    }

    @Override
    public List<Button> getButtons() {
        List<Button> res = new ArrayList<>();
        if (page > 0)
            res.add(Button.danger("back", Emoji.fromUnicode("⬅️")));
        if (page < pages.size() - 1)
            res.add(Button.danger("next", Emoji.fromUnicode("➡️")));
        return res;
    }
}
