import mocks.MockSlashCommand;
import mocks.MockTemplates;
import org.jdaextension.configuration.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slashcommands.SimpleCommandOptions;
import slashcommands.SimpleCommandMod;
import slashcommands.SimpleCommand;

import java.util.Map;

public class TestSlashCommands {
    private static final Configuration configuration = new Configuration();

    @BeforeAll
    public static void addCommands() {
        configuration.addCommand(new SimpleCommandOptions());
        configuration.addCommand(new SimpleCommandMod());
        configuration.addCommand(new SimpleCommand());
    }
    @Test
    public void testConfiguration() {
        MockSlashCommand mockSlashCommand = new MockSlashCommand("simple","teste","teste",configuration,null,false);
        mockSlashCommand.execute();
        String message = mockSlashCommand.getMessageSent2();
        Assertions.assertNotEquals(0, message.length());
        Assertions.assertEquals(MockTemplates.getMessage("SimpleCommand", Map.of("title","Boas","counter","1")), message);

    }
}
