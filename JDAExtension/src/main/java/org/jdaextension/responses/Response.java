package org.jdaextension.responses;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.function.Function;

public abstract class Response {
    protected final StringBuilder message;
    protected final List<Button> buttons;
    protected final List<Emoji> emojis;
    protected final List<FileUpload> files;
    private final Map<String, Object> variables;
    private final EmbedBuilder embedBuilder;
    private String file;

    public Response() {
        this.file = "";
        this.message = new StringBuilder();
        this.variables = new HashMap<>();
        this.buttons = new ArrayList<>();
        this.emojis = new ArrayList<>();
        this.files = new ArrayList<>();
        embedBuilder = new EmbedBuilder();
    }

    public Response setTemplate(String template) {
        this.file = template;
        return this;
    }

    public Response setVariable(String name, Object value) {
        this.variables.put(name, value);
        return this;
    }

    public Response addEmoji(Emoji emoji) {
        emojis.add(emoji);
        return this;
    }

    private void configMessage(Document doc) {
        Element message = doc.getElementsByTag("main").getFirst();
        this.message.append(message.wholeText().strip().replaceAll("\n +", "\n"));
    }

    private void configButton(Document doc, String classCSS, Function<Element, Button> function) {
        Elements buttonsDoc = doc.select("button." + classCSS);
        buttons.addAll(buttonsDoc.stream().map(function).toList());
    }

    private void configLinks(Document doc) {
        Elements links = doc.getElementsByTag("a");
        buttons.addAll(links.stream().map(l -> Button.link(l.attr("href"), l.text())).toList());
    }

    private void configButtons(Document doc, String id) {
        configButton(doc, "primary", n -> Button.primary(id + "_" + n.id(), n.text()));
        configButton(doc, "secondary", n -> Button.secondary(id + "_" + n.id(), n.text()));
        configButton(doc, "danger", n -> Button.danger(id + "_" + n.id(), n.text()));
        configLinks(doc);
    }

    private void configFiles(Document doc) {
        Elements files = doc.getElementsByTag("file");
        this.files.addAll(files.stream().map(f -> {
            try {
                return Response.class
                        .getClassLoader()
                        .getResource(System.getenv("FILES_FOLDER") + f.attr("src"))
                        .toURI();

            } catch (URISyntaxException e) {
                System.err.println("URI error: \n" + e);
                return null;
            }
        }).filter(Objects::nonNull).map(Paths::get).map(FileUpload::fromData).toList());
    }

    private String getAttribute(Element element, String name) {
        return element.hasAttr(name) ? element.attribute(name).getValue() : null;
    }

    private String getAttribute(Element element, String name, String defaultValue) {
        return element.hasAttr(name) ? element.attribute(name).getValue() : defaultValue;
    }

    private void configEmbed(Document doc) {
        if (!doc.getElementsByTag("embed").isEmpty()) {
            Element elementEmbed = doc.getElementsByTag("embed").getFirst();
            Elements elementsTable = elementEmbed.getElementsByTag("table").getFirst().getElementsByTag("tr");
            Element elementFooter = elementEmbed.getElementsByTag("footer").getFirst();
            embedBuilder.setAuthor(getAttribute(elementEmbed, "author"));
            embedBuilder.setColor(Color.decode(getAttribute(elementEmbed, "color", "0xFF5733")));
            embedBuilder.setTitle(getAttribute(elementEmbed, "title"));
            embedBuilder.setDescription(getAttribute(elementEmbed, "description"));
            embedBuilder.setThumbnail(getAttribute(elementEmbed, "thumbnail"));
            for (Element elementRow : elementsTable) {
                Elements elementsColumns = elementRow.getElementsByTag("td");
                elementsColumns.forEach(c -> embedBuilder.addField(c.attribute("name").getValue(), c.text(), true));
                embedBuilder.addBlankField(false);
            }
            embedBuilder.setFooter(elementFooter.text());
        }
    }

    protected boolean build(String id) {
        if (this.file != null && !this.file.isBlank()) {
            String result = PreCompileTemplates.apply(file, variables);
            Document doc = Jsoup.parse(result, "", org.jsoup.parser.Parser.xmlParser());
            configButtons(doc, id);
            configFiles(doc);
            configEmbed(doc);
            configMessage(doc);
            return true;
        } else
            return false;
    }

    public abstract void send();

    protected void sendReactions(Message message) {
        emojis.forEach(e -> message.addReaction(e).queue());
    }

    protected MessageEditCallbackAction setEmbed(MessageEditCallbackAction m) {
        return !this.embedBuilder.isEmpty() ? m.setEmbeds(embedBuilder.build()) : m;
    }

    protected MessageCreateAction setEmbed(MessageCreateAction m) {
        return !this.embedBuilder.isEmpty() ? m.setEmbeds(embedBuilder.build()) : m;
    }

    protected ReplyCallbackAction setEmbed(ReplyCallbackAction m) {
        return !this.embedBuilder.isEmpty() ? m.setEmbeds(embedBuilder.build()) : m;
    }

    protected WebhookMessageEditAction<Message> setEmbed(WebhookMessageEditAction<Message> m) {
        return !this.embedBuilder.isEmpty() ? m.setEmbeds(embedBuilder.build()) : m;
    }

    public String getMessageTest() {
        build("");
        return message.toString();
    }
}
