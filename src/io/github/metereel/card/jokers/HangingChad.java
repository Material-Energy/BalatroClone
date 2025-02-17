package io.github.metereel.card.jokers;

import io.github.metereel.card.PlayingCard;
import io.github.metereel.gui.Text;

import java.util.ArrayList;

public class HangingChad extends JokerCard {
    public HangingChad() {
        super(new Text("Hanging Chad"), "Hanging Chad", 2);
    }

    @Override
    public void addTriggers() {}

    @Override
    public boolean onHandPlayed() {
        this.charge = maxCharge;
        return false;
    }

    @Override
    public boolean onCardTrigger(PlayingCard card, ArrayList<PlayingCard> playedCards) {
        if (charge <= 0) return false;
        if (playedCards.indexOf(card) != 0) return false;

        card.retrigger();
        charge--;
        return true;
    }

    @Override
    public JokerCard copy() {
        return new HangingChad();
    }

}
