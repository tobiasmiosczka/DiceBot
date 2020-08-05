package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Argument;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Command(
        command = "help",
        description = "Shows information about either all or one specific command.",
        arguments = {
                @Argument(
                        name = "command",
                        isOptional = true,
                        description = "The command, you want to know more about."
                )
        }
)
public class HelpCommand implements CommandFunction {

    private static final String COMMAND_PREFIX = "!";

    private final Map<String, Command> commands;
    private final MessageEmbed commandsMessage;
    private final Map<String, MessageEmbed> commandMessageEmbed;

    public HelpCommand() {
        commands = new HashMap<>();
        try {
            Reflections reflections = new Reflections(this.getClass().getPackage().getName());
            Set<Class<? extends CommandFunction>> classes = reflections.getSubTypesOf(CommandFunction.class);
            for (Class<? extends CommandFunction> c : classes) {
                Command commandAnnotation = c.getAnnotation(Command.class);
                if (commandAnnotation == null)
                    continue;
                commands.put(commandAnnotation.command(), commandAnnotation);
            }
        } catch (Exception e) {
            //TODO: implement error handling
        }
        commandsMessage = generateCommandsMessage(commands);
        commandMessageEmbed = new HashMap<>();
        for (Command command : commands.values()) {
            commandMessageEmbed.put(command.command(), generateCommandMessage(command));
        }
    }

    private static MessageEmbed generateCommandsMessage(Map<String, Command> commands) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Commands");
        List<Map.Entry<String, Command>> sortedEntries = new ArrayList<>(commands.entrySet());
        sortedEntries.sort(Map.Entry.comparingByKey());
        for (Map.Entry<String, Command> e : sortedEntries) {
            embedBuilder.addField(COMMAND_PREFIX + e.getKey(), e.getValue().description(), false);
        }
        return embedBuilder.build();
    }

    private static MessageEmbed generateCommandMessage(Command command) {
        String argumentsString = Arrays.stream(command.arguments())
                    .map(a -> (a.isOptional() ? "[" + a.name() + "]" : a.name()))
                    .reduce((s1, s2) -> s1 + " " + s2)
                    .orElse("");
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(COMMAND_PREFIX + command.command() + " " + argumentsString)
                .setDescription(command.description());
        for (Argument a : command.arguments()) {
            embedBuilder.addField(a.isOptional() ? "[" + a.name() + "]" : a.name(), a.description(), false);
        }
        return embedBuilder.build();
    }

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        if (arg == null || arg.isEmpty()) {
            messageChannel
                    .sendMessage(commandsMessage)
                    .queue();
            return true;
        }
        if (!commands.containsKey(arg)) {
            messageChannel
                    .sendMessage("There is no command `" + arg + "`")
                    .queue();
            return false;
        }
        messageChannel
                .sendMessage(commandMessageEmbed.get(arg))
                .queue();
        return true;
    }
}
