package io.github.metereel.card.jokers;

import io.github.metereel.card.Card;
import io.github.metereel.card.PlayingCard;
import io.github.metereel.gui.Text;

import java.util.ArrayList;

import static io.github.metereel.Constants.JOKERS;

public abstract class JokerCard extends Card {
    private final String jokerType;
    protected int charge = 0;
    protected int maxCharge;
    private boolean nextTrigger;

    public JokerCard(Text name, String jokerType, int maxCharge) {
        super(name);
        this.jokerType = jokerType;
        this.maxCharge = maxCharge;
    }

    public abstract void addTriggers();
    public abstract boolean onHandPlayed();
    public abstract boolean onCardTrigger(PlayingCard card, ArrayList<PlayingCard> playedCards);
    public abstract JokerCard copy();

    @Override
    public void updateSprite() {
        this.cardFront = JOKERS.getSprite(jokerType);
        super.updateSprite();
    }

    public String getJokerType() {
        return jokerType;
    }

    public void setNextTrigger(boolean triggered) {
        this.nextTrigger = triggered;
    }

    public void updateTrigger(){
        this.setTriggered(this.nextTrigger);
        this.nextTrigger = false;
    }
}
