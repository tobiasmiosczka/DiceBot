package com.github.tobiasmiosczka.dicebot.discord.command;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Option;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CommandEngine extends ListenerAdapter {

    private record Tuple(Command command, CommandFunction commandFunction) {}

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandEngine.class);

    private final Map<String, Tuple> commands;

    private static OptionData toOptionData(Option option) {
        return new OptionData(OptionType.STRING, option.name(), option.description(), option.isRequired());
    }

    public CommandEngine(JDA jda, Set<CommandFunction> commandFunctions) {
        this.commands = loadCommands(jda, commandFunctions);
        jda.addEventListener(this);
    }

    private Map<String, Tuple> loadCommands(JDA jda, Set<CommandFunction> commandFunctions) {
        Map<String, Tuple> result = new HashMap<>();
        for (CommandFunction c : commandFunctions) {
            Command command = c.getClass().getAnnotation(Command.class);
            if (command == null)
                continue;
            if (result.containsKey(command.command())) {
                LOGGER.warn("Command {} already found.", command.command());
                continue;
            }
            registerCommand(jda, command);
            result.put(command.command(), new Tuple(command, c));
        }
        LOGGER.info("{} commands loaded.", result.size());
        return result;
    }

    private void registerCommand(JDA jda, Command commandAnnotation) {
        String command = commandAnnotation.command();
        LOGGER.info("Registering {}", command);
        jda.upsertCommand(command, commandAnnotation.description())
                .setContexts(toInteractionContextType(commandAnnotation.guildOnly()))
                .addOptions(Arrays.stream(commandAnnotation.options())
                        .map(CommandEngine::toOptionData)
                        .toList())
                .complete();
        LOGGER.info("Registered {}", command);
    }

    private Collection<InteractionContextType> toInteractionContextType(boolean guildOnly) {
        if (guildOnly) {
            return List.of(InteractionContextType.GUILD);
        }
        return List.of(
                InteractionContextType.GUILD,
                InteractionContextType.BOT_DM,
                InteractionContextType.PRIVATE_CHANNEL);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Tuple tuple = commands.get(event.getName());
        tuple.commandFunction()
                .performCommand(event)
                .setEphemeral(tuple.command().ephemeral())
                .queue();
    }
}
