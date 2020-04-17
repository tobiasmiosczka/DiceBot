package com.github.tobiasmiosczka.dicebot;

import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

public class RollStatsManager {

    private final Map<Long, int[]> rollStats;

    public RollStatsManager() {
        rollStats = new HashMap<>();
    }

    public int[] getStatsByUser(User user) {
        if (!rollStats.containsKey(user.getIdLong()))
            rollStats.put(user.getIdLong(), new int[20]);
        return rollStats.get(user.getIdLong());
    }

    public void addToRollStats(User user, Roll roll) {
        ++getStatsByUser(user)[roll.getRoll() - 1];
    }

    public void addToRollStats(User user, Roll...rolls) {
        for (Roll roll : rolls)
            addToRollStats(user, roll);
    }

}
