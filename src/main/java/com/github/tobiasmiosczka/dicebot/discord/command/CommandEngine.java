package com.github.tobiasmiosczka.dicebot.discord.command;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Option;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.github.tobiasmiosczka.dicebot.util.ReflectionUtil.instantiate;

@Component
public class CommandEngine extends ListenerAdapter {

    private static final String COMMANDS_PACKAGE = "com.github.tobiasmiosczka.dicebot.commands";

    private record Tuple(Command command, CommandFunction commandFunction) {}

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandEngine.class);

    private final Map<String, Tuple> commands = new HashMap<>();

    private static OptionData toOptionData(Option option) {
        return new OptionData(OptionType.STRING, option.name(), option.description(), option.isRequired());
    }

    public CommandEngine(JDA jda) {
        loadCommands();
        commands.values().forEach(e -> registerCommand(jda, e.command()));
        LOGGER.info("{} commands loaded.", commands.size());
    }

    private void loadCommands() {
        Reflections reflections = new Reflections(COMMANDS_PACKAGE);
        for (Class<? extends CommandFunction> c : reflections.getSubTypesOf(CommandFunction.class)) {
            Command command = c.getAnnotation(Command.class);
            if (command == null)
                continue;
            instantiate(c).ifPresent(e -> addCommand(command, e));
        }
    }

    private void addCommand(Command command, CommandFunction commandFunction) {
        if (commands.containsKey(command.command())) {
            LOGGER.warn("Command {} already registered.", command.command());
            return;
        }
        commands.put(command.command(), new Tuple(command, commandFunction));
    }

    private void registerCommand(JDA jda, Command commandAnnotation) {
        String command = commandAnnotation.command();
        LOGGER.info("Registering {}", command);
        jda.upsertCommand(command, commandAnnotation.description())
                .setGuildOnly(commandAnnotation.guildOnly())
                .addOptions(Arrays.stream(commandAnnotation.options())
                        .map(CommandEngine::toOptionData)
                        .toList())
                .complete();
        LOGGER.info("Registered {}", command);
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
