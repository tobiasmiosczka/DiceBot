module DiceBot {
    requires java.desktop;
    requires java.logging;
    requires java.scripting;

    requires org.reflections;

    requires net.dv8tion.jda;

    exports com.github.tobiasmiosczka.dicebot;
    opens com.github.tobiasmiosczka.dicebot.commands;
    opens com.github.tobiasmiosczka.dicebot.util;
}