package com.github.tobiasmiosczka.dicebot.discord.command;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Option;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CommandEngine extends ListenerAdapter {

    private static final Logger LOGGER = Logger.getGlobal();

    private final Map<String, CommandFunction> commands;
    private final Map<String, Command> metaData;

    public CommandEngine(JDA jda, String commandsPackage) {
        commands = new HashMap<>();
        metaData = new HashMap<>();
        try {
            addCommands(jda, commandsPackage);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO, commands.size() + " commands loaded.");
    }

    private void addCommands(JDA jda, String commandsPackage)
            throws
                NoSuchMethodException,
                IllegalAccessException,
                InvocationTargetException,
                InstantiationException{
        Reflections reflections = new Reflections(commandsPackage);
        for (Class<? extends CommandFunction> c : reflections.getSubTypesOf(CommandFunction.class)) {
            Command command = c.getAnnotation(Command.class);
            if (command != null) {
                CommandFunction commandFunction = c.getDeclaredConstructor().newInstance();
                try {
                    addCommand(command, commandFunction);
                    registerCommand(jda, command);
                } catch (DuplicateCommandException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addCommand(Command command, CommandFunction commandFunction) throws DuplicateCommandException {
        String commandString = command.command();
        if (commands.containsKey(commandString) || metaData.containsKey(commandString))
            throw new DuplicateCommandException(commandFunction.getClass(), commandString);
        commands.put(commandString, commandFunction);
        metaData.put(commandString, command);
    }

    private void registerCommand(JDA jda, Command commandAnnotation) {
        jda.upsertCommand(commandAnnotation.command(), commandAnnotation.description())
                .setGuildOnly(commandAnnotation.guildOnly())
                .addOptions(Arrays.stream(commandAnnotation.arguments())
                        .map(CommandEngine::toOptionData)
                        .collect(Collectors.toList()))
                .complete();
    }

    private static OptionData toOptionData(Option option) {
        return new OptionData(OptionType.STRING, option.name(), option.description(), option.isRequired());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        commands.get(event.getName()).performCommand(event).queue();
    }

}