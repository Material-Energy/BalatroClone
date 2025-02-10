package io.github.metereel.card;

public enum HandType {
    HIGH_CARD(5, 1, 10, 1, "High Card"),
    PAIR(10, 2, 15, 1, "Pair"),
    TWO_PAIR(20, 2, 20, 1, "Two Pair"),
    THREE_OF_A_KIND(30, 3, 20, 2, "Three of a Kind"),
    STRAIGHT(30, 4, 30, 3, "Straight"),
    FLUSH(35, 4, 15, 2, "Flush"),
    FULL_HOUSE(40, 4, 25, 2, "Full House"),
    FOUR_OF_A_KIND(60, 7, 30, 3, "Four of a Kind"),
    STRAIGHT_FLUSH(100, 8, 40, 4, "Straight Flush"),

    FIVE_OF_A_KIND(120, 12, 35, 3, "Five of a Kind"),
    FLUSH_HOUSE(140, 14, 40, 4, "Flush House"),
    FLUSH_FIVE(160, 16, 50, 3, "Flush Five");

    private final int baseChips;
    private final int baseMult;
    private final int scalingChips;
    private final int scalingMult;

    private final String name;

    HandType(int chips, int mult, int sChips, int sMult, String name) {
        baseChips = chips;
        baseMult = mult;

        scalingChips = sChips;
        scalingMult = sMult;

        this.name = name;
    }

    public int getChips(int level){
        return this.baseChips + this.scalingChips * (level - 1);
    }

    public int getMult(int level){
        return this.baseMult + this.scalingMult * (level - 1);
    }

    @Override
    public String toString() {
        return name;
    }
}
