package com.github.tobiasmiosczka.dicebot.discord.command;

import com.github.tobiasmiosczka.dicebot.reflection.ReflectionUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CommandEngine extends ListenerAdapter {

    private static final String COMMAND_PREFIX = "/";
    private final long SELF_USER_ID_LONG;
    private final Map<String, CommandFunction> commands;

    public CommandEngine(String commandsPackage, JDA jda) {
        commands = new HashMap<>();
        this.SELF_USER_ID_LONG = jda.getSelfUser().getIdLong();
        try {
            addCommands(commandsPackage);
        } catch (IllegalAccessException | IOException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void addCommand(String commandString, CommandFunction commandFunction) {
        if (commands.containsKey(commandString))
            throw new DuplicateCommandException(commandFunction.getClass(), commandString);
        commands.put(commandString, commandFunction);
    }

    private void addCommands(String commandsPackage) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        for (Class<? extends CommandFunction> c : ReflectionUtil.getClassesImplementing(commandsPackage, CommandFunction.class)) {
            Command commandAnnotation = c.getAnnotation(Command.class);
            if (commandAnnotation == null)
                continue;
            CommandFunction command = c.getDeclaredConstructor().newInstance();
            addCommand(commandAnnotation.command(), command);
        }
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        String input = event.getMessage().getContentRaw()
                .trim()
                .replaceAll(" +", " ");

        if (!input.startsWith(COMMAND_PREFIX))
            return;

        if (event.getAuthor().getIdLong() == SELF_USER_ID_LONG)
            return;

        input = input.substring(COMMAND_PREFIX.length());

        int p = input.indexOf(" ");
        String commandString = (p == -1) ? input : input.substring(0, p);
        String arg = (p == -1) ? "" : input.substring(p + 1);

        CommandFunction command = commands.get(commandString);
        if (command == null)
            return;

        if (command.performCommand(arg, event.getAuthor(), event.getChannel()))
            event.getMessage().delete().queue();
    }
}
