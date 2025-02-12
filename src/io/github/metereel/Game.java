package io.github.metereel;

import io.github.metereel.card.Card;
import io.github.metereel.card.CardState;
import io.github.metereel.card.PlayingCard;

import java.util.ArrayList;

import static io.github.metereel.Constants.HUD;
import static io.github.metereel.Helper.activeCards;
import static io.github.metereel.Main.APP;
import static io.github.metereel.card.Deck.AXIS;

public class Game {
    public static boolean inBlind = false;

    public static ArrayList<PlayingCard> playedHand;
    public static boolean currentlyPlayingHand;
    public static final Timer playingTimer = new Timer();

    public static void tick() {
        HUD.display();
        if (inBlind){
            HUD.getDeck().tick();
            HUD.gameTick();
        }
    }

    public static void scoreHand() {
        playedHand.forEach(Card::tick);

        playingTimer.incrementTimer();
        if (playingTimer.getTimeWithCycle(20) == 10){
            playedHand.forEach(card -> card.setIgnore(false));
        }

        if (playingTimer.getTimeWithCycle(20) == 0) {
            ArrayList<PlayingCard> temp = new ArrayList<>(playedHand);
            temp.removeIf(playingCard -> !playingCard.isSelected());

            if (temp.getLast().finishedTriggering()){
                currentlyPlayingHand = false;
                HUD.getDeck().discard(playedHand);
                playedHand = null;
            } else {
                for (PlayingCard card : playedHand) {
                    if (!card.isSelected()) continue;
                    if (card.finishedTriggering()) continue;
                    card.tryTrigger(HUD.getScorer());
                    break;
                }
            }
        }
    }

    public static void startScoring(ArrayList<PlayingCard> playingCards) {
        playedHand = playingCards;
        currentlyPlayingHand = true;
        playingTimer.resetTimer();

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
