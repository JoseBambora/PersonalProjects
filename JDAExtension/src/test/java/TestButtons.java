import aux.GenericTests;
import cases.messages.SimpleMessage;
import cases.slashcommands.SimpleCommand;
import cases.slashcommands.SimpleCommandMod;
import cases.slashcommands.SimpleCommandOptions;
import mocks.MockButtonReaction;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jdaextension.configuration.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class TestButtons {

    private static final Configuration configuration = new Configuration();

    @BeforeAll
    public static void addCommands() {
        configuration.addCommand(new SimpleCommandOptions());
        configuration.addCommand(new SimpleCommandMod());
        configuration.addCommand(new SimpleCommand());
        configuration.addMessageReceiver(new SimpleMessage());
    }

    private void testButtonClick(String command, String button, boolean hasButtons, boolean hasFile, boolean hasEmbed, String template, Map<String, Object> variables) {
        MockButtonReaction buttonReaction = new MockButtonReaction(command + "_" + button, "teste", configuration);
        buttonReaction.execute();
        String message = buttonReaction.getResultMessage();
        List<Button> buttonList = buttonReaction.getResultButtons();
        List<FileUpload> fileUploadList = buttonReaction.getResultFiles();
        MessageEmbed messageEmbed = buttonReaction.getResultEmbed();
        GenericTests.testMessage(message, "", true, buttonList, hasButtons, fileUploadList, hasFile, messageEmbed, hasEmbed, command, template, variables);

    }

    @Test
    public void testButtonClickSimpleCommand() {
        testButtonClick("command_simple", "1", true, true, true, "SimpleCommand", Map.of("counter", 2));
    }

    @Test
    public void testButtonClickSimpleCommandMod() {
        testButtonClick("command_simplemod", "1", true, false, false, "SimpleCommandMod", Map.of("name", "Button 1 clicked"));
        testButtonClick("command_simplemod", "2", true, false, false, "SimpleCommandMod", Map.of("name", "Button 2 clicked"));
        testButtonClick("command_simplemod", "3", true, false, false, "SimpleCommandMod", Map.of("name", "Button 3 clicked"));
    }

    @Test
    public void testButtonClickMessage() {
        testButtonClick("message_0", "1", true, false, false, "SimpleMessage", Map.of("name", "Button Clicked"));
    }
}
