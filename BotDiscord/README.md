# Bot Gverreiro

This project was created for a Discord community/server.

I am an SC Braga fan, and two years ago, I wanted to join a fan community of this club to stay updated on club news and discuss interesting topics about Braga with other fans.

In this Discord server, there is a channel dedicated to predicting the outcomes of specific games. These games include the male professional football team and some indoor football games. The main goal of this channel is to create a leaderboard, and at the end, the winner receives an award. Managing the process of opening or closing the channel, announcing the winners, updating the leaderboard, and so on, is tedious to do manually. Therefore, I created a Discord bot called **Bot Gverreiro**.

For this project, I tried to implement as many features as possible, including numerous tests, multithreading, database atomic operations, and more.

## What Does Bot Gverreiro Do?

**Bot Gverreiro** collects predictions and the final results for each game and announces the users who made correct predictions. Behind the scenes, it maintains a leaderboard and keeps statistics for each season and each user/player.

In addition, the bot automatically opens and closes the channel based on the game schedule and sends reminders about missing items, such as results for specific games.

## How Does Bot Gverreiro Work?

The bot operates through SlashCommands (/command name). It has a list of these commands, each assigned to a specific task. Some commands are restricted to moderators. These special commands involve operations essential to the bot, such as adding a game to the bot, entering a result for a game, and so on.

Using these SlashCommands, users provide important input to the bot, enabling it to perform tasks automatically.

If you want to see the intricate workings of the bot, don't worry—I have documented all the "hard" parts.

## Configuration Steps

In addition to downloading the necessary libraries, you need to create a configuration file to set up the bot. This file is located at [files/config/config.txt] and uses a simple key-value format. Each parameter is explained within the file as an example.

## Future Work

This project has potential for future growth, for example, with AI integration so that the bot can also make predictions and send event messages (e.g., when a team scores a goal, the bot will send a message to a specific Discord channel). Additionally, more sports/modalities could be added to the bot.

## Technologies

- Java 22
- SQLite
- JUnit

## Libraries

These libraries are included in the [Maven file](pom.xml).

- [JDA 5.0.0-beta.23](https://github.com/discord-jda/JDA): Discord API in Java.
- [Mockito Core 5.12](https://mvnrepository.com/artifact/org.mockito/mockito-core/5.12.0): Testing.
- [JOOQ 3.19.8](https://github.com/jOOQ/jOOQ): Database queries.
- [SQLite-JDBC 3.45.3.0](https://github.com/xerial/sqlite-jdbc): SQLite connections.

## Notes
- Unfortunately, this is not the real version that the bot is using since running the bot 24/7 would incur costs. However, the *no server* version is based on this version.
- All the code is documented.
- All features were developed by myself.