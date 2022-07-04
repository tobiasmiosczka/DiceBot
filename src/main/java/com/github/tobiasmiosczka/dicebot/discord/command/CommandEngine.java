package com.github.tobiasmiosczka.dicebot.discord.command;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Option;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.tobiasmiosczka.dicebot.util.ReflectionUtil.instantiate;

public class CommandEngine extends ListenerAdapter {

    private record Tuple(Command command, CommandFunction commandFunction) {}

    private static final Logger LOGGER = Logger.getGlobal();

    private final Map<String, Tuple> commands = new HashMap<>();

    private static OptionData toOptionData(Option option) {
        return new OptionData(OptionType.STRING, option.name(), option.description(), option.isRequired());
    }

    public CommandEngine(JDA jda, String commandsPackage) {
        loadCommands(commandsPackage);
        commands.values().forEach(e -> registerCommand(jda, e.command()));
        LOGGER.log(Level.INFO, commands.size() + " commands loaded.");
    }

    private void loadCommands(String commandsPackage) {
        Reflections reflections = new Reflections(commandsPackage);
        for (Class<? extends CommandFunction> c : reflections.getSubTypesOf(CommandFunction.class)) {
            Command command = c.getAnnotation(Command.class);
            if (command == null)
                continue;
            instantiate(c).ifPresent(e -> addCommand(command, e));
        }
    }

    private void addCommand(Command command, CommandFunction commandFunction) {
        String commandString = command.command();
        if (commands.containsKey(commandString)) {
            LOGGER.log(Level.WARNING, "Command %s already registered.".formatted(command.command()));
            return;
        }
        commands.put(commandString, new Tuple(command, commandFunction));
    }

    private void registerCommand(JDA jda, Command commandAnnotation) {
        jda.upsertCommand(commandAnnotation.command(), commandAnnotation.description())
                .setGuildOnly(commandAnnotation.guildOnly())
                .addOptions(Arrays.stream(commandAnnotation.arguments())
                        .map(CommandEngine::toOptionData)
                        .toList())
                .complete();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        commands.get(event.getName()).commandFunction().performCommand(event).queue();
    }

}