package io.github.metereel.gui;

import io.github.metereel.Game;
import io.github.metereel.Timer;
import io.github.metereel.api.ScorePopup;
import io.github.metereel.card.Card;
import io.github.metereel.card.CardState;
import io.github.metereel.card.PlayingCard;
import processing.core.PVector;

import java.util.ArrayList;

import static io.github.metereel.Constants.CARD_HEIGHT;
import static io.github.metereel.Constants.HUD;
import static io.github.metereel.Helper.activeCards;
import static io.github.metereel.Main.APP;
import static io.github.metereel.card.Deck.AXIS;

public class ScorerHelper {

    private static int CYCLE_TIME = 20;
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
                playedHand = null;
                HUD.getDeck().clearHand();
                HUD.blindWon();
                return;
            }
        }

        if (playingTimer.getTimeWithCycle(CYCLE_TIME) == CYCLE_TIME / 2){
            playedHand.forEach(card -> card.setIgnore(false));
        }

        if (playingTimer.getTimeWithCycle(CYCLE_TIME) == 0) {
            ArrayList<PlayingCard> temp = new ArrayList<>(playedHand);
            temp.removeIf(playingCard -> !playingCard.isSelected());

            if (temp.isEmpty() || temp.getLast().finishedTriggering()){
                playedHand.forEach(card -> card.setSelected(false));
                currentlyPlayingHand = false;
                HUD.getDeck().discard(playedHand);
                scoreDisplay = null;

                if (HUD.getScorer().hasWon())
                    willEnd = true;
            } else {
                for (PlayingCard card : playedHand) {
                    if (!card.isSelected()) continue;
                    if (card.finishedTriggering()) continue;

                    while (card.getCurrentTrigger().hasNoEffect()){
                        card.skipTrigger();
                        if (card.finishedTriggering()) break;
                    }
                    if (card.finishedTriggering()) continue;

                    float x = (playedHand.indexOf(card) - playedHand.size() * 0.5f) * APP.width * 0.1f + AXIS;
                    float y = APP.height * .5f - CARD_HEIGHT * 2;

                    scoreDisplay = new ScorePopup(new PVector(x, y), 0.0f, card.getCurrentTrigger());
                    card.tryTrigger(HUD.getScorer());

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
        CYCLE_TIME = 20;

        willEnd = false;
        ArrayList<PlayingCard> activeCards = activeCards(playingCards);
        for (PlayingCard card : playedHand){
            card.setSelected(false);
            card.setState(CardState.PLAYING);

            float x = (playingCards.indexOf(card) - playingCards.size() * 0.5f) * APP.width * 0.1f + AXIS;
            card.setTargetPos(x, APP.height * .5f, 10);

            if (activeCards.contains(card)){
                card.setSelected(true);
                card.resetTrigger();
            }
        }
    }
}
