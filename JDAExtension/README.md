# JDA Extension Library

This library is built on top of [JDA](https://github.com/discord-jda/JDA).

The main goal of this library is to simplify the development of a discord bot, focusing on the requirements that I have
for my discord bot, [Bot Gverreiro](https://github.com/JoseBambora/PersonalProjects/tree/main/BotDiscord). If you want 
to reuse anything for your personal project, feel free to do so, by downloading this codebase.

## Introduction

As previously mentioned, I have a discord bot called [Bot Gverreiro](https://github.com/JoseBambora/PersonalProjects/tree/main/BotDiscord).
I am very proud with the final product, but I identified an issue with my development process, if I want to keep making 
updates, the codebase would start to be a mess and difficult to maintain. To address this issue and also to explore more 
features from *JDA*, I decided to create this library, which requirements are **100%** based on my personal bot. 
I tried to make this as generic as possible, so I can implement different for my bot in the future as well, but at the 
end I focused on personal requirements. 

## Technologies

- Java 22
- JUnit

## Configuration steps

This extension need some environmental variable defined at `.env` file. 
The information needed for this file is on the file `.env.default`.

## Features

### 1. Templates

Templates are just [Handle Bars](https://handlebarsjs.com/) files that are used to write messages and embeds, and attach
files and buttons. The syntax is not that complicated and I tried to follow the HTML standard. Files can be reused by
using partials. For more details about HandBars consult https://github.com/jknack/handlebars.java.
So the syntax is that I have created is shown below. It is important to mention that none of these fields are obligatory,
you can just use what you want. More examples can be found at [Tests Templates](/src/test/resources/views).

```hbs
<main>
    Write the message content here
</main>
<embed color="embed hexa code color">
    <author>author of the embed.</author>
    <title>embed title</title>
    <table>
        <tr>
            <td name="1">Line 11</td>
            <td name="2">Line 12</td>
            <td name="3">Line 13</td>
        </tr>
        <tr>
            <td name="4">Line 21</td>
            <td name="5">Line 22</td>
        </tr>
    </table>
    <description>Embed description</description>
    <footer>Embed Footer</footer>
</embed>
<button id="button id" class="primary, secondary or danger">Button</button>
<a href="link">button link types</a>
<file src="file name"/>
```

### 2. Register a slash command

Usually to register a slash command, there is a need to "reinvent the wheel", since the process for all of them are the 
same, only changing configuration information, such as command name, options, permissions, etc. The way I solve this was
creating a class called `SlashCommand`, which serves as a configuration class with a friendly interface. To create a slash 
command, the user must define a new class implementing the interface `SlashEvent` which only contains two methods, to configure
the `SlashCommand` object and the behaviour when this command is called. To not extend too much, the registration process 
is done automatically by the class `Configuration` and also the options are handled automatically, including options with 
custom type. The following snippet of code will summarize everything related with the slash command. More examples can 
be found within [Slash Command Tests](src/test/java/cases/slashcommands).

```java
// HelloCommand.java
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.configuration.option.Number;
import org.jdaextension.configuration.option.OptionNumber;
import org.jdaextension.configuration.option.OptionString;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.ResponseButton;
import org.jdaextension.responses.ResponseCommand;

// Slash command /hello name:(string option) number:(option integer)
public class HelloCommand implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        OptionString option1 = new OptionString("name", "Name", false);
        OptionNumber option2 = new OptionNumber("number", "Number", false, Number.INTEGER)
                .addChoice("Option 1", 2)
                .addChoice("Option 2", 3);
        slashCommand.setName("hello")
                .setDescription("just an example")
                .addOption(option1)
                .addOption(option2);
    }

    @Override
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, ResponseCommand response) {
        /**
         * variables key -> value:
         * name -> the name that the user wrote
         * number -> the choice selected (2 or 3).
         */
        response.setTemplate("TemplateHello").setVariable("name", variables.get("name"));
    }


    public void onButton1(ButtonInteractionEvent event, ResponseButton response) {
        response.setTemplate("TemplateHelloButton");
    }

    @Override
    public void onCall(ButtonInteractionEvent event, String id, ResponseButton response) {
        switch (id) {
            case "1" -> onButton1(event, response);
            default -> response.setTemplate("400").setVariable("message", "Button does not exists");
        }
    }
}
```

```hbs
<!-- TemplateHello.hbs -->
<main>
    Hello {{name}}.
</main>
<button class="primary" id="1">click here to change the message</button>
```

```hbs
<!-- TemplateHelloButton.hbs -->
<main>
    Edited.
</main>
```

### 3. Messages Receivers, Updates and Deletes

The problem and the approach here is the same as the slash command, the only different that here there are no options
and whatever that is specific to slash commands. To make this more usefully, there is pipeline configuration that basically
process each message event (received, updated or deleted) and return a boolean if the process must proceed or not. Additionally,
there is a Map that will store any variables that will be navigated from the pipeline since when they joined until the 
message event is processed. There is an example below, but more examples can be found at [Message Tests](src/test/java/cases/messages).

```java

package cases.messages;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import org.jdaextension.configuration.MessageReceiver;
import org.jdaextension.generic.MessageEvent;
import org.jdaextension.responses.ResponseButton;
import org.jdaextension.responses.ResponseMessageReceiver;
import org.jdaextension.responses.ResponseMessageUpdate;

import java.util.Map;
import java.util.function.BiFunction;

public class SimpleMessage implements MessageEvent {
    @Override
    public void configure(MessageReceiver messageReceiver) {
        // if author name == author name, then process the message.
        BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean> p1 = (e, v) -> e.getAuthor().getName().equals("author name");
        BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean> p2 = (e, v) -> {
            v.put("name", e.getAuthor().getName());
            return true;
        };
        messageReceiver.addToPipelineReceive(p1).addToPipelineReceive(p2);
    }

    @Override
    public void onCall(MessageReceivedEvent event, Map<String, Object> data, ResponseMessageReceiver response) {
        /**
         * data will contain:
         * name -> author name
         */
        response.setVariables(data).setTemplate("SimpleMessage");
    }
}
```

### 4. Extra

To not extend this documentation, I will not detail everything else that is implemented, but some examples can be found at 
[Tests](src/test/java/cases). To sum up, message and user context events can be added, with similar process to slash
commands. Configuration also enables to add behaviours when the bot is shut down, or when a user left a server for a reason
and also when users update their discord username.