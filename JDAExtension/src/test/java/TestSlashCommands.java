import mocks.MockSlashCommand;
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
import java.util.List;
import java.util.Map;

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
    @Test
    public void testSimpleCommand() {
        MockSlashCommand mockSlashCommand = new MockSlashCommand("simple","teste","teste",configuration,null,false);
        mockSlashCommand.execute();

        String message = mockSlashCommand.getMessageSent2();
        List<Button> buttonList = mockSlashCommand.getButtons2();
        List<String> fileUploadList = mockSlashCommand.getFiles().stream().map(this::getFileData).toList();

        Assertions.assertNotEquals(0, message.length());
        Assertions.assertNotEquals(0, buttonList.size());
        Assertions.assertNotEquals(0, fileUploadList.size());
        Assertions.assertEquals(Response.ResponseTests.getMessageTest("simple","SimpleCommand", Map.of("title","Boas","counter","1")), message);
        Assertions.assertEquals(Response.ResponseTests.getButtonsTest("simple","SimpleCommand", Map.of("title","Boas","counter","1")), buttonList);
        Assertions.assertEquals(Response.ResponseTests.getFilesTest("simple","SimpleCommand", Map.of("title","Boas","counter","1")).stream().map(this::getFileData).toList(), fileUploadList);
    }
}
