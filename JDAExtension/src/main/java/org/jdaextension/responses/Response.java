package org.jdaextension.responses;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

public abstract class Response {
    private String file;
    private final Map<String,Object> variables;
    protected final StringBuilder message;
    protected final List<Button> buttons;
    protected final List<Emoji> emojis;
    protected final List<FileUpload> files;

    public Response() {
        this.file = "";
        this.message = new StringBuilder();
        this.variables = new HashMap<>();
        this.buttons = new ArrayList<>();
        this.emojis = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public Response setTemplate(String template) {
        this.file = template;
        return this;
    }

    public Response setVariable(String name, Object value) {
        this.variables.put(name,value);
        return this;
    }

    public Response addEmoji(Emoji emoji) {
        emojis.add(emoji);
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

    private void configLinks(Document doc) {
        Elements links = doc.getElementsByTag("a");
        buttons.addAll(links.stream().map(l -> Button.link(l.attr("href"),l.text())).toList());
    }

    private void configButtons(Document doc, String id) {
        configButton(doc, "primary", n -> Button.primary(id + "_" + n.id(),n.text()));
        configButton(doc, "secondary",n -> Button.secondary(id + "_" + n.id(),n.text()));
        configButton(doc, "danger",n -> Button.danger(id + "_" + n.id(),n.text()));
        configLinks(doc);
    }

    private void configFiles(Document doc) {
        Elements files = doc.getElementsByTag("file");
        this.files.addAll(files.stream().map(f -> {
            try {
                return Response.class
                        .getClassLoader()
                        .getResource(System.getenv("FILES_FOLDER") + f.attr("src") )
                        .toURI();

            } catch (URISyntaxException e) {
                System.err.println("URI error: \n" + e);
                return null;
            }
        }).filter(Objects::nonNull).map(Paths::get).map(FileUpload::fromData).toList());
    }

    protected boolean build(String id) {
        if(this.file != null && !this.file.isBlank()) {
            String result = PreCompileTemplates.apply(file, variables);
            Document doc = Jsoup.parse(result, "", org.jsoup.parser.Parser.xmlParser());
            configMessage(doc);
            configButtons(doc, id);
            configFiles(doc);
            return true;
        }
        else
            return false;
    }

    public abstract void send();

    protected void sendReactions(Message message) {
        emojis.forEach(e -> message.addReaction(e).queue());
    }
}
