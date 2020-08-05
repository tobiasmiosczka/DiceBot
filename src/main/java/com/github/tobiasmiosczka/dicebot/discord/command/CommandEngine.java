package com.github.tobiasmiosczka.dicebot.discord.command;

import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.reflections.Reflections;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandEngine extends ListenerAdapter {

    private static final Logger LOGGER = Logger.getGlobal();

    private final String commandPrefix;
    private final long botId;
    private final Map<String, CommandFunction> commands;

    public CommandEngine(JDA jda, String commandPrefix, String commandsPackage) {
        this.commandPrefix = commandPrefix;
        commands = new HashMap<>();
        this.botId = jda.getSelfUser().getIdLong();
        try {
            addCommands(commandsPackage);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO, commands.size() + " commands loaded.");
    }

    public void addCommand(String commandString, CommandFunction commandFunction) {
        if (commands.containsKey(commandString))
            throw new DuplicateCommandException(commandFunction.getClass(), commandString);
        commands.put(commandString, commandFunction);
    }

    private void addCommands(String commandsPackage)
            throws
                NoSuchMethodException,
                IllegalAccessException,
                InvocationTargetException,
                InstantiationException{

        Reflections reflections = new Reflections(commandsPackage);
        for (Class<? extends CommandFunction> c : reflections.getSubTypesOf(CommandFunction.class)) {
            Command commandAnnotation = c.getAnnotation(Command.class);
            if (commandAnnotation != null) {
                CommandFunction commandFunction = c.getDeclaredConstructor().newInstance();
                addCommand(commandAnnotation.command(), commandFunction);
            }
        }
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getAuthor().getIdLong() == botId)
            return;
        String input = event.getMessage().getContentRaw()
                .trim()
                .replaceAll(" +", " ");
        if (!input.startsWith(commandPrefix))
            return;
        int p = input.indexOf(" ");
        String commandString = (p == -1) ? input.substring(1) : input.substring(commandPrefix.length(), p);
        if (!commands.containsKey(commandString))
            return;
        CommandFunction command = commands.get(commandString);
        String arg = (p == -1) ? "" : input.substring(p + 1);
        if (command.performCommand(arg, event.getAuthor(), event.getChannel()))
            try {
                event.getMessage().delete().queue();
            } catch (IllegalStateException | InsufficientPermissionException e) {
                LOGGER.finer("Couldn't delete a Message.");
            }
    }
}