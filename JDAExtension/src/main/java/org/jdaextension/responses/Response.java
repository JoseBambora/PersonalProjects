package org.jdaextension.responses;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
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
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Response {
    protected final StringBuilder message;
    protected final List<Button> buttons;
    protected final List<Emoji> emojis;
    protected final List<FileUpload> files;
    protected final EmbedBuilder embedBuilder;
    private final Map<String, Object> variables;
    protected Modal modal;
    protected boolean isModal;
    private String file;
    private Runnable success;
    private Consumer<Throwable> failure;

    public Response() {
        this.file = "";
        this.message = new StringBuilder();
        this.variables = new HashMap<>();
        this.buttons = new ArrayList<>();
        this.emojis = new ArrayList<>();
        this.files = new ArrayList<>();
        embedBuilder = new EmbedBuilder();
        modal = null;
        isModal = false;
        success = null;
        failure = null;
    }

    public Response setSuccess(Runnable success) {
        this.success = success;
        return this;
    }

    public Response setFailure(Consumer<Throwable> failure) {
        this.failure = failure;
        return this;
    }

    public Response setModal() {
        isModal = true;
        return this;
    }

    public Response setTemplate(String template) {
        this.file = template;
        return this;
    }

    public Response setVariables(Map<String, Object> variables) {
        this.variables.putAll(variables);
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

    public Response addEmoji(String code) {
        emojis.add( Emoji.fromUnicode(code));
        return this;
    }

    private void configMessage(Document doc) {
        Elements messageElement = doc.getElementsByTag("main");
        if (!messageElement.isEmpty()) {
            Element message = messageElement.getFirst();
            this.message.append(message.wholeText().strip().replaceAll("\n +", "\n"));
        }
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

    private String getAttribute(Element element, String name, String defaultValue) {
        return element.hasAttr(name) ? element.attribute(name).getValue() : defaultValue;
    }

    private String getAttribute(Element element, String name) {
        return element.hasAttr(name) ? element.attribute(name).getValue() : null;
    }

    private String getElementText(Element elementEmbed, String tag) {
        Elements elements = elementEmbed.getElementsByTag(tag);
        return elements.isEmpty() ? null : elements.getFirst().wholeText();
    }

    private void configEmbed(Document doc) {
        if (!doc.getElementsByTag("embed").isEmpty()) {
            Element elementEmbed = doc.getElementsByTag("embed").getFirst();
            Elements elementsTable = elementEmbed.getElementsByTag("table").getFirst().getElementsByTag("tr");

            embedBuilder.setColor(Color.decode(getAttribute(elementEmbed, "color", "0xFF5733")))
                    .setAuthor(getElementText(elementEmbed, "author"))
                    .setTitle(getElementText(elementEmbed, "title"))
                    .setDescription(getElementText(elementEmbed, "description"))
                    .setThumbnail(getElementText(elementEmbed, "thumbnail"))
                    .setFooter(getElementText(elementEmbed, "footer"));
            for (Element elementRow : elementsTable) {
                Elements elementsColumns = elementRow.getElementsByTag("td");
                elementsColumns.forEach(c -> embedBuilder.addField(c.attribute("name").getValue(), c.text(), true));
                embedBuilder.addBlankField(false);
            }
        }
    }

    private TextInputStyle getTextInputStyle(String string) {
        if (string != null) {
            if (string.equals("SHORT"))
                return TextInputStyle.SHORT;
            else if (string.equals("PARAGRAPH"))
                return TextInputStyle.PARAGRAPH;
            else
                return TextInputStyle.UNKNOWN;
        } else
            return TextInputStyle.UNKNOWN;
    }

    private void configModal(Document document, String id) {
        Element elementModal = document.getElementsByTag("modal").getFirst();
        String title = getElementText(elementModal, "title");
        Elements inputs = elementModal.getElementsByTag("input");
        List<TextInput> textInputs = inputs.stream().map(e -> {
            String idTI = getAttribute(e, "id", "-1");
            String label = getAttribute(e, "label", "-1");
            TextInputStyle textInputStyle = getTextInputStyle(getAttribute(e, "class"));
            TextInput.Builder builder = TextInput.create(idTI, label, textInputStyle);
            String min = getAttribute(e, "min");
            String max = getAttribute(e, "max");
            String placeHolder = getAttribute(e, "placeholder");
            if (min != null)
                builder.setMinLength(Integer.parseInt(min));
            if (max != null)
                builder.setMaxLength(Integer.parseInt(max));
            builder.setPlaceholder(placeHolder);
            builder.setRequired("true".equals(getAttribute(e, "required")));
            return builder.build();
        }).toList();
        modal = Modal.create(id, title != null ? title : "").addComponents(textInputs.stream().map(ActionRow::of).toList()).build();
    }

    protected boolean build(String id) {
        if (this.file != null && !this.file.isBlank()) {
            String result = PreCompileTemplates.apply(file, variables);
            Document doc = Jsoup.parse(result, "", org.jsoup.parser.Parser.xmlParser());
            if (isModal)
                configModal(doc, id);
            else {
                configButtons(doc, id);
                configFiles(doc);
                configEmbed(doc);
                configMessage(doc);
            }
            return true;
        } else
            return false;
    }

    public abstract void send();

    protected void sendReactions(Message message) {
        emojis.forEach(e -> message.addReaction(e).queue());
    }

    public static class ResponseTests {
        private static Response configureMessage(String id, String command, String template, Map<String, Object> variables) {
            Response responseCommand = new ResponseCommand(null, command, false, false).setTemplate(template);
            variables.forEach(responseCommand::setVariable);
            responseCommand.build((command.isBlank() ? "" : command + "_") + id);
            return responseCommand;
        }

        public static String getMessageTest(String id, String command, String template, Map<String, Object> variables) {
            Response response = configureMessage(id, command, template, variables);
            return response.message.isEmpty() ? null : response.message.toString();
        }

        public static List<Button> getButtonsTest(String id, String command, String template, Map<String, Object> variables) {
            return configureMessage(id, command, template, variables).buttons;
        }

        public static List<FileUpload> getFilesTest(String id, String command, String template, Map<String, Object> variables) {
            return configureMessage(id, command, template, variables).files;

        }
    }

    protected void sendIH(RestAction<InteractionHook> restAction) {
        if(success == null)
            restAction.queue();
        else if (failure == null)
            restAction.queue(_ -> success.run());
        else
            restAction.queue(_ -> success.run(), failure);
    }

    protected void sendWA(WebhookMessageEditAction<Message> webhookMessageEditAction) {
        if(success == null)
            webhookMessageEditAction.queue();
        else if (failure == null)
            webhookMessageEditAction.queue(_ -> success.run());
        else
            webhookMessageEditAction.queue(_ -> success.run(), failure);
    }

    protected void sendMCA(MessageCreateAction messageCreateAction) {
        if(success == null)
            messageCreateAction.queue();
        else if (failure == null)
            messageCreateAction.queue(_ -> success.run());
        else
            messageCreateAction.queue(_ -> success.run(), failure);
    }
}
