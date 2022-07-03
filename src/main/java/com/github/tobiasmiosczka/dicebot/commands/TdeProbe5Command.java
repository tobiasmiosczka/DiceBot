package com.github.tobiasmiosczka.dicebot.commands;

import com.github.tobiasmiosczka.dicebot.discord.command.CommandFunction;
import com.github.tobiasmiosczka.dicebot.discord.command.documentation.Command;
import com.github.tobiasmiosczka.dicebot.model.Dice;
import com.github.tobiasmiosczka.dicebot.model.Roll;
import com.github.tobiasmiosczka.dicebot.parsing.DiceNotationParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

@Command(
        command = "p",
        description = "Performs a probe according to the rules of TDE5."
)
public class TdeProbe5Command implements CommandFunction {

    private static boolean isCriticalHit(Roll[] rolls) {
        return ((rolls[0].roll() == 1 && rolls[1].roll() == 1) ||
                (rolls[1].roll() == 1 && rolls[2].roll() == 1) ||
                (rolls[0].roll() == 1 && rolls[2].roll() == 1));
    }

    private static boolean isCriticalMiss(Roll[] rolls) {
        return ((rolls[0].roll() == 20 && rolls[1].roll() == 20) ||
                (rolls[1].roll() == 20 && rolls[2].roll() == 20) ||
                (rolls[0].roll() == 20 && rolls[2].roll() == 20));
    }

    @Override
    public ReplyCallbackAction performCommand(SlashCommandInteractionEvent event) {
        Roll[] rolls = new Dice(20).roll(3);
        return event.reply(
                event.getUser().getAsMention()
                        + ": " + DiceNotationParser.rollsToString(rolls)
                        + (isCriticalHit(rolls) ? " Critical hit!:partying_face: " : "")
                        + (isCriticalMiss(rolls) ? " Critical miss!:see_no_evil: " : ""));
    }
}
