package com.github.tobiasmiosczka.dicebot.discord.command.documentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    String command();
    String description() default "";
    Option[] arguments() default {};
    boolean guildOnly() default false;
}