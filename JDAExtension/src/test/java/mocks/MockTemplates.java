package mocks;

import org.jdaextension.responses.Response;
import org.jdaextension.responses.ResponseCommand;

import java.util.Map;

public class MockTemplates {
    public static String getMessage(String template, Map<String,Object> variables) {
        Response responseCommand = new ResponseCommand(null,false,false).setTemplate(template);
        variables.forEach(responseCommand::setVariable);
        return responseCommand.getMessageTest();
    }
}
