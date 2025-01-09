package aux;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jdaextension.responses.Response;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GenericTests {

    private static String getFileData(FileUpload fileUpload) {
        try {
            return new String(fileUpload.getData().readAllBytes());
        } catch (IOException e) {
            return "";
        }
    }

    public static void testMessage(String message, String command, boolean hasMessage, List<Button> buttonList, boolean hasButtons, List<FileUpload> fileUploadList, boolean hasFile, MessageEmbed messageEmbed, boolean hasEmbed, String id, String template, Map<String, Object> variables) {
        Assertions.assertNotEquals(hasMessage, message == null || message.isBlank());
        Assertions.assertNotEquals(hasButtons, buttonList.isEmpty());
        Assertions.assertNotEquals(hasFile, fileUploadList.isEmpty());
        Assertions.assertEquals(hasEmbed, messageEmbed != null);
        Assertions.assertEquals(Response.ResponseTests.getMessageTest(id, command, template, variables), message);
        Assertions.assertEquals(Response.ResponseTests.getButtonsTest(id, command, template, variables), buttonList);
        Assertions.assertEquals(Response.ResponseTests.getFilesTest(id, command, template, variables).stream().map(GenericTests::getFileData).toList(), fileUploadList.stream().map(GenericTests::getFileData).toList());
    }

    public static <T> T getValueSingle(ArgumentCaptor<T> captor) {
        return captor.getAllValues().isEmpty() ? null : captor.getValue();
    }

    public static <T> List<T> getValueCollection(ArgumentCaptor<List<T>> captor) {
        return captor.getAllValues().isEmpty() ? Collections.emptyList() : captor.getValue();
    }
}
