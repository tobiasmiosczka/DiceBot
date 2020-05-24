package com.github.tobiasmiosczka.dicebot.discord.command;

public class DuplicateCommandException extends RuntimeException {

    DuplicateCommandException(Class<?> commandClass, String commandString) {
        super("Command must be unique. Command " + commandString + " already exists: " + commandClass.getName());
    }

}
