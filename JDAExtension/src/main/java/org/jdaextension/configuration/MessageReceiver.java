package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jdaextension.interfaces.MessageReceiverInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageReceiver {
    private String channelID;
    private Pattern pattern;
    private boolean catchMoreThanOne;
    private MessageReceiverInterface controller;
    private Function<MessageReceivedEvent,String> onErrorRegex;

    public MessageReceiver() {
        channelID = null;
        pattern = null;
        catchMoreThanOne = false;
        controller = null;
        onErrorRegex = null;
    }

    public MessageReceiver setRegex(String regex, Function<MessageReceivedEvent,String> onErrorRegex) {
        this.pattern = Pattern.compile(regex);
        this.onErrorRegex = onErrorRegex;
        return this;
    }

    public MessageReceiver setChannelID(String channelID) {
        this.channelID = channelID;
        return this;
    }

    protected void setController(MessageReceiverInterface controller) {
        this.controller = controller;
    }

    public MessageReceiver catchPatternMultipleTimes() {
        this.catchMoreThanOne = true;
        return this;
    }

    private void addMatchFound(Matcher matcher, List<String> groups) {
        for(int i = 0; i < matcher.groupCount(); i++)
            groups.add(matcher.group(i));
    }

    private List<String> getGroups(String message) {
        if(pattern != null) {
            Matcher matcher = pattern.matcher(message);
            List<String> groups = new ArrayList<>(matcher.groupCount());
            int n = catchMoreThanOne ? -1 : 1;
            while (matcher.find() && (n--) != 0) {
                addMatchFound(matcher,groups);
            }
            if(n == -1 || n == 1)
                return null;
            else
                return groups;
        }
        else
            return Collections.emptyList();
    }

    protected void messageReceived(MessageReceivedEvent event) {
        if(channelID == null || channelID.isBlank() || channelID.equals(event.getChannel().getId())) {
            String message = event.getMessage().getContentDisplay();
            List<String> groups = getGroups(message);
            if(groups != null)
                controller.onCall(event,groups ).send(event);
            else
                event.getMessage().reply(onErrorRegex.apply(event)).queue();
        }
    }
}
