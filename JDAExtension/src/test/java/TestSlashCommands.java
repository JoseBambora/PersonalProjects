import aux.GenericTests;
import cases.slashcommands.SimpleCommand;
import cases.slashcommands.SimpleCommandMod;
import cases.slashcommands.SimpleCommandOptions;
import mocks.MockSlashCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jdaextension.configuration.Configuration;
import org.jdaextension.responses.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class TestSlashCommands {
    private static final Configuration configuration = new Configuration();

    @BeforeAll
    public static void addCommands() {
        configuration.addCommand(new SimpleCommandOptions());
        configuration.addCommand(new SimpleCommandMod());
        configuration.addCommand(new SimpleCommand());
    }

    private void testCommandSucess(MockSlashCommand mockSlashCommand, String id, String template, Map<String, Object> variables, boolean hasButtons, boolean hasFile, boolean hasEmbed, boolean sendThinking) {
        String message = mockSlashCommand.getResultMessage(sendThinking);
        List<Button> buttonList = mockSlashCommand.getResultButtons(sendThinking);
        List<FileUpload> fileUploadList = mockSlashCommand.getResultFiles(sendThinking);
        MessageEmbed messageEmbed = mockSlashCommand.getResultEmbed(sendThinking);
        GenericTests.testMessage(message, "command", true, buttonList, hasButtons, fileUploadList, hasFile, messageEmbed, hasEmbed, id, template, variables);
    }

    private void emptyBody(MockSlashCommand mockSlashCommand, boolean sendThinking) {
        List<FileUpload> fileUploadList = mockSlashCommand.getResultFiles(sendThinking);
        List<Button> buttonList = mockSlashCommand.getResultButtons(sendThinking);
        MessageEmbed messageEmbed = mockSlashCommand.getResultEmbed(sendThinking);
        Assertions.assertTrue(fileUploadList.isEmpty());
        Assertions.assertTrue(buttonList.isEmpty());
        Assertions.assertNull(messageEmbed);
    }

    private void test403(MockSlashCommand mockSlashCommandNoPermission, boolean sendThinking) {
        String noPermission = mockSlashCommandNoPermission.getResultMessage(sendThinking);
        Assertions.assertFalse(noPermission.isBlank());
        emptyBody(mockSlashCommandNoPermission, sendThinking);
        Assertions.assertEquals(Response.ResponseTests.getMessageTest(mockSlashCommandNoPermission.getCommand(), "", "403", Map.of("message", "You do not have access to this command")), noPermission);
    }

    private void test400(MockSlashCommand mockSlashCommandPermissionArgs, boolean sendThinking, List<String> argumentsMissing) {
        String messageNoArgs = mockSlashCommandPermissionArgs.getResultMessage(sendThinking);
        Assertions.assertFalse(messageNoArgs.isBlank());
        emptyBody(mockSlashCommandPermissionArgs, sendThinking);
        Assertions.assertEquals(Response.ResponseTests.getMessageTest(mockSlashCommandPermissionArgs.getCommand(), "","400", Map.of("errors", argumentsMissing.stream().map(s -> "Argument `" + s + "` is missing").sorted().toList())), messageNoArgs);
    }

    @Test
    public void testSimpleCommand() {
        MockSlashCommand mockSlashCommand = new MockSlashCommand("simple", "teste", "teste", configuration, null, false);
        mockSlashCommand.execute();
        testCommandSucess(mockSlashCommand, "simple", "SimpleCommand", Map.of("title", "Boas", "counter", "1"), true, true, true, false);
    }

    @Test
    public void testSimpleModCommand() {
        MockSlashCommand mockSlashCommandNoPermission = new MockSlashCommand("simplemod", "teste", "teste", configuration, null, false);
        mockSlashCommandNoPermission.execute();
        test403(mockSlashCommandNoPermission, false);

        MockSlashCommand mockSlashCommandPermissionNoArgs = new MockSlashCommand("simplemod", "teste", "teste", configuration, null, true);
        mockSlashCommandPermissionNoArgs.execute();
        test400(mockSlashCommandPermissionNoArgs, false, List.of("number", "name"));

        MockSlashCommand mockSlashCommandPermissionArgs = new MockSlashCommand("simplemod", "teste", "teste", configuration, Map.of("name", "Olá", "number", 2), true);
        mockSlashCommandPermissionArgs.execute();
        testCommandSucess(mockSlashCommandPermissionArgs, "simplemod", "SimpleCommandMod", Map.of("name", "Olá", "items", IntStream.range(0, 2).boxed().toList()), true, false, false, false);
    }

    @Test
    public void testSimpleCommandOptions() {
        Map<String, Object> arguments = new HashMap<>(Map.of("word", "Olá", "coords", "(1,1)", "coords2", "(1,2)"));
        Map<String, Object> vars = new HashMap<>(Map.of("word", "Olá", "coords", "Coords(1,1)", "coords2", "Coords(1,2)"));

        MockSlashCommand mockSlashCommand = new MockSlashCommand("simpleoptions", "teste", "teste", configuration, arguments, false);
        mockSlashCommand.execute();
        testCommandSucess(mockSlashCommand, "simpleoptions", "SimpleCommandOptions", vars, false, false, false, true);

        arguments.remove("word");
        vars.remove("word");

        MockSlashCommand mockSlashCommandNoArgs = new MockSlashCommand("simpleoptions", "teste", "teste", configuration, arguments, false);
        mockSlashCommandNoArgs.execute();
        testCommandSucess(mockSlashCommandNoArgs, "simpleoptions", "SimpleCommandOptions", vars, false, false, false, true);

        arguments.remove("coords2");
        vars.remove("coords2");

        MockSlashCommand mockSlashCommandNoArgs2 = new MockSlashCommand("simpleoptions", "teste", "teste", configuration, arguments, false);
        mockSlashCommandNoArgs2.execute();
        test400(mockSlashCommandNoArgs2, true, List.of("coords2"));
    }
}
