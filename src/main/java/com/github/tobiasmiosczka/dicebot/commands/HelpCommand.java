package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Option;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
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
                @Option(
                        name = "command",
                        type = OptionType.STRING,
                        isRequired = false,
                        description = "The command, you want to know more about."
                )
        }
)
public class HelpCommand implements CommandFunction {

    private static final String COMMAND_PREFIX = "/";

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
                    .map(a -> (a.isRequired() ? a.name() : "[" + a.name() + "]"))
                    .reduce("", (s1, s2) -> s1 + " " + s2);
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle(COMMAND_PREFIX + command.command() + " " + argumentsString)
                .setDescription(command.description());
        for (Option a : command.arguments()) {
            embedBuilder.addField(a.isRequired() ? a.name() : "[" + a.name() + "]", a.description(), false);
        }
        return embedBuilder.build();
    }

    @Override
    public ReplyCallbackAction performCommand(SlashCommandInteractionEvent event) {
        String arg = event.getOptionsByName("command").get(0).getAsString();
        if (arg.isEmpty()) {
            return event.replyEmbeds(commandsMessage);
        }
        if (!commands.containsKey(arg)) {
            return event.reply("There is no command `" + arg + "`");
        }
        return event.replyEmbeds(commandMessageEmbed.get(arg));
    }
}
