package com.github.tobiasmiosczka.dicebot.discord.command;

import com.github.tobiasmiosczka.dicebot.reflection.ReflectionUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CommandEngine extends ListenerAdapter {

    private static final String COMMAND_PREFIX = "!";
    private final Map<String, CommandFunction> commands = new HashMap<>();

    public CommandEngine(String commandsPackage) {
        try {
            addCommands(commandsPackage);
        } catch (IllegalAccessException | IOException | NoSuchMethodException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addCommand(String commandString, CommandFunction commandFunction) {
        if (commands.containsKey(commandString))
            throw new DuplicateCommandException(commandFunction.getClass(), commandString);
        commands.put(commandString, commandFunction);
    }

    private void addCommands(String commandsPackage) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        Class[] classes = ReflectionUtil.getClasses(commandsPackage);
        for (Class c : classes) {
            if (!CommandFunction.class.isAssignableFrom(c))
                continue;
            System.out.println(c.getName());
            Command commandAnnotation = (Command)c.getAnnotation(Command.class);
            if (commandAnnotation == null)
                continue;
            CommandFunction command = (CommandFunction)c.getDeclaredConstructor().newInstance();
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

        input = input.substring(COMMAND_PREFIX.length());

        int p = input.indexOf(" ");
        String commandString = (p == -1) ? input : input.substring(0, p);
        String arg = (p == -1) ? "" : input.substring(p + 1);

        System.out.println(commandString + " | " + arg);

        CommandFunction command = commands.get(commandString);
        if (command == null)
            return;

        if (command.performCommand(arg, event.getAuthor(), event.getChannel()))
            event.getMessage().delete().queue();
    }
}
