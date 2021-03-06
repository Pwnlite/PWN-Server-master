package com.arlania.world.content.wheel;

import com.arlania.model.Item;


import java.security.SecureRandom;
import java.util.Random;

public class WheelOfFortuneGame {

    private final int[] items;
    private final int winningIndex;

    public WheelOfFortuneGame(int[] items) {
        this.items = items;
        this.winningIndex = new SecureRandom().nextInt(items.length);
    }

    public int[] getItems() {
        return items;
    }

    public int getWinningIndex() {
        return winningIndex;
    }

    public Item getReward() {
        return new Item(items[winningIndex]);
    }
}
