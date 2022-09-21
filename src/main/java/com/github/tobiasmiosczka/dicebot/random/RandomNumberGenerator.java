package com.github.tobiasmiosczka.dicebot.random;

import java.util.*;

public abstract class RandomNumberGenerator {

    abstract int nextInteger(int rangeFrom, int rangeToExclusive);

    abstract int nextInteger(int rangeToExclusive);

    public <T> T randomOf(List<T> list) {
        return list.get(nextInteger(list.size()));
    }

    public <T> T randomOf(T[] array) {
        return array[nextInteger(array.length)];
    }

    public <T> List<T> shuffled(Collection<T> list) {
        List<T> shuffled = new ArrayList<>(list);
        shuffle(shuffled);
        return shuffled;
    }

    public <T> void shuffle(List<T> list) {
        Object[] arr = list.toArray();
        for (int i = list.size(); i > 1; i--) {
            int j = nextInteger(i);
            Object tmp = arr[i - 1];
            arr[i - 1] = arr[j];
            arr[j] = tmp;
        }
        ListIterator it = list.listIterator();
        for (Object e : arr) {
            it.next();
            it.set(e);
        }
    }
}
