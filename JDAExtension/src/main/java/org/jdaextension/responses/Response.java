package org.jdaextension.responses;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.function.Function;

public abstract class Response {
    private String file;
    private final Map<String,Object> variables;
    protected final StringBuilder message;
    protected final List<Button> buttons;

    public Response() {
        this.file = "";
        this.message = new StringBuilder();
        this.variables = new HashMap<>();
        this.buttons = new ArrayList<>();
    }

    public Response setTemplate(String template) {
        this.file = template;
        return this;
    }

    public Response setVariable(String name, Object value) {
        this.variables.put(name,value);
        return this;
    }

    private void configMessage(Document doc) {
        Element message = doc.getElementsByTag("main").getFirst();
        this.message.append(message.wholeText().strip().replaceAll("\n +","\n"));
    }
    private void configButton(Document doc, String classCSS, Function<Element,Button> function) {
        Elements buttonsDoc = doc.select("button." + classCSS);
        buttons.addAll(buttonsDoc.stream().map(function).toList());
    }
    private void configButtons(Document doc, String id) {
        configButton(doc, "primary", n -> Button.primary(id + "_" + n.id(),n.text()));
        configButton(doc, "secondary",n -> Button.secondary(id + "_" + n.id(),n.text()));
        configButton(doc, "danger",n -> Button.danger(id + "_" + n.id(),n.text()));
    }

    protected void build(String id) {
        String result = PreCompileTemplates.apply(file, variables);
        Document doc = Jsoup.parse(result, "", org.jsoup.parser.Parser.xmlParser());
        configMessage(doc);
        configButtons(doc,id);
    }

    public abstract void send();
}
