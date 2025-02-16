package io.github.metereel.api;

public enum Enhancement {
    NORMAL("Card Normal"),
    BONUS("Card Bonus"),
    MULT("Card Mult"),
    LUCKY("Card Lucky"),
    GLASS("Card Glass"),
    STEEL("Card Steel"),
    GOLD("Card Gold"),
    WILD("Card Wild");

    private final String cardType;

    Enhancement(String cardType) {
        this.cardType = cardType;
    }

    public String getCardType() {
        return this.cardType;
    }
}
