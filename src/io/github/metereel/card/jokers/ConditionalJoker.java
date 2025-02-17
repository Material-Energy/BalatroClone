package io.github.metereel.card.jokers;

import io.github.metereel.api.Trigger;
import io.github.metereel.card.PlayingCard;
import io.github.metereel.gui.Text;

import java.util.ArrayList;

public class ConditionalJoker extends JokerCard{
    public ConditionalJoker(Text name, String jokerType) {
        super(name, jokerType, 0);
    }

    public ConditionalJoker(Text name, String jokerType, Trigger trigger) {
        super(name, jokerType, 0);
    }

    @Override
    public void addTriggers() {

    }

    @Override
    public boolean onHandPlayed() {
        return false;
    }

    @Override
    public boolean onCardTrigger(PlayingCard card, ArrayList<PlayingCard> playedCards) {
        return false;
    }

    @Override
    public JokerCard copy() {
        return new ConditionalJoker(
                getName(),
                getJokerType(),
                trigger.copy()
        );
    }
}
