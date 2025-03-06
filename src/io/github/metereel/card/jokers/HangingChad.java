package io.github.metereel.card.jokers;

import io.github.metereel.card.PlayingCard;
import io.github.metereel.gui.ScorerHelper;
import io.github.metereel.gui.Text;

import java.util.ArrayList;

public class HangingChad extends JokerCard {
    public HangingChad() {
        super(new Text("Hanging Chad"), "Hanging Chad", 2);
    }

    @Override
    public void onHandPlayed() {
    }

    @Override
    public boolean onCardTrigger(PlayingCard card, ArrayList<PlayingCard> playedCards) {
        if (charge <= 0) return false;
        if (playedCards.indexOf(card) != 0) return false;

        card.retrigger();
        this.triggerText = "Again!";
        charge--;
        return true;
    }

    @Override
    public void postTrigger() {

    }

    @Override
    public JokerCard copy() {
        return new HangingChad();
    }

    @Override
    public void resetTrigger() {
        super.resetTrigger();
        this.charge = maxCharge;
    }

    @Override
    public boolean cannotTrigger(ScorerHelper.Stage playedStage) {
        return super.cannotTrigger(playedStage) && (playedStage != ScorerHelper.Stage.CARD_JOKER || charge <= 0);
    }

    @Override
    public boolean finishedTriggering() {
        return true;
    }
}
