package org.jdaextension.reponses;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseMessage {
    private final String file;
    private final Map<String,Object> variables;
    private final StringBuilder message;
    private final List<Button> buttons;

    public ResponseMessage(String filename) {
        this.file = System.getenv("VIEWS_FOLDER") + "/" + filename;
        this.message = new StringBuilder();
        this.variables = new HashMap<>();
        this.buttons = new ArrayList<>();
    }

    public ResponseMessage setVariable(String name, Object value) {
        this.variables.put(name,value);
        return this;
    }

    private void configMessage(Document doc) {
        Element message = doc.getElementsByTag("main").getFirst();
        this.message.append(message.wholeText().strip().replaceAll("\n +","\n"));
    }
    private void configButton(Document doc,String classCSS) {
        Elements buttonsDoc = doc.select("button." + classCSS);
        buttons.addAll(buttonsDoc.stream().map(n -> Button.primary(n.id(),n.text())).toList());
    }
    private void configButtons(Document doc) {
        configButton(doc, "primary");
        configButton(doc, "secondary");
        configButton(doc, "danger");
    }

    private void build() {
        Handlebars handlebars = new Handlebars();
        try {
            Template template = handlebars.compile(file);
            String result = template.apply(variables);
            Document doc = Jsoup.parse(result, "", org.jsoup.parser.Parser.xmlParser());
            configMessage(doc);
            configButtons(doc);
        } catch (IOException e) {
            System.err.println("Error sending message: \n" + e);
        }
    }

    public void send(SlashCommandInteractionEvent event, boolean sentThinking) {
        this.build();
        if(sentThinking) {
            if(this.buttons.isEmpty()) {
                event.getHook()
                        .sendMessage(this.message.toString())
                        .queue();
            }
            else {
                event.getHook()
                        .sendMessage(this.message.toString())
                        .setActionRow(this.buttons)
                        .queue();
            }
        }
        else {
            if(this.buttons.isEmpty()) {
                event.reply(this.message.toString()).queue();
            }
            else {
                event.reply(this.message.toString())
                        .setActionRow(this.buttons)
                        .queue();
            }
        }
    }
}
