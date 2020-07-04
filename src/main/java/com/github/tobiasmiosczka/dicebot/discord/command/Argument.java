package com.github.tobiasmiosczka.dicebot.discord.command;

public @interface Argument {
    String name();
    boolean isOptional() default false;
    String description() default "";
}
