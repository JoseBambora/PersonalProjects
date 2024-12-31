import kotlin.ranges.IntRange;
import mocks.MockSlashCommand;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jdaextension.configuration.Configuration;
import org.jdaextension.responses.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slashcommands.SimpleCommandOptions;
import slashcommands.SimpleCommandMod;
import slashcommands.SimpleCommand;

import java.io.IOException;
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
    private String getFileData(FileUpload fileUpload) {
        try {
            return new String(fileUpload.getData().readAllBytes());
        } catch (IOException e) {
            return "";
        }
    }
    private void testCommandSucess(MockSlashCommand mockSlashCommand, String id, String template, Map<String,Object> variables, boolean hasButtons, boolean hasFile, boolean hasEmbed) {
        String message = mockSlashCommand.getMessageSent2();
        List<Button> buttonList = mockSlashCommand.getButtons2();
        List<String> fileUploadList = mockSlashCommand.getFiles().stream().map(this::getFileData).toList();
        MessageEmbed messageEmbed = mockSlashCommand.getResultEmbed();

        Assertions.assertFalse(message.isBlank());
        Assertions.assertNotEquals(hasButtons,buttonList.isEmpty());
        Assertions.assertNotEquals(hasFile,fileUploadList.isEmpty());
        Assertions.assertNull(messageEmbed);
        Assertions.assertEquals(Response.ResponseTests.getMessageTest(id,template,variables), message);
        Assertions.assertEquals(Response.ResponseTests.getButtonsTest(id,template,variables), buttonList);
        Assertions.assertEquals(Response.ResponseTests.getFilesTest(id,template,variables).stream().map(this::getFileData).toList(), fileUploadList);

    }

    @Test
    public void testSimpleCommand() {
        MockSlashCommand mockSlashCommand = new MockSlashCommand("simple","teste","teste",configuration,null,false);
        mockSlashCommand.execute();
        testCommandSucess(mockSlashCommand,"simple","SimpleCommand", Map.of("title","Boas","counter","1"),true,true,false);
    }

    @Test
    public void testSimpleModCommand() {
        MockSlashCommand mockSlashCommandNoPermission = new MockSlashCommand("simplemod","teste","teste",configuration,null,false);
        mockSlashCommandNoPermission.execute();
        String noPermission = mockSlashCommandNoPermission.getMessageSent2();


        MockSlashCommand mockSlashCommandPermissionNoArgs = new MockSlashCommand("simplemod","teste","teste",configuration,null,true);
        mockSlashCommandPermissionNoArgs.execute();
        String messageNoArgs = mockSlashCommandPermissionNoArgs.getMessageSent2();

        MockSlashCommand mockSlashCommandPermissionArgs = new MockSlashCommand("simplemod","teste","teste",configuration,Map.of("name","Olá","number",2),true);
        mockSlashCommandPermissionArgs.execute();
        String message = mockSlashCommandPermissionArgs.getMessageSent2();

        Assertions.assertFalse(noPermission.isBlank());
        Assertions.assertFalse(messageNoArgs.isBlank());
        Assertions.assertFalse(message.isBlank());



        Assertions.assertEquals(Response.ResponseTests.getMessageTest("simplemod","403",Map.of("message","You do not have access to this command")),noPermission);
        Assertions.assertNotEquals(Response.ResponseTests.getMessageTest("simplemod","403",Map.of("message","You do not have access to this command")),messageNoArgs);
        Assertions.assertEquals(Response.ResponseTests.getMessageTest("simplemod","400",Map.of("errors",List.of("Argument `name` is missing","Argument `number` is missing"))),messageNoArgs);
        testCommandSucess(mockSlashCommandPermissionArgs,"simplemod","SimpleCommandMod", Map.of("name","Olá","items", IntStream.range(0, 2).boxed().toList()),true,false,false);

        // Assertions.assertEquals(Response.ResponseTests.getMessageTest("simplemod","SimpleCommandMod",Map.of("name","Olá","items", IntStream.range(0, 2).boxed().toList())),message);

    }
}
