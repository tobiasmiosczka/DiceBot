package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.Command;
import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.reflection.ReflectionUtil;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Command(command = "help", helpText = "Shows information about a specific command.")
public class HelpCommand implements CommandFunction {

    private final Map<String, String> helpTexts;
    private final String commands;

    public HelpCommand() {
        helpTexts = new HashMap<>();
        try {
            for (Class<? extends CommandFunction> c : ReflectionUtil.getClassesImplementing(this.getClass().getPackageName(), CommandFunction.class)) {
                Command commandAnnotation = c.getAnnotation(Command.class);
                if (commandAnnotation == null)
                    continue;
                helpTexts.put(commandAnnotation.command(), commandAnnotation.helpText());
            }
        } catch (Exception e) {
            //TODO: implement error handling
        }

        commands = helpTexts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> "`" + e.getKey() + "`: " + e.getValue() + "\n")
                .collect(Collectors.joining());
    }

    @Override
    public boolean performCommand(String arg, User author, MessageChannel messageChannel) {
        if (arg == null || arg.isEmpty()) {
            messageChannel.sendMessage(commands).queue();
            return true;
        }

        if (!helpTexts.containsKey(arg)) {
            messageChannel.sendMessage("There is no command `" + arg + "`").queue();
            return false;
        }

        String helpText = helpTexts.get(arg);
        messageChannel.sendMessage(author.getAsMention() + ": `" + arg + "`: " + helpText).queue();
        return true;
    }
}
