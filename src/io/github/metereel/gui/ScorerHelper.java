package io.github.metereel.gui;

import io.github.metereel.Timer;
import io.github.metereel.api.ScorePopup;
import io.github.metereel.card.Card;
import io.github.metereel.card.CardState;
import io.github.metereel.card.PlayingCard;
import io.github.metereel.card.jokers.JokerCard;
import org.apfloat.Apfloat;
import processing.core.PVector;

import java.util.ArrayList;

import static io.github.metereel.Constants.CARD_HEIGHT;
import static io.github.metereel.Constants.HUD;
import static io.github.metereel.Game.jokers;
import static io.github.metereel.Helper.activeCards;
import static io.github.metereel.Javatro.APP;
import static io.github.metereel.card.Deck.AXIS;

public class ScorerHelper {

    public static Apfloat blindMult = new Apfloat("1.6");
    private static int CYCLE_TIME = 40;
    public static ArrayList<PlayingCard> playedHand;
    public static boolean currentlyPlayingHand;
    public static final Timer playingTimer = new Timer();

    private static ScorePopup scoreDisplay;
    private static boolean willEnd;

    public static void scoreHand() {
        playedHand.forEach(Card::tick);

        playingTimer.incrementTimer();
        if (scoreDisplay != null) {
            scoreDisplay.display();
        }

        if (playingTimer.getTimeWithCycle(CYCLE_TIME * 4) == 0){
            if (willEnd){
                jokers.forEach(jokerCard -> jokerCard.setTriggered(false));
                playedHand = null;
                HUD.blindWon();
                return;
            }
        }

        if (playingTimer.getTimeWithCycle(CYCLE_TIME) == CYCLE_TIME / 2){
            jokers.forEach(JokerCard::updateTrigger);
            playedHand.forEach(card -> card.setTriggered(false));
        }

        if (playingTimer.getTimeWithCycle(CYCLE_TIME) == 0) {

            ArrayList<PlayingCard> activeCards = new ArrayList<>(playedHand);
            activeCards.removeIf(playingCard -> !playingCard.isSelected());

            if (activeCards.isEmpty() || activeCards.getLast().finishedTriggering()){
                playedHand.forEach(card -> card.setSelected(false));
                currentlyPlayingHand = false;
                HUD.getDeck().discard(playedHand);
                scoreDisplay = null;

                if (HUD.getScorer().hasWon()){
                    if (!willEnd)
                        playingTimer.resetTimer();
                    willEnd = true;
                    HUD.getDeck().clearHand();
                } else {
                    playedHand = null;
                }
            } else {
                for (PlayingCard card : playedHand) {
                    if (!card.isSelected()) continue;
                    if (card.finishedTriggering()) continue;

                    while (card.getCurrentTrigger().hasNoEffect()){
                        card.skipTrigger();
                        if (card.finishedTriggering()) break;
                    }
                    if (card.finishedTriggering()) continue;

                    float x = card.getPos().x;
                    float y = card.getPos().y - CARD_HEIGHT * 2;

                    scoreDisplay = new ScorePopup(new PVector(x, y), 0.0f, card.getCurrentTrigger());
                    card.tryTrigger(HUD.getScorer());

                    jokers.forEach(jokerCard -> {
                        boolean triggered = jokerCard.onCardTrigger(card, activeCards);
                        jokerCard.setNextTrigger(triggered);
                    });

                    CYCLE_TIME = Math.max(2, CYCLE_TIME - (Math.random() > 0.9 ? 1 : 0));
                    break;
                }
            }
        }
    }

    public static void startScoring(ArrayList<PlayingCard> playingCards) {
        playedHand = playingCards;
        currentlyPlayingHand = true;
        playingTimer.resetTimer();
        CYCLE_TIME = 40;

        jokers.forEach(jokerCard -> {
            boolean triggered = jokerCard.onHandPlayed();
            jokerCard.setTriggered(triggered);
        });

        willEnd = false;
        ArrayList<PlayingCard> activeCards = activeCards(playingCards);
        for (PlayingCard card : playedHand){
            card.setSelected(false);
            card.setState(CardState.PLAYING);

            float x = (playingCards.indexOf(card) - playingCards.size() * 0.5f) * APP.width * 0.1f + AXIS;
            card.setTargetPos(x, APP.height * .4f, 25, true);

            if (activeCards.contains(card)){
                card.setSelected(true);
                card.resetTrigger();
            }
        }
    }
}
