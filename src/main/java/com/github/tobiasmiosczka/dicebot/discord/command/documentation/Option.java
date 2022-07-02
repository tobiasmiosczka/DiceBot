package com.github.tobiasmiosczka.dicebot.discord.command.documentation;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Option {
    String name();
    OptionType type();
    boolean isRequired() default true;
    String description() default "";
}
